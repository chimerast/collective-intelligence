package chapter4

class SearchNet(dburl: String) {
  import Layer._

  val dao = new DataAccess(dburl)

  def getStrength(fromId: Int, toId: Int, layer: Layer): Double = {
    layer match {
      case WordToHidden =>
        dao.getStrengthOfWordToHidden(fromId, toId).getOrElse(-0.2)
      case HiddenToUrl =>
        dao.getStrengthOfHiddenToUrl(fromId, toId).getOrElse(0.0)
    }
  }

  def setStrength(fromId: Int, toId: Int, layer: Layer, strength: Double): Unit = {
    layer match {
      case WordToHidden =>
        dao.setStrengthOfWordToHidden(fromId, toId, strength)
      case HiddenToUrl =>
        dao.setStrengthOfHiddenToUrl(fromId, toId, strength)
    }
  }

  def generateHiddenNode(wordIds: Array[Int], urlIds: Array[Int]): Unit = {
    if (wordIds.size > 3) return

    // この単語セットに対してノードを既に作り上げているか調べる
    val createKey = wordIds.mkString("_")

    if (!dao.hasHiddenNode(createKey)) {
      val hiddenId = dao.insertHiddenNode(createKey)
      for (wordId <- wordIds) setStrength(wordId, hiddenId, WordToHidden, 1.0 / wordIds.size)
      for (urlId <- urlIds) setStrength(urlId, hiddenId, HiddenToUrl, 0.1)
    }
  }
}

object Layer extends Enumeration {
  type Layer = Layer.Value
  val WordToHidden, HiddenToUrl = Value
}
