package chapter3

import scala.swing._
import scala.math._
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.FileOutputStream
import javax.imageio.ImageIO
import java.awt.RenderingHints

object Dendrogram {
  def apply(clust: BiCluster, labels: Array[String]): Unit = {
    Swing.onEDT { new Dendrogram(clust, labels) startup (Array[String]()) }
  }

  def save(clust: BiCluster, labels: Array[String], filename: String): Unit = {
    // 高さと幅
    val h = getheight(clust) * 20
    val w = 1200.0
    val depth = getdepth(clust)

    // 幅は固定されているため、適宜縮尺する
    val scaling = (w - 150).toDouble / depth

    val image = new BufferedImage(w.toInt, h.toInt, BufferedImage.TYPE_3BYTE_BGR)

    val g = image.getGraphics.asInstanceOf[Graphics2D]
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
    g.setColor(Color.WHITE)
    g.fillRect(0, 0, image.getWidth, image.getHeight)
    drawtree(g, clust, h, scaling, labels)
    g.dispose

    ImageIO.write(image, "png", new FileOutputStream(filename))
  }

  import util.Graphics._

  def drawtree(g: Graphics2D, clust: BiCluster, h: Double, scaling: Double, labels: Array[String]): Unit = {
    g.setColor(Color.BLUE)
    g.drawLine(0.0, h / 2, 10.0, h / 2)
    drawnode(g, clust, 10, h / 2, scaling, labels)
  }

  def drawnode(g: Graphics2D, clust: BiCluster, x: Double, y: Double, scaling: Double, labels: Array[String]): Unit = {
    (clust.left, clust.right) match {
      case (Some(left), Some(right)) =>
        val h1 = getheight(left) * 20
        val h2 = getheight(right) * 20
        val top = y - (h1 + h2) / 2
        val bottom = y + (h1 + h2) / 2
        // 直線の長さ
        val ll = clust.distance * scaling

        g.setColor(Color.BLUE)

        // クラスタから子への垂直な直線
        g.drawLine(x, top + h1 / 2, x, bottom - h2 / 2)

        // 左側のアイテムへの水平な直線
        g.drawLine(x, top + h1 / 2, x + ll, top + h1 / 2)

        // 右側のアイテムへの水平な直線
        g.drawLine(x, bottom - h2 / 2, x + ll, bottom - h2 / 2)

        drawnode(g, left, x + ll, top + h1 / 2, scaling, labels)
        drawnode(g, right, x + ll, bottom - h2 / 2, scaling, labels)
      case _ =>
        g.setColor(Color.BLACK)
        // 終点であればアイテムのラベルを描く
        g.drawString(labels(clust.id), x + 5, y + 5)
    }
  }

  def getheight(clust: BiCluster): Double = {
    (clust.left, clust.right) match {
      case (Some(left), Some(right)) =>
        // そうでなければ高さはそれぞれの枝の高さ
        getheight(left) + getheight(right)
      case _ =>
        // 終端であればたかさは1にする
        1.0
    }
  }

  def getdepth(clust: BiCluster): Double = {
    (clust.left, clust.right) match {
      case (Some(left), Some(right)) =>
        // 枝の距離は二つの方向の大きい方にそれ自身の距離を足したもの
        max(getdepth(left), getdepth(right)) + clust.distance
      case _ =>
        // 終端への距離は0
        0.0
    }
  }
}

class Dendrogram(clust: BiCluster, labels: Array[String]) extends SimpleSwingApplication {
  import Dendrogram._

  // 高さと幅
  val h = getheight(clust) * 20
  val w = 1200.0
  val depth = getdepth(clust)

  // 幅は固定されているため、適宜縮尺する
  val scaling = (w - 150).toDouble / depth

  def top = new MainFrame {
    background = Color.WHITE
    resizable = true
    contents = new ScrollPane() {
      preferredSize = new Dimension(w.toInt, 800)
      viewportView = Some(new Component() {
        preferredSize = new Dimension(w.toInt, h.toInt)
        override def paint(g: Graphics2D): Unit = {
          drawtree(g, clust, h, scaling, labels)
        }
      })
    }
  }
}
