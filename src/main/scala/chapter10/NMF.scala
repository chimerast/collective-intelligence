package chapter10

import scala.util.control.Breaks._

import org.apache.commons.math.linear.Array2DRowRealMatrix
import org.apache.commons.math.linear.RealMatrix

object NMF extends {

  def main(args: Array[String]) {
    val a = Array(Array(2.0, 0.0, 3.0, 0.0), Array(0.0, 2.0, 0.0, 1.0), Array(0.0, 0.0, 1.0, 1.0))
    val (w, h) = factorize(new Array2DRowRealMatrix(a), 2, 100)

    println(w.multiply(h))
  }

  def difcost(a: RealMatrix, b: RealMatrix): Double =
    a.subtract(b).getData().map(_.map(math.abs).sum).sum

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
