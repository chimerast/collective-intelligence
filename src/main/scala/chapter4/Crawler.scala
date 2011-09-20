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
  val dburl = "jdbc:h2:/data/h2/searchindex"

  val crawler = new Crawler(dburl)
  crawler.dao.db withSession {
    // crawler.dao.createIndexTables
    crawler.crawl(List("http://kiwitobes.com/wiki/Categorical_list_of_programming_languages.html"))
  }
}

class Crawler(dburl: String) {
  protected val logger = LoggerFactory.getLogger(getClass)
  private val ignoreWords = Set("the", "of", "to", "and", "a", "in", "is", "it")

  val dao = new DataAccess(dburl)

  def addToIndex(url: String, segment: Segment): Unit = {
    if (isIndexed(url)) return

    println("Indexing %s" format url)

    // 個々の単語を取得する
    val text = getTextOnly(segment)
    val words = separateWords(text)

    // URL idを取得する
    val urlId = dao.getUrlId(url)

    // それぞれの単語と、このurlのリンク
    words.zipWithIndex.collect {
      case (word, i) if !ignoreWords.contains(word) =>
        val wordId = dao.getWordId(word)
        dao.insertWordLocation(urlId, wordId, i)
    }
  }

  def getTextOnly(segment: Segment): String = {
    segment.getTextExtractor.toString
  }

  def separateWords(text: String): List[String] = {
    text.split("\\W+").filter(""!=).map(_.toLowerCase).toList
  }

  def isIndexed(url: String): Boolean = {
    dao.isIndexed(url)
  }

  def addLinkRef(urlFrom: String, urlTo: String, linkText: String): Unit = {
    val words = separateWords(linkText)

    val fromId = dao.getUrlId(urlFrom)
    val toId = dao.getUrlId(urlTo)

    if (fromId == toId)
      return

    val linkId = dao.insertLink(fromId, toId)

    words.collect {
      case word if !ignoreWords.contains(word) =>
        val wordId = dao.getWordId(word)
        dao.insertLinkWords(linkId, wordId)
    }
  }

  def crawl(pages: List[String], depth: Int = 2): Unit = {
    if (depth == 0)
      return

    val newpages = pages.flatMap { page =>
      try {
        val html = HtmlScraper(page)
        val uri = new URI(page)

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
          logger.warn("Could not open %s" format page, e)
          List()
      }
    }

    crawl(newpages, depth - 1)
  }
}
