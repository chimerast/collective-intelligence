package chapter3

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map
import scala.io._
import scala.math._

import util.section

object Chapter3 extends App {

  section("3.3 階層的クラスタリング") {
    val (blognames, words, data) = BiCluster.readfile()
    val clust = BiCluster.hcluster(data)
    BiCluster.printclust(clust, Some(blognames))
  }

  section("3.6 K平均法によるクラスタリング") {
    val (blognames, words, data) = BiCluster.readfile()
    val kclust = BiCluster.kcluster(data, k = 10)
    kclust.zipWithIndex.map {
      case (k, i) => println("k[%d]: %s".format(i, k.map(blognames).mkString(", ")))
    }
  }
}
