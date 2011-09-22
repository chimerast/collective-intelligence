package chapter3

import java.net._
import java.sql._
import java.io._
import util._
import util.HtmlScraper._
import net.htmlparser.jericho._

import org.joda.time._

import org.scalaquery.session._
import org.scalaquery.session.Database.threadLocalSession

import org.scalaquery.ql._
import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql.extended.{ ExtendedTable => Table }
import org.scalaquery.ql.extended.MySQLDriver.Implicit._

object Nikkei225 extends App {
  val outfile = "nikkei.txt"

  val url = "http://www3.nikkei.co.jp/nkave/about/225_list.cfm"
  val xpath = "//tr[@bgcolor='#F0E7D1' or @bgcolor='#FFF5DE']/td[1]/span[1]/text()"

  val nikkei225 = DiskStore.load(HtmlScraper(url).eval(xpath).map(_.toString.toInt).sorted, url)

  def toSql(localDate: LocalDate) = new Date(localDate.toDate.getTime)

  val db = Database.forURL("jdbc:mysql://db/speeda?user=system&password=system", driver = "com.mysql.jdbc.Driver")

  val corpName = db withSession {
    val q = for (c <- CorpBasis) yield c.*
    q.list.toMap
  }

  val start = new LocalDate(2011, 4, 1)
  val end = new LocalDate(2011, 7, 1).minusDays(1)

  val map = (for (code <- nikkei225) yield {
    val prices = db withSession {
      val q = for (
        s <- SharePrice if s.corpId === code
          && s.marketId === SharePrice.TOSHO
          && s.chartType === SharePrice.DAILY
          && s.infoDate.between(toSql(start), toSql(end));
        _ <- Query orderBy s.infoDate
      ) yield s.infoDate ~ s.price

      q.list
    }

    val points = (prices zip prices.drop(1)).map {
      case ((_, prev), (date, curr)) =>
        (date -> (curr - prev) / prev * 100.0)
    }.toMap.withDefaultValue(0.0)

    (code -> points)
  }).toMap

  using(new PrintWriter(outfile, "UTF-8")) { out =>
    val days = map(9501).keys.toList.sortBy(_.getTime)

    out.print("Nikkei225")
    days.foreach(day => out.print("\t%s".format(day)))
    out.println()
    for ((code, points) <- map) {
      out.print(corpName(code))
      days.foreach(day => out.print("\t%.2f" format points(day)))
      out.println()
    }
  }
}

object CorpBasis extends Table[(Int, String)]("corp_bss") {
  def corpId = column[Int]("corp_id")
  def name = column[String]("corp_imfrml_name_vch")
  def * = corpId ~ name
}

object SharePrice extends Table[(Int, Int, Int, Date, Double)]("shr_price") {
  def corpId = column[Int]("corp_id")
  def marketId = column[Int]("mkt_id")
  def chartType = column[Int]("cndl_chart_typ")
  def infoDate = column[Date]("info_date")
  def price = column[Double]("close_price_num")
  def * = corpId ~ marketId ~ chartType ~ infoDate ~ price

  val TOSHO = 1
  val DAILY = 1
}
