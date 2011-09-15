package chapter3

import scala.math._
import scala.swing._

import java.awt.Color

object MDScaling {
  def apply(data: Array[Array[Double]], labels: Array[String]): Unit = {
    Swing.onEDT { new MDScaling(data, labels) startup (Array[String]()) }
  }

  def scaledown(data: Array[Array[Double]], distance: BiCluster.Distance = BiCluster.pearson, rate: Double = 0.01, dim: Int = 2): Array[Array[Double]] = {
    val n = data.size
    // アイテムの全ての組の実際の距離
    val realdist = Array.tabulate(n, n) { (i, j) => distance(data(i), data(j)) }

    // 2次元上にランダムに配置するように初期化する
    val loc = Array.fill(n) { Array.fill(dim) { random } }

    var lasterror = Double.MaxValue
    for (m <- 0 until 1000) {
      // 予測距離を測る
      val fakedist = Array.tabulate(n, n) { (i, j) => dist(loc(i), loc(j)) }

      // ポイントの移動
      val grad = Array.fill(n) { Array.fill(dim) { 0.0 } }

      var totalerror = 0.0
      for (k <- 0 until n; j <- 0 until n if j != k) {
        // 誤差は距離の差の百分率
        val errorterm = (fakedist(j)(k) - realdist(j)(k)) / realdist(j)(k)

        // 他のポイントへの誤差に比例してそれぞれのポイントを
        // 近づけたり遠ざけたりする必要がある
        for (i <- 0 until dim) grad(k)(i) += ((loc(k)(i) - loc(j)(i)) / fakedist(j)(k)) * errorterm

        // 誤差の合計を記録
        totalerror += errorterm.abs
      }

      // ポイントを移動することで誤差が悪化したら終了
      if (lasterror < totalerror)
        return loc
      lasterror = totalerror

      // 学習率と傾斜を掛け合わせてそれぞれのポイントを移動
      for (k <- 0 until n) {
        for (i <- 0 until dim) loc(k)(i) -= rate * grad(k)(i)
      }
    }

    return loc
  }

  private def dist(p1: Array[Double], p2: Array[Double]): Double = {
    sqrt((p1 zip p2).map { case (e1, e2) => pow(e1 - e2, 2) }.sum)
  }
}

class MDScaling(data: Array[Array[Double]], labels: Array[String]) extends SimpleSwingApplication {
  import util.Graphics._

  val w = 2000
  val h = 2000

  def top = new MainFrame {
    background = Color.WHITE
    resizable = true
    contents = new ScrollPane() {
      preferredSize = new Dimension(1000, 800)
      viewportView = Some(new Component() {
        preferredSize = new Dimension(w, h)
        override def paint(g: Graphics2D): Unit = {
          data.zipWithIndex.foreach {
            case (d, i) =>
              // -0.5 〜 1.5 の範囲を描画する
              val x = (d(0) + 0.5) * w / 2.0
              val y = (d(1) + 0.5) * w / 2.0
              g.drawStringCenter(labels(i), x.toFloat, y.toFloat)
          }
        }
      })
    }
  }
}
