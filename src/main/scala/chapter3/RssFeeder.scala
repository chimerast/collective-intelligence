package chapter3

import scala.collection.JavaConversions._
import scala.collection.mutable.Map

import java.net._

import com.sun.syndication.feed.synd._
import com.sun.syndication.io._
import util._

object RssFeeder extends App {
  def getWordCounts(url: String): (String, Map[String, Int]) = {
    val rss = new SyndFeedInput().build(new XmlReader(new URL(url)))
    val wc = Map[String, Int]().withDefaultValue(0)

    rss.getEntries.foreach {
      case e: SyndEntry =>
        val summary = if (e.getContents.size > 0) {
          e.getContents.get(0) match {
            case c: SyndContent => c.getValue
          }
        } else {
          e.getDescription.getValue
        }
        for (word <- getWords(e.getTitle + " " + summary)) {
          wc(word) += 1
        }
    }
    (rss.getTitle, wc)
  }

  private def getWords(str: String): List[String] = {
    Igo.detectNoun(str.replaceAll("<[^>]+>", ""))
  }
}
