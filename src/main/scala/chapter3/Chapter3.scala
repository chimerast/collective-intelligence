package chapter3

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map
import scala.io._
import scala.math._

import util._

object Chapter3 { // extends App {
  implicit val codec = Codec.string2codec("UTF-8")

  // 評価関数
  type Distance = (Array[Double], Array[Double]) => Double

  def readfile(filename: String = "blogdata.txt"): (Array[String], Array[String], Array[Array[Double]]) = {
    val lines = Source.fromFile(filename).getLines

    val colnames = lines.next().split("\\t").drop(1)
    val (rownames, data) = lines.map { line =>
      val p = line.split("\\t")
      (p(0), p.drop(1).map(_.toDouble))
    }.foldRight(Array[String](), Array[Array[Double]]()) {
      case (a, b) => (a._1 +: b._1, a._2 +: b._2)
    }

    (rownames, colnames, data)
  }

  def pearson(v1: Array[Double], v2: Array[Double]): Double = {
    val n = v1.size

    // 単純な合計
    val sum1 = v1.sum
    val sum2 = v2.sum

    // 平方を合計
    val sum1Sq = v1.map(pow(_, 2)).sum
    val sum2Sq = v2.map(pow(_, 2)).sum

    // 積の合計
    val pSum = v1.zip(v2).map { case (i1, i2) => i1 * i2 }.sum

    // ピアソンによるスコアを算出
    val num = pSum - (sum1 * sum2 / n)
    val den = sqrt((sum1Sq - pow(sum1, 2) / n) * (sum2Sq - pow(sum2, 2) / n))
    if (den == 0.0) return 0.0

    1.0 - num / den
  }

  case class BiCluster(vec: Array[Double], id: Int, left: Option[BiCluster] = None, right: Option[BiCluster] = None, distance: Double = 0.0)

  def hcluster(rows: Array[Array[Double]], distance: Distance = pearson): BiCluster = {
    val distances = Map[(Int, Int), Double]()
    var currentclusterid = -1

    // クラスタは最初は行たち
    val clust = ArrayBuffer(rows.zipWithIndex.map { case (row, i) => BiCluster(row, i) }: _*)

    while (clust.size > 1) {
      var lowestpair = (0, 1)
      var closest = Double.MaxValue

      // すべての組をループし、最も距離の近い組を探す
      clust.zipWithIndex.combinations(2).foreach {
        case ArrayBuffer((ci, i), (cj, j)) =>
          // 距離をキャッシュしてあればそれを使う
          val d = distances.getOrElseUpdate((ci.id, cj.id), distance(ci.vec, cj.vec))
          if (d < closest) {
            closest = d
            lowestpair = (i, j)
          }
      }

      // 二つのクラスタの平均を計算する
      val mergevec = clust(lowestpair._1).vec.zip(clust(lowestpair._2).vec).map { case (i1, i2) => (i1 + i2) / 2.0 }

      // 新たなクラスタを作る
      val newcluster = BiCluster(mergevec, currentclusterid,
        left = Some(clust(lowestpair._1)),
        right = Some(clust(lowestpair._2)),
        distance = closest)

      // 元のセットではないクラスタのIDは負にする
      currentclusterid -= 1
      clust.remove(lowestpair._2)
      clust.remove(lowestpair._1)
      clust.append(newcluster)
    }

    clust(0)
  }

  def printclust(clust: BiCluster, labels: Option[Array[String]] = None, n: Int = 0): Unit = {
    // 階層型のレイアウトにするためにインデントする
    print(" " * n)
    if (clust.id < 0) {
      // 負のidの時はこれが枝であることを示している
      println("-")
    } else {
      // 正のidはこれが終端だということを示している
      labels match {
        case Some(labels) =>
          println(labels(clust.id))
        case None =>
          println(clust.id)
      }
    }

    // 右と左の枝を表示する
    clust.left.foreach(printclust(_, labels, n + 1))
    clust.right.foreach(printclust(_, labels, n + 1))
  }
  /*
  section("3.3 階層的クラスタリング") {
    val (blognames, words, data) = readfile()
    val clust = hcluster(data)
    printclust(clust, Some(blognames))
  }
*/
}
