package chapter4

import math._

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
    val createKey = wordIds.sorted.mkString("_")

    if (!dao.hasHiddenNode(createKey)) {
      val hiddenId = dao.insertHiddenNode(createKey)
      for (wordId <- wordIds) setStrength(wordId, hiddenId, WordToHidden, 1.0 / wordIds.size)
      for (urlId <- urlIds) setStrength(hiddenId, urlId, HiddenToUrl, 0.1)
    }
  }

  def getAllHiddenIds(wordIds: Array[Int], urlIds: Array[Int]): Array[Int] = {
    dao.getHiddenIds(wordIds, urlIds)
  }

  var wordIds: Array[Int] = _
  var hiddenIds: Array[Int] = _
  var urlIds: Array[Int] = _

  var ai: Array[Double] = _
  var ah: Array[Double] = _
  var ao: Array[Double] = _

  var wi: Array[Array[Double]] = _
  var wo: Array[Array[Double]] = _

  def setupNetwork(wordIds: Array[Int], urlIds: Array[Int]): Unit = {
    // 値のリスト
    this.wordIds = wordIds
    this.hiddenIds = getAllHiddenIds(wordIds, urlIds)
    this.urlIds = urlIds

    // ノードの出力
    this.ai = Array.fill(wordIds.length)(1.0)
    this.ah = Array.fill(hiddenIds.length)(1.0)
    this.ao = Array.fill(urlIds.length)(1.0)

    // 重みの行列を作る
    this.wi = for (wordId <- wordIds) yield for (hiddenId <- hiddenIds) yield getStrength(wordId, hiddenId, WordToHidden)
    this.wo = for (hiddenId <- hiddenIds) yield for (urlId <- urlIds) yield getStrength(hiddenId, urlId, HiddenToUrl)
  }

  def feedForward: Array[Double] = {
    // 入力はクエリの単語たち
    for (i <- 0 until wordIds.size) ai(i) = 1.0

    // 隠れ層の発火
    for (j <- 0 until hiddenIds.size) {
      val sum = (0 until wordIds.size).map(i => ai(i) * wi(i)(j)).sum
      ah(j) = tanh(sum)
    }

    // 出力層の発火
    for (k <- 0 until urlIds.size) {
      val sum = (0 until hiddenIds.size).map(j => ah(j) * wo(j)(k)).sum
      ao(k) = tanh(sum)
    }

    ao
  }

  def getResult(wordIds: Array[Int], urlIds: Array[Int]): Array[Double] = {
    setupNetwork(wordIds, urlIds)
    feedForward
  }

  def dtanh(x: Double) = 1.0 - x * x
}

object Layer extends Enumeration {
  type Layer = Layer.Value
  val WordToHidden, HiddenToUrl = Value
}
