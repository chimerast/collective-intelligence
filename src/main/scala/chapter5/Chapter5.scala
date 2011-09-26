package chapter5

import scala.collection.mutable.HashMap
import scala.collection.mutable.Map
import scala.collection.mutable.Set
import scala.collection.mutable.MultiMap
import scala.collection.mutable.ArrayBuffer
import scala.io._
import scala.math._
import scala.util.Random
import scala.util.control.Breaks._

import util._

object Chapter5 extends App {
  val people = Array(
    ("Seymour", "BOS"),
    ("Franny", "DAL"),
    ("Zooey", "CAK"),
    ("Walt", "MIA"),
    ("Buddy", "ORD"),
    ("Les", "OMA"))

  val destination = "LGA"

  val flights = using(Source.fromFile("schedule.txt")) { source =>
    val flights = new HashMap[(String, String), Array[(String, String, Int)]]
    for (line <- source.getLines) {
      val Array(origin, dest, depart, arrive, price, _@ _*) = line.split(",")

      flights((origin, dest)) =
        flights.getOrElse((origin, dest), Array()) :+ (depart, arrive, price.toInt)
    }
    flights.map { case (key, value) => (key, value.toArray) }
  }

  def getMinutes(t: String): Int = {
    val Array(hour, minute, _@ _*) = t.split(":")
    hour.toInt * 60 + minute.toInt
  }

  def printScedule(r: Array[Int]): Unit = {
    for (d <- 0 until r.size / 2) {
      val name = people(d)._1
      val origin = people(d)._2
      val out = flights((origin, destination))(r(d * 2))
      val ret = flights((destination, origin))(r(d * 2 + 1))
      printf("%10s%10s %5s-%5s $%3d %5s-%5s $%3d\n",
        name, origin, out._1, out._2, out._3, ret._1, ret._2, ret._3)
    }
  }

  val s = Array(1, 4, 3, 2, 7, 3, 6, 3, 2, 4, 5, 3)

  section("5.2 解の表現") {
    printScedule(s)
  }

  def scheduleCost(r: Array[Int]): Int = {
    var totalprice = 0
    var latestarrival = 0
    var earliestdep = 24 * 60

    for (d <- 0 until r.size / 2) {
      // 行きと帰りのフライトを得る
      val origin = people(d)._2
      val out = flights((origin, destination))(r(d * 2))
      val ret = flights((destination, origin))(r(d * 2 + 1))

      // 運賃総額total pliceは出立便と帰宅便すべての運賃
      totalprice += out._3
      totalprice += ret._3

      // 最も遅い到着と最も速い到着を記録
      if (latestarrival < getMinutes(out._2)) latestarrival = getMinutes(out._2)
      if (earliestdep > getMinutes(ret._1)) earliestdep = getMinutes(ret._1)
    }

    // 最後の人が到着するまで全員空港で待機。
    // 帰りも空港にみんなできて自分の便を待たねばならない。
    var totalwait = 0
    for (d <- 0 until r.size / 2) {
      val origin = people(d)._2
      val out = flights((origin, destination))(r(d * 2))
      val ret = flights((destination, origin))(r(d * 2 + 1))

      totalwait += latestarrival - getMinutes(out._2)
      totalwait += getMinutes(ret._1) - earliestdep
    }

    totalprice += totalwait

    // この解ではレンタカーの追加料金が必要か？これは50ドル！
    if (latestarrival < earliestdep) totalprice += 50

    totalprice
  }

  section("5.3 コスト関数") {
    println(scheduleCost(s))
  }

  type Cost = (Array[Int]) => Int

  def randomOptimize(domain: Array[(Int, Int)], costf: Cost): Array[Int] = {
    var best = Int.MaxValue
    var bestr = Array[Int]()

    for (i <- 0 until 1000) {
      // 無作為解の生成
      val r = Array.tabulate(domain.size)(i => domain(i)._1 + Random.nextInt(domain(i)._2 - domain(i)._1))

      // コストの取得
      val cost = costf(r)

      // 最良会と比較
      if (cost < best) {
        best = cost
        bestr = r
      }
    }

    return bestr
  }

  section("5.4 ランダムサーチ") {
    val domain = Array.fill(people.size * 2)((0, 9))
    val s = randomOptimize(domain, scheduleCost)
    println(scheduleCost(s))
    printScedule(s)
  }

  def hillclimbOptimize(domain: Array[(Int, Int)], costf: Cost): Array[Int] = {
    // 無作為解の生成
    var sol = Array.tabulate(domain.size)(i => domain(i)._1 + Random.nextInt(domain(i)._2 - domain(i)._1))

    val neighbors = ArrayBuffer[Array[Int]]()
    breakable {
      while (true) {
        // 近傍解リストの生成
        for (j <- domain.indices) {
          if (sol(j) - 1 >= domain(j)._1)
            neighbors += sol.take(j) ++ Array(sol(j) - 1) ++ sol.drop(j + 1)
          if (sol(j) + 1 < domain(j)._2)
            neighbors += sol.take(j) ++ Array(sol(j) + 1) ++ sol.drop(j + 1)
        }

        val current = costf(sol)
        var best = current

        for (neighbor <- neighbors) {
          val cost = costf(neighbor)
          if (cost < best) {
            best = cost
            sol = neighbor
          }
        }

        if (best == current)
          break
      }
    }

    sol
  }

  section("5.5 ヒルクライム") {
    val domain = Array.fill(people.size * 2)((0, 9))
    val s = hillclimbOptimize(domain, scheduleCost)
    println(scheduleCost(s))
    printScedule(s)
  }

  def annealingOptimize(domain: Array[(Int, Int)], costf: Cost, T: Double = 10000.0, cool: Double = 0.95, step: Int = 1): Array[Int] = {
    // ランダムな値で解を初期化
    var sol = Array.tabulate(domain.size)(i => domain(i)._1 + Random.nextInt(domain(i)._2 - domain(i)._1))

    var t = T
    while (t > 0.1) {
      // インデックスを一つ選ぶ
      val i = Random.nextInt(domain.size)

      // インデックスの値に加える変更の方向を選ぶ
      val dir = -step + Random.nextInt(step * 2 + 1)

      // 値を変更した解を生成
      val newsol = sol.clone
      newsol(i) += dir
      if (newsol(i) < domain(i)._1)
        newsol(i) = domain(i)._1
      else if (newsol(i) >= domain(i)._2)
        newsol(i) = domain(i)._2 - 1

      val costold = costf(sol)
      val costnew = costf(newsol)
      val p = exp(-abs(costnew - costold) / t)

      if (costnew < costold || random < p)
        sol = newsol

      t *= cool
    }

    sol
  }

  section("5.6 模擬アニーリング") {
    val domain = Array.fill(people.size * 2)((0, 9))
    val s = annealingOptimize(domain, scheduleCost)
    println(scheduleCost(s))
    printScedule(s)
  }
}
