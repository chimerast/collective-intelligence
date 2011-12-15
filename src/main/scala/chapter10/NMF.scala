package chapter10

import scala.util.control.Breaks._

import java.net._
import java.sql.Date
import java.io._
import util._
import util.HtmlScraper._
import net.htmlparser.jericho._

import org.apache.commons.math.linear._

import org.joda.time._

import org.scalaquery.session.Database.threadLocalSession
import org.scalaquery.session._

import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql.extended.MySQLDriver.Implicit._
import org.scalaquery.ql.extended.{ ExtendedTable => Table }
import org.scalaquery.ql._

import org.joda.time._

object NMF extends App {
  val outfile = "nikkei.txt"

  val url = "http://www3.nikkei.co.jp/nkave/about/225_list.cfm"
  val xpath = "//tr[@bgcolor='#F0E7D1' or @bgcolor='#FFF5DE']/td[1]/span[1]/text()"

  val nikkei225 = DiskStore.load(HtmlScraper(url).eval(xpath).map(_.toString.toInt).sorted, url)

  def toSql(localDate: LocalDate) = new Date(localDate.toDate.getTime)

  val db = Database.forURL("jdbc:mysql://db/speeda?user=system&password=system", driver = "com.mysql.jdbc.Driver")

  val corps = db withSession {
    val q = for (
      c <- CorpBasis if c.listed === 1
        && c.corp === 1
        && c.tosho === 11
    ) yield c.corpId ~ c.name

    q.toMap
  }

  val start = new LocalDate(2011, 1, 1)
  val end = new LocalDate(2011, 7, 1).minusDays(1)

  val codes = corps.keys.toList.sorted

  def loadData() = {
    (for (code <- codes) yield {
      val volumes = db withSession {
        val q = for (
          s <- SharePrice if s.corpId === code
            && s.marketId === SharePrice.TOSHO
            && s.chartType === SharePrice.DAILY
            && s.infoDate.between(toSql(start), toSql(end));
          _ <- Query orderBy s.infoDate
        ) yield s.infoDate ~ s.volume

        q.toMap
      }

      println(code)
      (code -> volumes)
    }).toMap
  }

  val map = DiskStore.load(loadData, "stockdata")

  val days = map.first._2.keys.toList

  val array = Array.tabulate(codes.size, days.size) {
    case (corp, day) =>
      val a = (map(codes(corp)).withDefaultValue(1.0))(days(day))
      if (a.isInfinite) println("NG")
      a
  }

  val matrix = new Array2DRowRealMatrix(array)

  val pc = 40
  val (w, h) = factorize(matrix, pc, 100)

  println

  for (i <- 0 until pc) {
    (codes.map(corps) zip w.getColumn(i)).sortBy(-_._2).take(5).foreach(print)
    println
    (days zip h.getRow(i)).sortBy(-_._2).take(5).foreach(print)
    println
    println
  }

  def difcost(a: RealMatrix, b: RealMatrix): Double = {
    a.subtract(b).getData().map(_.map(math.abs).sum).sum
  }

  def factorize(v: RealMatrix, pc: Int = 10, iter: Int = 50) = {
    val ic = v.getRowDimension
    val fc = v.getColumnDimension

    var w = new Array2DRowRealMatrix(Array.fill(ic, pc) { math.random })
    var h = new Array2DRowRealMatrix(Array.fill(pc, fc) { math.random })

    breakable {
      for (i <- 1 to iter) {
        val wh = w.multiply(h)

        val cost = difcost(v, wh)

        if (i % 10 == 0) println(cost)

        if (cost == 0) break

        val hn = w.transpose.multiply(v)
        val hd = w.transpose.multiply(w).multiply(h)

        val th = (h.getData, hn.getData, hd.getData).zip.map(_.zip.map(t => t._1 * t._2 / t._3))
        h = new Array2DRowRealMatrix(th)

        val wn = v.multiply(h.transpose)
        val wd = w.multiply(h).multiply(h.transpose)

        val tw = (w.getData, wn.getData, wd.getData).zip.map(_.zip.map(t => t._1 * t._2 / t._3))
        w = new Array2DRowRealMatrix(tw)
      }
    }

    (w, h)
  }
}

object CorpBasis extends Table[(Int, String, Int, Int, Int)]("corp_bss") {
  def corpId = column[Int]("corp_id")
  def name = column[String]("corp_imfrml_name_vch")
  def listed = column[Int]("list_sgn")
  def corp = column[Int]("corp_sgn")
  def tosho = column[Int]("tosho_id")
  def * = corpId ~ name ~ listed ~ corp ~ tosho
}

object SharePrice extends Table[(Int, Int, Int, Date, Double, Double)]("shr_price") {
  def corpId = column[Int]("corp_id")
  def marketId = column[Int]("mkt_id")
  def chartType = column[Int]("cndl_chart_typ")
  def infoDate = column[Date]("info_date")
  def price = column[Double]("close_price_num")
  def volume = column[Double]("trde_volm_num")
  def * = corpId ~ marketId ~ chartType ~ infoDate ~ price ~ volume

  val TOSHO = 1
  val DAILY = 1
}
