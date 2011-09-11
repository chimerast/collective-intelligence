import java.net.URL
import scala.collection.JavaConversions._
import scala.collection.mutable.Map
import com.sun.syndication.feed.synd.SyndEntry
import com.sun.syndication.io.SyndFeedInput
import com.sun.syndication.io.XmlReader
import com.sun.syndication.feed.synd.SyndContent

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
