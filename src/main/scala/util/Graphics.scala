package util

import java.awt.Graphics2D

object Graphics {
  class WrappedGraphics2D(g: Graphics2D) {
    def drawLine(x1: Double, y1: Double, x2: Double, y2: Double): Unit =
      g.drawLine(x1.toInt, y1.toInt, x2.toInt, y2.toInt)

    def drawString(str: String, x: Double, y: Double): Unit =
      g.drawString(str, x.toInt, y.toInt)

    def drawStringCenter(str: String, x: Double, y: Double): Unit = {
      val width = g.getFontMetrics().stringWidth(str)
      val height = g.getFontMetrics().getHeight()
      drawString(str, x - width / 2.0, y - height / 2.0)
    }
  }

  implicit def wrapGraphics2D(g: Graphics2D): WrappedGraphics2D = new WrappedGraphics2D(g)
}