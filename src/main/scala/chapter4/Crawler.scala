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

object Crawler extends App {
  // val dburl = "jdbc:h2:mem:?ignorecase=true"
  val dburl = "jdbc:h2:/data/h2/searchindex"

  // new DataAccess(dburl).createIndexTables

  val crawler = new Crawler(dburl)
  crawler.dao.db withSession {
    //crawler.dao.createIndexTables
    //println(crawler.dao.getUrlId("http://chimera.st/"))
    crawler.crawl(List("http://kiwitobes.com/wiki/Perl.html"))
  }
  //.crawl(List("http://kiwitobes.com/wiki/Perl.html"))
}

class Crawler(dburl: String) {
  protected val logger = LoggerFactory.getLogger(getClass)

  implicit val codec = Codec.string2codec("UTF-8")

  val dao = new DataAccess(dburl)

  def addToIndex(url: String, segment: Segment): Unit = {
    if (isIndexed(url)) return

    println("Indexing %s".format(url))

    // 個々の単語を取得する
    val text = getTextOnly(segment)
    val words = Igo.parse(text)

    // URL idを取得する
    val urlId = dao.getUrlId(url)

    // それぞれの単語と、このurlのリンク
    words.zipWithIndex.collect {
      case (word, i) if word._2 == "名詞" =>
        val wordId = dao.getWordId(word._1)
        dao.insertWordLocation(urlId, wordId, i)
        println(word._1)
    }
  }

  def getTextOnly(segment: Segment): String = {
    segment.getTextExtractor.toString
  }

  def isIndexed(url: String): Boolean = {
    false
  }

  def addLinkRef(urlFrom: String, urlTo: String, linkText: String): Unit = {
  }

  def crawl(pages: List[String], depth: Int = 2): Unit = {
    if (depth == 0)
      return

    val newpages = pages.flatMap { page =>
      try {
        val uri = new URI(page)
        val html = HtmlScraper(page)

        addToIndex(page, html)

        html.eval("//a/@href").collect {
          case attr: Attribute =>
            val url = uri.resolve(attr.getValue).toASCIIString.split("#")(0)
            if (url.startsWith("http") && !isIndexed(url)) {
              val linkText = getTextOnly(attr.getStartTag.getElement)
              addLinkRef(page, url, linkText)
              Some(url)
            } else {
              None
            }
        }.flatten
      } catch {
        case e =>
          logger.warn("Could not open %s".format(page))
          List()
      }
    }

    crawl(newpages, depth - 1)
  }
}

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
    val ret = q.list
    if (!ret.isEmpty) {
      ret.head
    } else {
      URLList.url insert (url)
      Query(scopeIdentity).first
    }
  }

  def getWordId(word: String): Int = {
    val q = for (w <- WordList if w.word === word) yield w.id
    val ret = q.list
    if (!ret.isEmpty) {
      ret.head
    } else {
      WordList.word insert (word)
      Query(scopeIdentity).first
    }
  }

  def insertWordLocation(urlId: Int, wordId: Int, location: Int): Unit = {
    WordLocation.insert(urlId, wordId, location)
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
