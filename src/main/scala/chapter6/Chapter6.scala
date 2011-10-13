package chapter6

import scala.collection.mutable.Map
import scala.io._
import scala.math._

import util._

object Chapter6 extends App {
  def getWords(doc: String): Map[String, Int] = {
    val splitter = """\W+""".r
    // 単語を非アルファベットの文字で分割する
    val words = for (s <- splitter.split(doc) if s.length > 2 && s.length < 20) yield s.toLowerCase
    // ユニークな単語のみの集合を返す
    Map(words.map((_ -> 1)): _*)
  }

  def sampletrain(cl: Classifier): Unit = {
    cl.train("Nobody owns the water.", "good")
    cl.train("the quick rabbit jumps fences", "good")
    cl.train("buy pharmaceuticals now", "bad")
    cl.train("make quick money at the online casino", "bad")
    cl.train("the quick brown fox jumps", "good")
  }

  section("6.3 分類器のトレーニング") {
    val cl = new Classifier(getWords)
    cl.train("the quick brown fox jumps over the lazy dog", "good")
    cl.train("make quick money in the online casino", "bad")
    output(cl.fcount("quick", "good"))
    output(cl.fcount("quick", "bad"))
  }

  section("6.4 確率を計算する") {
    val cl = new Classifier(getWords)
    sampletrain(cl)
    output(cl.fprob("quick", "good"))
  }

  section("6.4.1 推測を始める") {
    val cl = new Classifier(getWords)
    sampletrain(cl)
    output(cl.fprob("quick", "good"))
    output(cl.weightedprob("money", "good", cl.fprob))
    sampletrain(cl)
    output(cl.weightedprob("money", "good", cl.fprob))
  }

  section("6.5.2 ベイズの定理の簡単な紹介") {
    val cl = new NativeBayes(getWords)
    sampletrain(cl)
    output(cl.prob("quick rabbit", "good"))
    output(cl.prob("quick rabbit", "bad"))
  }

  section("6.5.3 カテゴリの選択") {
    val cl = new NativeBayes(getWords)
    sampletrain(cl)
    output(cl.classify("quick rabbit", "unknown"))
    output(cl.classify("quick money", "unknown"))
    cl.setThreshold("bad", 3.0)
    output(cl.classify("quick money", "unknown"))
    (0 until 10).foreach(_ => sampletrain(cl))
    output(cl.classify("quick money", "unknown"))
  }
}

class NativeBayes(getFeatures: (String) => Map[String, Int]) extends Classifier(getFeatures) {
  val thresholds = Map[String, Double]()

  def docprob(item: String, cat: String): Double = {
    val features = getFeatures(item)
    // すべての特徴の確率を掛け合わせる
    features.keys.map(f => weightedprob(f, cat, fprob)).product
  }

  def prob(item: String, cat: String): Double = {
    val cprob = catcount(cat) / totalCount
    val dprob = docprob(item, cat)
    dprob * cprob
  }

  def setThreshold(cat: String, t: Double): Unit = {
    thresholds(cat) = t
  }

  def getThreshold(cat: String): Double = {
    thresholds.getOrElse(cat, 1.0)
  }

  def classify(item: String, default: String): String = {
    val probs = Map(categories.map(cat => cat -> prob(item, cat)): _*)
    // もっとも確率の高いカテゴリを探す
    val best = probs.maxBy(_._2)
    for (cat <- probs.keys if cat != best._1) {
      if (probs(cat) * getThreshold(best._1) > best._2)
        return default
    }
    best._1
  }
}

class Classifier(getFeatures: (String) => Map[String, Int]) {
  // 特徴/カテゴリのカウント
  val fc = Map[String, Map[String, Double]]()
  // それぞれのカテゴリの中のドキュメント数
  val cc = Map[String, Double]().withDefaultValue(0.0)

  // 特徴/カテゴリのカウントを増やす
  def incf(f: String, cat: String): Unit = {
    fc.getOrElseUpdate(f, Map[String, Double]().withDefaultValue(0.0))
    fc(f)(cat) += 1.0
  }

  // カテゴリのカウントを増やす
  def incc(cat: String): Unit = {
    cc(cat) += 1.0
  }

  // あるカテゴリの中に特徴が現れた数
  def fcount(f: String, cat: String): Double = {
    fc.get(f).flatMap(_.get(cat)).getOrElse(0.0)
  }

  // あるカテゴリ中のアイテムたちの数
  def catcount(cat: String): Double = {
    cc.getOrElse(cat, 0.0)
  }

  // アイテムたちの総数
  def totalCount(): Double = {
    cc.values.sum
  }

  // 全てのカテゴリたちのリスト
  def categories(): List[String] = {
    cc.keys.toList
  }

  def train(item: String, cat: String): Unit = {
    val features = getFeatures(item)
    // このカテゴリの中の特徴たちのカウントを増やす
    features.keys.foreach(incf(_, cat))
    // このカテゴリのカウントを増やす
    incc(cat)
  }

  def fprob(f: String, cat: String): Double = {
    if (catcount(cat) == 0) return 0.0
    // このカテゴリ中にこの特徴が出現する回数を、このカテゴリ中のアイテムの総数で割る
    fcount(f, cat) / catcount(cat)
  }

  def weightedprob(f: String, cat: String, prf: (String, String) => Double, weight: Double = 1.0, ap: Double = 0.5): Double = {
    // 現在の確率を計算する
    val basicprob = prf(f, cat)

    // この特徴がすべてのカテゴリ中に出現する数を数える
    val totals = categories.map(fcount(f, _)).sum

    // 重み付けした平均を計算
    ((weight * ap) + (totals * basicprob)) / (weight + totals)
  }
}
