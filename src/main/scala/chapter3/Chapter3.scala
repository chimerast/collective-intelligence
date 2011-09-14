package chapter3

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map
import scala.io._
import scala.math._

import util._

object Chapter3 { // extends App {
  section("3.3 階層的クラスタリング") {
    val (blognames, words, data) = BiCluster.readfile()
    val clust = BiCluster.hcluster(data)
    BiCluster.printclust(clust, Some(blognames))
  }
}
