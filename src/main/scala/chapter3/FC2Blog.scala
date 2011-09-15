package chapter3

import java.net._
import util._
import util.HtmlScraper._
import net.htmlparser.jericho._

object FC2Blog extends App {
  val FC2 = """url=http://(.*)\.fc2\.com/""".r

  for (i <- 1 to 10) {
    val html = HtmlScraper("http://blogranking.fc2.com/rank/all_%d.html".format(i))

    html.eval("//div[@class='tit']/a/@href").collect {
      case attr: Attribute =>
        attr.getValue()
    }.filter(_.startsWith("http://")).map(_.split("&")(1)).map(URLDecoder.decode(_)).collect {
      case FC2(host) => host
    }.map("http://feeds.fc2.com/fc2/xml?host=" + _).foreach(println)
  }
}
