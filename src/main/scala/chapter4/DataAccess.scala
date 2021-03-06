package chapter4

import scala.io._

import java.net.URI

import util.HtmlScraper._
import util._

import net.htmlparser.jericho.Attribute
import net.htmlparser.jericho.Segment

import org.slf4j.LoggerFactory

import org.scalaquery.session._
import org.scalaquery.session.Database.threadLocalSession

import org.scalaquery.ql._
import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql.extended.{ ExtendedTable => Table }
import org.scalaquery.ql.extended.H2Driver.Implicit._

object DataAccess {
  private val scopeIdentity = SimpleFunction.nullary[Int]("scope_identity")

  def createIndexTables(): Unit = {
    (URLList.ddl ++
      WordList.ddl ++
      WordLocation.ddl ++
      Link.ddl ++
      LinkWords.ddl ++
      PageRank.ddl ++
      HiddenNode.ddl ++
      WordHidden.ddl ++
      HiddenUrl.ddl).create
  }

  def getUrl(id: Int): String = {
    val q = for (u <- URLList if u.id === id) yield u.url
    q.first
  }

  def getOrCreateUrlId(url: String): Int = {
    val q = for (u <- URLList if u.url === url) yield u.id
    q.firstOption match {
      case Some(id) =>
        id
      case None =>
        URLList.url insert (url)
        Query(scopeIdentity).first
    }
  }

  def getWordId(word: String): Option[Int] = {
    val q = for (w <- WordList if w.word === word) yield w.id
    q.firstOption
  }

  def getOrCreateWordId(word: String): Int = {
    getWordId(word) match {
      case Some(id) =>
        id
      case None =>
        (WordList.word) insert (word)
        Query(scopeIdentity).first
    }
  }

  def insertWordLocation(urlId: Int, wordId: Int, location: Int): Unit = {
    WordLocation insert (urlId, wordId, location)
  }

  def insertLink(fromId: Int, toId: Int): Int = {
    (Link.fromId ~ Link.toId) insert (fromId, toId)
    Query(scopeIdentity).first
  }

  def insertLinkWords(linkId: Int, wordId: Int): Unit = {
    LinkWords insert (wordId, linkId)
  }

  def isIndexed(url: String): Boolean = {
    val q = for (u <- URLList if u.url === url) yield u.id
    val ret = q.firstOption.flatMap { urlId =>
      // URLが実際にクロールされているかどうかチェックする
      val q2 = for (wl <- WordLocation if wl.urlId === urlId) yield wl.*
      q2.firstOption
    }
    ret.isDefined
  }

  def countLinkToId(toId: Int): Int = {
    val q = for (l <- Link if l.toId === toId) yield l.id.count
    q.first
  }

  def initPageRank(): Unit = {
    PageRank.ddl.drop
    PageRank.ddl.create

    val q = for (u <- URLList) yield u.id ~ 1.0
    PageRank.insert(q)
  }

  def getUrlIdList(): List[Int] = {
    val q = for (u <- URLList) yield u.id
    q.list
  }

  def getLinker(toId: Int): List[Int] = {
    val q = for (l <- Link if l.toId === toId) yield l.fromId
    q.list.toSet.toList
  }

  def getPageRank(urlId: Int): Double = {
    val q = for (r <- PageRank if r.urlId === urlId) yield r.score
    q.first
  }

  def countLinkFromId(fromId: Int): Int = {
    val q = for (l <- Link if l.fromId === fromId) yield l.id.count
    q.first
  }

  def updatePageRank(urlId: Int, score: Double): Unit = {
    val q = for (r <- PageRank if r.urlId === urlId) yield r.score
    q.update(score)
  }

  def getLink(wordId: Int): List[(Int, Int)] = {
    val q = for (
      w <- LinkWords if w.wordId === wordId;
      l <- w.link
    ) yield l.fromId ~ l.toId
    q.list
  }

  def getStrengthOfWordToHidden(fromId: Int, toId: Int): Option[Double] = {
    val q = for (e <- WordHidden if e.fromId === fromId && e.toId === toId) yield e.strength
    q.firstOption
  }

  def getStrengthOfHiddenToUrl(fromId: Int, toId: Int): Option[Double] = {
    val q = for (e <- HiddenUrl if e.fromId === fromId && e.toId === toId) yield e.strength
    q.firstOption
  }

  def setStrengthOfWordToHidden(fromId: Int, toId: Int, strength: Double): Unit = {
    val q = for (e <- WordHidden if e.fromId === fromId && e.toId === toId) yield e.strength
    if (q.update(strength) == 0) WordHidden.insert(fromId, toId, strength)
  }

  def setStrengthOfHiddenToUrl(fromId: Int, toId: Int, strength: Double): Unit = {
    val q = for (e <- HiddenUrl if e.fromId === fromId && e.toId === toId) yield e.strength
    if (q.update(strength) == 0) HiddenUrl.insert(fromId, toId, strength)
  }

  def hasHiddenNode(createKey: String): Boolean = {
    val q = for (n <- HiddenNode if n.createKey === createKey) yield n.id
    q.firstOption.isDefined
  }

  def insertHiddenNode(createKey: String): Int = {
    (HiddenNode.createKey).insert(createKey)
    Query(scopeIdentity).first
  }

  def getHiddenIds(wordIds: Array[Int], urlIds: Array[Int]): Array[Int] = {
    val hidden1 = for (wordId <- wordIds) yield {
      val q = for (e <- WordHidden if e.fromId === wordId) yield e.toId
      q.list
    }
    val hidden2 = for (urlId <- urlIds) yield {
      val q = for (e <- HiddenUrl if e.toId === urlId) yield e.fromId
      q.list
    }
    (hidden1 ++ hidden2).flatten.toSet.toArray
  }
}

object URLList extends Table[(Int, String)]("urllist") {
  def id = column[Int]("id", O AutoInc, O PrimaryKey)
  def url = column[String]("url")
  def * = id ~ url

  def urlIndex = index("urllist_url_idx", url, true)
}

object WordList extends Table[(Int, String)]("wordlist") {
  def id = column[Int]("id", O AutoInc, O PrimaryKey)
  def word = column[String]("word")
  def * = id ~ word

  def wordIndex = index("wordlist_word_idx", word, true)
}

object WordLocation extends Table[(Int, Int, Int)]("wordlocation") {
  def urlId = column[Int]("url_id")
  def wordId = column[Int]("word_id")
  def location = column[Int]("location")
  def * = urlId ~ wordId ~ location

  def urlList = foreignKey("fk_wordlication_url_id", urlId, URLList)(_.id)
  def wordList = foreignKey("fk_wordlication_word_id", wordId, WordList)(_.id)

  def wordIdIndex = index("wordlocation_word_id_idx", wordId, false)
}

object Link extends Table[(Int, Int, Int)]("link") {
  def id = column[Int]("id", O AutoInc, O PrimaryKey)
  def fromId = column[Int]("from_id")
  def toId = column[Int]("to_id")
  def * = id ~ fromId ~ toId

  def fromUrlList = foreignKey("fk_link_from_id", fromId, URLList)(_.id)
  def toUrlList = foreignKey("fk_link_to_id", toId, URLList)(_.id)

  def fromIdIndex = index("link_from_id_idx", fromId, false)
  def toIdIndex = index("linf_to_id_idx", toId, false)
}

object LinkWords extends Table[(Int, Int)]("linkwords") {
  def wordId = column[Int]("word_id")
  def linkId = column[Int]("link_id")
  def * = wordId ~ linkId

  def wordList = foreignKey("fk_linkwords_word_id", wordId, WordList)(_.id)
  def link = foreignKey("fk_linkwords_link_id", linkId, Link)(_.id)
}

object PageRank extends Table[(Int, Double)]("pagerank") {
  def urlId = column[Int]("url_id", O PrimaryKey)
  def score = column[Double]("score")
  def * = urlId ~ score

  def urlList = foreignKey("fk_pagerank_url_id", urlId, URLList)(_.id)
}

object HiddenNode extends Table[(Int, String)]("hiddennode") {
  def id = column[Int]("id", O AutoInc, O PrimaryKey)
  def createKey = column[String]("craete_key")
  def * = id ~ createKey
}

object WordHidden extends Table[(Int, Int, Double)]("wordhidden") {
  def fromId = column[Int]("from_id")
  def toId = column[Int]("to_id")
  def strength = column[Double]("strength")
  def * = fromId ~ toId ~ strength
}

object HiddenUrl extends Table[(Int, Int, Double)]("hiddenurl") {
  def fromId = column[Int]("from_id")
  def toId = column[Int]("to_id")
  def strength = column[Double]("strength")
  def * = fromId ~ toId ~ strength
}
