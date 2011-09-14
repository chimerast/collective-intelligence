package chapter3

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map
import scala.io._
import scala.math._
import util.section
import scala.collection.BitSet

object Chapter3 extends App {
  implicit val codec = Codec.string2codec("UTF-8")

  def readfile(filename: String): (Array[String], Array[String], Array[Array[Double]]) = {
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

  section("3.3 階層的クラスタリング") {
    val (blognames, words, data) = readfile("blogdata.txt")
    val clust = BiCluster.hcluster(data)
    BiCluster.printclust(clust, Some(blognames))
  }

  section("3.4 デンドログラムを描く") {
    val (blognames, words, data) = readfile("blogdata.txt")
    val clust = BiCluster.hcluster(data)
    Dendrogram(clust, blognames)
  }

  section("3.6 K平均法によるクラスタリング") {
    val (blognames, words, data) = readfile("blogdata.txt")
    val kclust = BiCluster.kcluster(data, k = 10)
    kclust.zipWithIndex.map {
      case (k, i) => println("k[%d]: %s".format(i, k.map(blognames).mkString(", ")))
    }
  }

  section("3.7 嗜好のクラスタ") {
    val (wants, people, data) = readfile("zebo.txt")
    val clust = BiCluster.hcluster(data, distance = BiCluster.tanimoto)
    Dendrogram(clust, wants)
  }
}
