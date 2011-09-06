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
    println("sqrt(pow(5 - 4, 2) + pow(4 - 1, 2))")
    println(sqrt(pow(5 - 4, 2) + pow(4 - 1, 2)))
    println("1 / (1 + sqrt(pow(5 - 4, 2) + pow(4 - 1, 2)))")
    println(1 / (1 + sqrt(pow(5 - 4, 2) + pow(4 - 1, 2))))

    println("Lisa RoseとGene Seymourのユークリッド距離")
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
    println("Lisa RoseとGene Seymourのピアソン相関")
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
    topMatches(critics, "Toby", n = 3).foreach(println)
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
  def getRecommendations(prefs: Prefs, person: String, n: Int = 5, similarity: Similarity = simPearson): List[(Double, String)] = {
    val totals = Map[String, Double]().withDefaultValue(0.0)
    val simSums = Map[String, Double]().withDefaultValue(0.0)

    for (
      other <- prefs.keys if other != person; // 自分自身とは比較しない
      sim = similarity(prefs, person, other) if sim > 0.0; // 0.0以下のスコアは無視する
      item <- prefs(other).keys if (!prefs(person).contains(item)) || prefs(person)(item) == 0.0 // まだ見ていない映画の得点のみを算出
    ) {
      // 類似度 * スコア
      totals(item) += prefs(other)(item) * sim
      // 類似度を合計
      simSums(item) += sim
    }

    // 正規化したリストを作る
    val rankings = totals.toList.map { case (item, total) => (total / simSums(item), item) }

    // ソート済みのリストを返す
    rankings.sortBy(_._1).reverse.take(n)
  }

  Section("2.4 アイテムを推薦する") {
    println("ピアソン相関でToby用の商品を推薦")
    getRecommendations(critics, "Toby").foreach(println)
    println("ユークリッド距離でToby用の商品を推薦")
    getRecommendations(critics, "Toby", similarity = simDistance).foreach(println)
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
    println("Superman Returnsに似ている商品を探す")
    topMatches(movies, "Superman Returns").foreach(println)
    println("Just My Luckを見ていない評者の中で高い評価をつけそうな人を予測する")
    getRecommendations(movies, "Just My Luck").foreach(println)
  }

  import Delicious._

  Section("2.6.1 del.icio.usのAPI") {
    println("programmingに関する人気のブックマーク")
    getPopular("programming").take(5).foreach(println)
  }

  /**
   * tagに関する人気のリンクを投稿したユーザを取得
   */
  def initializeUserDict(tag: String, count: Int = 5): List[String] = {
    // popularな投稿をcount番目まで取得
    for (p1 <- getPopular(tag, count); p2 <- getUrlPosts(p1(Delicious.PARAM_URL))) yield p2(Delicious.PARAM_USER)
  }

  /**
   * usersによって投稿されたリンクを集める
   */
  def fillItems(users: List[String]): Map[String, Map[String, Double]] = {
    val allItems = Set[String]()

    // すべてのユーザによって投稿されたリンクを取得
    val userDict = Map(users.map { user =>
      val dict = getUserPosts(user).map { post =>
        val url = post(Delicious.PARAM_URL)
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
  val delusers = initializeUserDict("programming", count)
  val delitems = fillItems(delusers)

  Section("2.6.2 データセットを作る") {
    println("del.icio.usから人気のprogrammingのURLをブックマークしたユーザを抜いてくる")
    delitems.take(5).foreach(println)
  }

  Section("2.6.3 ご近所さんとリンクの推薦") {
    println("ユーザーに似た嗜好のユーザを探す")
    val user = delusers(Random.nextInt(delusers.size))
    println("ユーザ名: " + user)
    println(topMatches(delitems, user))

    println("ユーザが好みそうなリンクを探す")
    println(getRecommendations(delitems, user))

    println("特定のリンクに似たリンクを探す")
    var url = getRecommendations(delitems, user)(0)._2
    println("URL: " + url)
    println(topMatches(transformPrefs(delitems), url))
  }

  type ItemMatch = Map[String, List[(Double, String)]]

  /**
   * アイテムをキーとして持ち、それぞれのアイテムに似ている
   * アイテムのリストを値として持つディクショナリを作る。
   */
  def calculateSimilarItems(prefs: Prefs, n: Int = 10, similarity: Similarity = simDistance): ItemMatch = {
    // 嗜好の行列をアイテム中心な形に反転させる
    val itemPrefs = transformPrefs(prefs)

    Map(itemPrefs.zip(Stream.from(1)).map {
      case ((item, _), c) =>
        // 巨大なデータセット用にステータスを表示
        if (c % 100 == 0) println("%d / %d" format (c, itemPrefs.size))
        // このアイテムに最も似ているアイテムたちを探す
        val scores = topMatches(itemPrefs, item, n = n, similarity = similarity)
        (item -> scores)
    }.toList: _*)
  }

  val itemsim = calculateSimilarItems(critics)

  Section("2.7.1 アイテム間の類似度のデータセットを作る") {
    itemsim.foreach(println)
  }

  /**
   * 推薦を行う
   */
  def getRecommendedItems(prefs: Prefs, itemMatch: ItemMatch, user: String, n: Int = 5): List[(Double, String)] = {
    val userRatings = prefs(user)
    val scores = Map[String, Double]().withDefaultValue(0.0)
    val totalSim = Map[String, Double]().withDefaultValue(0.0)

    for (
      (item, rating) <- userRatings;
      (similarity, item2) <- itemMatch(item) if !userRatings.contains(item2)
    ) {
      // 評点と類似度を掛け合わせたものの合計で重み付けする
      scores(item2) += similarity * rating
      // 全ての類似度の合計
      totalSim(item2) += similarity
    }

    // 正規化のため、それぞれの重み付けしたスコアを類似度の合計で割る
    val rankings = scores.toList.map { case (item, score) => (score / totalSim(item), item) }

    // 降順にランキングを返す
    rankings.sortBy(_._1).reverse.take(n)
  }

  Section("2.7.2 推薦を行う") {
    getRecommendedItems(critics, itemsim, "Toby").foreach(println)
  }
}
