package chapter3

import java.net._
import util._
import util.HtmlScraper._
import net.htmlparser.jericho._

object Nikkei225 extends App {
  val html = HtmlScraper("http://www3.nikkei.co.jp/nkave/about/225_list.cfm")
  val xpath = "//tr[@bgcolor='#8A4500' or @bgcolor='#FFF5DE']/td[1]/span[1]/text()"

  html.eval(xpath).map(_.toString.toInt).sortBy(i => i).foreach(println)

}
