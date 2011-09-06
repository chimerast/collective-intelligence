import scala.collection.mutable._
import scala.collection.JavaConversions._
import scala.math._
import scala.util._

object Chapter2 extends App {
  val critics = Map(
    "Lisa Rose" -> Map("Lady in the Water" -> 2.5, "Snakes on a Plane" -> 3.5, "Just My Luck" -> 3.0, "Superman Returns" -> 3.5, "You, Me and Dupree" -> 2.5, "The Night Listener" -> 3.0),
    "Gene Seymour" -> Map("Lady in the Water" -> 3.0, "Snakes on a Plane" -> 3.5, "Just My Luck" -> 1.5, "Superman Returns" -> 5.0, "The Night Listener" -> 3.0, "You, Me and Dupree" -> 3.5),
    "Michael Phillips" -> Map("Lady in the Water" -> 2.5, "Snakes on a Plane" -> 3.0, "Superman Returns" -> 3.5, "The Night Listener" -> 4.0),
    "Claudia Puig" -> Map("Snakes on a Plane" -> 3.5, "Just My Luck" -> 3.0, "The Night Listener" -> 4.5, "Superman Returns" -> 4.0, "You, Me and Dupree" -> 2.5),
    "Mick LaSalle" -> Map("Lady in the Water" -> 3.0, "Snakes on a Plane" -> 4.0, "Just My Luck" -> 2.0, "Superman Returns" -> 3.0, "The Night Listener" -> 3.0, "You, Me and Dupree" -> 2.0),
    "Jack Matthews" -> Map("Lady in the Water" -> 3.0, "Snakes on a Plane" -> 4.0, "The Night Listener" -> 3.0, "Superman Returns" -> 5.0, "You, Me and Dupree" -> 3.5),
    "Toby" -> Map("Snakes on a Plane" -> 4.5, "You, Me and Dupree" -> 1.0, "Superman Returns" -> 4.0))

  // 評価対象データ
  type Prefs = Map[String, Map[String, Double]]

  /**
   * ユークリッド距離によるスコア
   */
  def simDistance(prefs: Prefs, p1: String, p2: String): Double = {
    // 両者共に評価しているものが一つも無ければ0を返す
    if (!prefs(p1).keys.exists(prefs(p2).contains)) return 0.0

    // ユークリッド距離を算出
    val sumOfSquares = prefs(p1).filter(i => prefs(p2).contains(i._1))
      .map(i => pow(i._2 - prefs(p2)(i._1), 2)).sum

    // 0.0〜1.0に納めるための計算
    1.0 / (1.0 + sumOfSquares)
  }

  Section("2.3.1 ユークリッド距離によるスコア") {
    println(sqrt(pow(5 - 4, 2) + pow(4 - 1, 2)))
    println(1 / (1 + sqrt(pow(5 - 4, 2) + pow(4 - 1, 2))))

    println(simDistance(critics, "Lisa Rose", "Gene Seymour"))
  }

  /**
   * ピアソン相関によるスコア
   */
  def simPearson(prefs: Prefs, p1: String, p2: String): Double = {
    // 両者が互いに評価しているアイテムのリストを取得
    val si = prefs(p1).keys.toList.filter(prefs(p2).contains)
    val n = si.size

    // 共に評価しているアイテムがなければ0を返す
    if (n == 0) return 0.0

    // すべての嗜好を合計する
    val sum1 = si.map(prefs(p1)).sum
    val sum2 = si.map(prefs(p2)).sum

    // 平方を合計する
    val sum1Sq = si.map(prefs(p1)).map(pow(_, 2)).sum
    val sum2Sq = si.map(prefs(p2)).map(pow(_, 2)).sum

    // 積を合計する
    val pSum = si.map(i => prefs(p1)(i) * prefs(p2)(i)).sum

    // ピアソンによるスコアを計算する
    val num = pSum - (sum1 * sum2 / n)
    val den = sqrt((sum1Sq - pow(sum1, 2) / n) * (sum2Sq - pow(sum2, 2) / n))
    if (den == 0.0) return 0.0

    num / den
  }

  Section("2.3.2 ピアソン相関によるスコア") {
    println(simPearson(critics, "Lisa Rose", "Gene Seymour"))
  }

  /*
   * 2.3.3 どちらの類似尺度を利用すべきか？
   * どの方法がベストであるかは、アプリケーションによって異なる。
   * 
   * Jaccard係数 / マンハッタン距離 / etc...
   * http://en.wikipedia.org/wiki/Metric_(mathematics)#Examples
   */

  Section("2.3.4 評者をランキングする") {
    println(topMatches(critics, "Toby", n = 3))
  }

  // 評価関数
  type Similarity = (Prefs, String, String) => Double

  /**
   * prefsからpersonにもっともマッチするものたちを返す
   */
  def topMatches(prefs: Prefs, person: String, n: Int = 5, similarity: Similarity = simPearson): List[(Double, String)] = {
    val scores = prefs.keys.toList.filter(person !=).map(other => (similarity(prefs, person, other), other))
    // 高スコアがリストの最初にくるように並び替える
    scores.sortBy(_._1).reverse.take(n)
  }

  /**
   * person以外の全ユーザの評点の重み付き平均を使い、personへの推薦を算出する
   */
  def getRecommendations(prefs: Prefs, person: String, similarity: Similarity = simPearson): List[(Double, String)] = {
    val totals = Map[String, Double]().withDefaultValue(0.0)
    val simSums = Map[String, Double]().withDefaultValue(0.0)
    prefs.keys.toList.filter(person !=).map(other => (similarity(prefs, person, other), other)).filter(_._1 > 0.0).foreach {
      case (sim, other) =>
        // まだ見ていない映画の得点のみを算出
        prefs(other).keys.toList
          .filter(item => (!prefs(person).contains(item)) || prefs(person)(item) == 0.0)
          .foreach { item =>
            // 類似度 * スコア
            totals(item) += prefs(other)(item) * sim
            // 類似度を合計
            simSums(item) += sim
          }
    }

    // 正規化したリストを作る
    val rankings = totals.toList.map { case (item, total) => (total / simSums(item), item) }

    // ソート済みのリストを返す
    rankings.sortBy(_._1).reverse
  }

  Section("2.4 アイテムを推薦する") {
    println(getRecommendations(critics, "Toby"))
    println(getRecommendations(critics, "Toby", similarity = simDistance))
  }

  /**
   * ディクショナリの構造を転置
   */
  def transformPrefs(prefs: Prefs): Prefs = {
    val result = Map[String, Map[String, Double]]()
    for (person <- prefs.keys; item <- prefs(person).keys) {
      result.getOrElseUpdate(item, Map[String, Double]())
      // itemとpersonを入れ替える
      result(item)(person) = prefs(person)(item)
    }
    result
  }

  Section("2.5 似ている商品") {
    val movies = transformPrefs(critics)
    println(topMatches(movies, "Superman Returns"))
    println(getRecommendations(movies, "Just My Luck"))
  }

  import Delicious._

  Section("2.6.1 del.icio.usのAPI") {
    getPopular("programming").take(5).foreach(println)
  }

  /**
   * tagに関する人気のリンクを投稿したユーザを取得
   */
  def initializeUserDict(tag: String, count: Int = 5): List[String] = {
    // popularな投稿をcount番目まで取得
    for (p1 <- getPopular(tag, count); p2 <- getUrlPosts(p1("u"))) yield p2("a")
  }

  /**
   * usersによって投稿されたリンクを集める
   */
  def fillItems(users: List[String]): Map[String, Map[String, Double]] = {
    val allItems = Set[String]()

    // すべてのユーザによって投稿されたリンクを取得
    val userDict = Map(users.map { user =>
      val dict = getUserPosts(user).map { post =>
        val url = post("u")
        allItems += url
        url -> 1.0
      }
      (user -> Map(dict: _*))
    }: _*)

    // 空のアイテムを0で埋める
    for (dict <- userDict.values; url <- allItems) {
      dict.getOrElseUpdate(url, 0.0)
    }

    userDict
  }

  val count = 1
  val delusers = initializeUserDict("web", count)
  val delitems = fillItems(delusers)

  Section("2.6.2 データセットを作る") {
    println(delitems.take(1))
  }

  Section("2.6.3 ご近所さんとリンクの推薦") {
    val user = delusers(Random.nextInt(delusers.size))
    println(user)
    println(topMatches(delitems, user))

    println(getRecommendations(delitems, user).take(5))
  }
}
