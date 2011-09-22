package chapter4

import scala.collection.mutable.Map
import scala.math._

import util._

import org.slf4j.LoggerFactory

import org.scalaquery.session._
import org.scalaquery.session.Database.threadLocalSession

import org.scalaquery.ql._
import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql.extended.{ ExtendedTable => Table }
import org.scalaquery.ql.extended.H2Driver.Implicit._

import java.sql.ResultSet

class Searcher(dburl: String) {
  protected val logger = LoggerFactory.getLogger(getClass)

  type Scoring = (Array[Array[Int]], Array[Int]) => Map[Int, Double]

  val dao = new DataAccess(dburl)

  def getMatchRows(q: String)(implicit session: Session): (Array[Array[Int]], Array[Int]) = {
    val fieldList = new StringBuilder()
    val tableList = new StringBuilder()
    val clauseList = new StringBuilder()

    fieldList ++= """w0."url_id""""

    // 空白で単語を分ける
    val words = q.split(" ")
    var tableNumber = 0

    val wordIds = words.flatMap { word =>
      dao.getWordId(word).map { wordId =>
        if (tableNumber > 0) {
          tableList ++= ", "
          clauseList ++= """ AND w%d."url_id"=w%d."url_id" AND """ format (tableNumber - 1, tableNumber)
        }
        fieldList ++= """, w%d."location"""" format tableNumber
        tableList ++= """"wordlocation" w%d""" format tableNumber
        clauseList ++= """w%d."word_id"=%d""" format (tableNumber, wordId)
        tableNumber += 1
        wordId
      }
    }

    def matchResults(rs: ResultSet): Iterator[Array[Int]] = new Iterator[Array[Int]] {
      override def hasNext = !rs.isLast
      override def next = { rs.next; (1 to rs.getMetaData().getColumnCount()).map(rs.getInt(_)).toArray }
    }

    // 分割されたパーツからクエリを構築
    val sql = "SELECT %s FROM %s WHERE %s" format (fieldList, tableList, clauseList)
    val rows = using(session.createStatement()) { stmt => matchResults(stmt.executeQuery(sql)).toArray }

    (rows, wordIds)
  }

  def getScoredList(rows: Array[Array[Int]], wordIds: Array[Int], scoring: List[(Double, Scoring)]): Map[Int, Double] = {
    val totalScores = Map(rows.map(row => (row(0), 0.0)): _*)

    val weights = scoring.map { case (weight, scoring) => (weight, scoring(rows, wordIds)) }

    for ((weight, scores) <- weights; url <- totalScores.keys)
      totalScores(url) += weight * scores(url)

    totalScores
  }

  def query(q: String, scoring: List[(Double, Scoring)]): Unit = {
    val (rows, wordIds) = getMatchRows(q)
    val scores = getScoredList(rows, wordIds, scoring)
    val rankedScores = scores.toList.sortBy(_._2).reverse

    for ((urlId, score) <- rankedScores.take(10)) {
      println("%f\t%s" format (score, dao.getUrl(urlId)))
    }
  }

  /**
   * スコアの正規化
   */
  def normalizeScore(scores: Map[Int, Double], smallIsBetter: Boolean = false): Map[Int, Double] = {
    val vsmall = Double.MinPositiveValue
    if (smallIsBetter) {
      val minscore = scores.values.min
      for ((urlId, score) <- scores) yield (urlId, minscore / max(vsmall, score))
    } else {
      var maxscore = scores.values.max
      if (abs(maxscore) < vsmall) maxscore = vsmall
      for ((urlId, score) <- scores) yield (urlId, score / maxscore)
    }
  }

  /**
   * 単語の頻度によるスコアリング
   */
  def frequencyScore(rows: Array[Array[Int]], wordIds: Array[Int]): Map[Int, Double] = {
    val counts = Map(rows.map(row => (row(0), 0.0)): _*)
    for (row <- rows) counts(row(0)) += 1.0
    normalizeScore(counts)
  }

  /**
   * ドキュメント中での位置によるスコアリング
   */
  def locationScore(rows: Array[Array[Int]], wordIds: Array[Int]): Map[Int, Double] = {
    val locations = Map(rows.map(row => (row(0), Double.PositiveInfinity)): _*)
    for (row <- rows) {
      val loc = row.drop(1).sum
      if (loc < locations(row(0)))
        locations(row(0)) = loc
    }
    normalizeScore(locations, smallIsBetter = true)
  }

  /**
   * 単語間の距離によるスコアリング
   */
  def distanceScore(rows: Array[Array[Int]], wordIds: Array[Int]): Map[Int, Double] = {
    // 単語が一つしか無い場合、全員が勝者！
    if (rows(0).size <= 2) return Map(rows.map(row => (row(0), 1.0)): _*)

    // 大きな値でディクショナリを初期化する
    val minDistance = Map(rows.map(row => (row(0), Double.PositiveInfinity)): _*)

    for (row <- rows) {
      val dist = (row.drop(1) zip row.drop(2)).map(p => abs(p._1 - p._2)).sum
      if (dist < minDistance(row(0)))
        minDistance(row(0)) = dist
    }
    normalizeScore(minDistance, smallIsBetter = true)
  }

  /**
   * インバウンドリンクを単純に数え上げる
   */
  def inboudLinkScore(rows: Array[Array[Int]], wordIds: Array[Int]): Map[Int, Double] = {
    val uniqueUrls = rows.map(_(0)).toSet
    val inboundCounts = Map((uniqueUrls.map { u => (u, dao.countLinkToId(u).toDouble) }.toSeq): _*)
    normalizeScore(inboundCounts)
  }

  /**
   * PageRankの計算
   */
  def calculatePageRank(iterations: Int = 20): Unit = {
    dao.initPageRank

    for (i <- 0 until iterations) {
      println("Iteration %d" format i)

      dao.getUrlIdList.foreach { urlId =>
        var pr = 0.15

        // このページにリンクしているすべてのページをループする
        dao.getLinker(urlId).foreach { linker =>
          // linkerのPageRankを取得する
          val linkingpr = dao.getPageRank(linker)

          // linkerからリンクの合計を取得する
          val linkingcount = dao.countLinkFromId(linker)

          pr += 0.85 * (linkingpr / linkingcount)
        }

        dao.updatePageRank(urlId, pr)
      }
    }
  }

  /**
   * PageRankによるスコアリング
   */
  def pagerankScore(rows: Array[Array[Int]], wordIds: Array[Int]): Map[Int, Double] = {
    val pageranks = Map(rows.map(row => (row(0), dao.getPageRank(row(0)))): _*)
    normalizeScore(pageranks)
  }

  /**
   * リンクのテキストによるスコアリング
   */
  def linkTextScore(rows: Array[Array[Int]], wordIds: Array[Int]): Map[Int, Double] = {
    val linkScores = Map(rows.map(row => (row(0), 0.0)): _*)
    for (wordId <- wordIds) {
      for ((fromId, toId) <- dao.getLink(wordId) if linkScores.contains(toId)) {
        linkScores(toId) += dao.getPageRank(fromId)
      }
    }
    normalizeScore(linkScores)
  }
}
