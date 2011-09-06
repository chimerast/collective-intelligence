import scala.collection.JavaConversions._

import java.net.URL
import java.security.MessageDigest
import java.{ util => ju }

import com.sun.syndication.io._
import com.sun.syndication.feed.synd._

object Delicious {
  val DLCS_RSS = "http://feeds.delicious.com/rss/"

  def getRss(tag: String = "", popular: Boolean = false, url: String = "", user: String = ""): List[Map[String, String]] = {
    val rssurl =
      if (url != "") {
        DLCS_RSS + "url/%s" format md5(url)
      } else if (user != "" && tag != "") {
        DLCS_RSS + "%s/%s" format (user, tag)
      } else if (user != "" && tag == "") {
        DLCS_RSS + "%s" format user
      } else if (!popular && tag == "") {
        DLCS_RSS + ""
      } else if (!popular && tag != "") {
        DLCS_RSS + "tag/%s" format tag
      } else if (popular && tag == "") {
        DLCS_RSS + "popular/"
      } else if (popular && tag != "") {
        DLCS_RSS + "popular/%s" format tag
      } else {
        ""
      }

    val rss = new SyndFeedInput().build(new XmlReader(new URL(rssurl)))

    rss.getEntries.asInstanceOf[ju.List[SyndEntry]].toList.map { e =>
      Map(
        "url" -> Option(e.getLink),
        "description" -> Option(e.getTitle),
        "tags" -> Option(e.getCategories.asInstanceOf[ju.List[SyndCategory]]).map(_.map(_.getName).mkString(",")),
        "dt" -> Option(e.getUpdatedDate).map(_.toGMTString),
        "extended" -> Option(e.getDescription).map(_.getValue),
        "user" -> Option(e.getAuthor))
        .flatMap { case (key, value) => value.map(key -> _) }
    }
  }

  def getPopular(tag: String = ""): List[Map[String, String]] = getRss(tag = tag, popular = true)
  def getUrlPosts(url: String): List[Map[String, String]] = getRss(url = url)
  def getUserPosts(user: String): List[Map[String, String]] = getRss(user = user)

  def md5(str: String): String = {
    val md5 = MessageDigest.getInstance("MD5")
    md5.reset()
    md5.update(str.getBytes("UTF-8"))

    md5.digest().map(0xFF & _).map("%02x".format(_)).mkString
  }
}
