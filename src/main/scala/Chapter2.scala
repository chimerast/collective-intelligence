import scala.collection.mutable._
import scala.collection.JavaConversions._
import scala.io._
import scala.math._
import scala.util._
import Section._
import Utils._

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

  section("2.3.1 ユークリッド距離によるスコア") {
    subsection("sqrt(pow(5 - 4, 2) + pow(4 - 1, 2))")
    output(sqrt(pow(5 - 4, 2) + pow(4 - 1, 2)))
    subsection("1 / (1 + sqrt(pow(5 - 4, 2) + pow(4 - 1, 2)))")
    output(1 / (1 + sqrt(pow(5 - 4, 2) + pow(4 - 1, 2))))

    subsection("Lisa RoseとGene Seymourのユークリッド距離")
    output(simDistance(critics, "Lisa Rose", "Gene Seymour"))
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

  section("2.3.2 ピアソン相関によるスコア") {
    subsection("Lisa RoseとGene Seymourのピアソン相関")
    output(simPearson(critics, "Lisa Rose", "Gene Seymour"))
  }

  /*
   * 2.3.3 どちらの類似尺度を利用すべきか？
   * どの方法がベストであるかは、アプリケーションによって異なる。
   * 
   * Jaccard係数 / マンハッタン距離 / etc...
   * http://en.wikipedia.org/wiki/Metric_(mathematics)#Examples
   */

  section("2.3.4 評者をランキングする") {
    subsection("Tobyに似た評者を探す")
    topMatches(critics, "Toby", n = 3).foreach(output)
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

  section("2.4 アイテムを推薦する") {
    subsection("ピアソン相関でToby用の商品を推薦")
    getRecommendations(critics, "Toby").foreach(output)
    subsection("ユークリッド距離でToby用の商品を推薦")
    getRecommendations(critics, "Toby", similarity = simDistance).foreach(output)
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

  section("2.5 似ている商品") {
    val movies = transformPrefs(critics)
    subsection("Superman Returnsに似ている商品を探す")
    topMatches(movies, "Superman Returns").foreach(output)
    subsection("Just My Luckを見ていない評者の中で高い評価をつけそうな人を予測する")
    getRecommendations(movies, "Just My Luck").foreach(output)
  }

  import Delicious._

  section("2.6.1 del.icio.usのAPI") {
    subsection("programmingに関する人気のブックマーク")
    getPopular("programming").take(5).foreach(output)
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

  val delusers = initializeUserDict("programming", 3)
  val delitems = fillItems(delusers)

  section("2.6.2 データセットを作る") {
    subsection("del.icio.usから人気のprogrammingのURLをブックマークしたユーザを抜いてくる")
    delitems.take(5).foreach(output)
  }

  section("2.6.3 ご近所さんとリンクの推薦") {
    subsection("ユーザーに似た嗜好のユーザを探す")
    val user = delusers(Random.nextInt(delusers.size))
    output("ユーザ名: " + user)
    output(topMatches(delitems, user))

    subsection("ユーザが好みそうなリンクを探す")
    output(getRecommendations(delitems, user))

    subsection("特定のリンクに似たリンクを探す")
    var url = getRecommendations(delitems, user)(0)._2
    output("URL: " + url)
    output(topMatches(transformPrefs(delitems), url))
  }

  type ItemMatch = Map[String, List[(Double, String)]]

  /**
   * アイテムをキーとして持ち、それぞれのアイテムに似ている
   * アイテムのリストを値として持つディクショナリを作る。
   */
  def calculateSimilarItems(prefs: Prefs, n: Int = 10, similarity: Similarity = simDistance): ItemMatch = {
    // 嗜好の行列をアイテム中心な形に反転させる
    val itemPrefs = transformPrefs(prefs)

    itemPrefs.par.map {
      case (item, _) =>
        // このアイテムに最も似ているアイテムたちを探す
        val scores = topMatches(itemPrefs, item, n = n, similarity = similarity)
        (item -> scores)
    }.seq
  }

  val itemsim = calculateSimilarItems(critics)

  section("2.7.1 アイテム間の類似度のデータセットを作る") {
    itemsim.foreach(output)
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

  section("2.7.2 推薦を行う") {
    subsection("アイテムベースの表からToby向け推薦を行う")
    getRecommendedItems(critics, itemsim, "Toby").foreach(output)
  }

  /**
   * MovieLensのデータセットを読み込む
   */
  def loadMovieLens(path: String = "ml-100k"): Map[String, Map[String, Double]] = {
    implicit val codec = Codec.string2codec("ISO-8859-9")

    // 映画のタイトルを得る
    val movies = using(Source.fromFile(path + "/u.item")) { source =>
      source.getLines.map { line =>
        val Array(item, title, _@ _*) = line.split("\\|")
        (item -> title)
      }.toMap
    }

    // データの読み込み
    using(Source.fromFile(path + "/u.data")) { source =>
      val prefs = Map[String, Map[String, Double]]()
      source.getLines.foreach { line =>
        val Array(user, movieid, rating, ts, _@ _*) = line.split("\t")
        prefs.getOrElseUpdate(user, Map[String, Double]())
        prefs(user)(movies(movieid)) = rating.toDouble
      }
      prefs
    }
  }

  section("2.8 MovieLensのデータセットを使う") {
    val prefs = loadMovieLens()
    subsection("87番のユーザの評価を出力")
    prefs.take(4).foreach(output)

    subsection("87番のユーザベースの推薦")
    getRecommendations(prefs, "87").foreach(output)

    subsection("アイテムベースの推薦")
    val itemsim = calculateSimilarItems(prefs, n = 50)
    getRecommendedItems(prefs, itemsim, "87", n = 5).foreach(output)
  }

}
