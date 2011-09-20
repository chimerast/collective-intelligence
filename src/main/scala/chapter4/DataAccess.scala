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

class DataAccess(dburl: String) {
  val db = Database.forURL(dburl, driver = "org.h2.Driver")

  private val scopeIdentity = SimpleFunction.nullary[Int]("scope_identity")

  def createIndexTables(): Unit = {
    (URLList.ddl ++
      WordList.ddl ++
      WordLocation.ddl ++
      Link.ddl ++
      LinkWords.ddl).create
  }

  def getUrlId(url: String): Int = {
    val q = for (u <- URLList if u.url === url) yield u.id
    q.firstOption match {
      case Some(id) =>
        id
      case None =>
        URLList.url insert (url)
        Query(scopeIdentity).first
    }
  }

  def getWordId(word: String): Int = {
    val q = for (w <- WordList if w.word === word) yield w.id
    q.firstOption match {
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
}

object URLList extends Table[(Int, String)]("urllist") {
  def id = column[Int]("id", O AutoInc, O PrimaryKey)
  def url = column[String]("url")
  def * = id ~ url
}

object WordList extends Table[(Int, String)]("wordlist") {
  def id = column[Int]("id", O AutoInc, O PrimaryKey)
  def word = column[String]("word")
  def * = id ~ word
}

object WordLocation extends Table[(Int, Int, Int)]("wordlocation") {
  def urlId = column[Int]("url_id")
  def wordId = column[Int]("word_id")
  def location = column[Int]("location")
  def * = urlId ~ wordId ~ location

  def urlList = foreignKey("fk_wordlication_url_id", urlId, URLList)(_.id)
  def wordList = foreignKey("fk_wordlication_word_id", wordId, WordList)(_.id)
}

object Link extends Table[(Int, Int, Int)]("link") {
  def id = column[Int]("id", O AutoInc, O PrimaryKey)
  def fromId = column[Int]("from_id")
  def toId = column[Int]("to_id")
  def * = id ~ fromId ~ toId

  def fromUrlList = foreignKey("fk_link_from_id", fromId, URLList)(_.id)
  def toUrlList = foreignKey("fk_link_to_id", toId, URLList)(_.id)
}

object LinkWords extends Table[(Int, Int)]("linkwords") {
  def wordId = column[Int]("word_id")
  def linkId = column[Int]("link_id")
  def * = wordId ~ linkId

  def wordList = foreignKey("fk_linkwords_word_id", wordId, WordList)(_.id)
  def link = foreignKey("fk_linkwords_link_id", linkId, Link)(_.id)
}
