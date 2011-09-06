import scala.collection.JavaConversions._
import scala.io._
import scala.util.parsing.json._

import java.net.URL
import java.{ util => ju }

object Delicious {
  import Utils._

  val DLCS_RSS = "http://feeds.delicious.com/v2/json/"

  def getRss(tag: String = "", popular: Boolean = false, url: String = "", user: String = "", count: Int = 30): List[Map[String, String]] = {
    val rssurl =
      (if (url != "") {
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
      }) + "?count=" + count

    val json = using(Source.fromURL(rssurl, "UTF-8")) { source =>
      JSON.parseFull(source.mkString)
    }

    json match {
      case Some(list: List[Map[String, AnyRef]]) =>
        list.map { map =>
          map.map {
            case (key, value: String) =>
              (key, value)
            case (key, value: List[String]) =>
              (key, value.mkString("[", ", ", "]"))
          }
        }
      case _ =>
        throw new IllegalStateException()
    }
  }

  def getPopular(tag: String = "", count: Int = 30): List[Map[String, String]] = getRss(tag = tag, popular = true, count = count)
  def getUrlPosts(url: String, count: Int = 30): List[Map[String, String]] = getRss(url = url, count = count)
  def getUserPosts(user: String, count: Int = 30): List[Map[String, String]] = getRss(user = user, count = count)
}
