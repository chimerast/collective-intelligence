package chapter4

import util._

import org.scalaquery.session._
import org.scalaquery.session.Database.threadLocalSession

import org.scalaquery.ql._
import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql.extended.{ ExtendedTable => Table }
import org.scalaquery.ql.extended.H2Driver.Implicit._

object Chapter4 extends App {
  val dburl = "jdbc:h2:/data/h2/searchindex"
  val db = Database.forURL(dburl, driver = "org.h2.Driver")

  val searcher = new Searcher()
  db withSession {
    /*
    val crawler = new Crawler()
    crawler.dao.createIndexTables
    crawler.crawl(List("http://kiwitobes.com/wiki/Categorical_list_of_programming_languages.html"))
    */

    section("4.5.2 単語の頻度") {
      searcher.query("functional programming", List((1.0, searcher.frequencyScore)))
    }

    section("4.5.3 ドキュメント中での位置") {
      searcher.query("functional programming", List((1.0, searcher.locationScore)))
    }

    section("4.5.4 単語間の距離") {
      searcher.query("functional programming", List((1.0, searcher.distanceScore)))
    }

    section("4.6.1 単純に数え上げる") {
      searcher.query("functional programming", List((1.0, searcher.inboudLinkScore)))
    }

    section("4.6.2 PageRankアルゴリズム") {
      // searcher.calculatePageRank()

      subsection("PageRankを高い順に表示")
      val q = for (r <- PageRank; _ <- Query orderBy r.score.desc) yield r.*
      q.take(5).list.map(u => (searcher.dao.getUrl(u._1), u._2)).foreach(println)

      subsection("PageRankによる検索結果のスコアリング")
      val scoring = List[(Double, searcher.Scoring)](
        (1.0, searcher.locationScore),
        (1.0, searcher.frequencyScore),
        (1.0, searcher.pagerankScore))
      searcher.query("functional programming", scoring)
    }

    section("4.6.3 リンクのテキストを利用する") {
      searcher.query("functional programming", List((1.0, searcher.linkTextScore)))
    }

    val (wWorld, wRiver, wBank) = (101, 102, 103)
    val (uWorldBank, uRiver, uEarth) = (201, 202, 203)

    val net = new SearchNet()
    section("4.7.2 データベースのセットアップ") {
      (HiddenNode.ddl ++ WordHidden.ddl ++ HiddenUrl.ddl).drop
      (HiddenNode.ddl ++ WordHidden.ddl ++ HiddenUrl.ddl).create

      net.generateHiddenNode(Array(wWorld, wBank), Array(uWorldBank, uRiver, uEarth))
      subsection("SELECT * FROM wordhidden")
      (for (e <- WordHidden) yield e.*).foreach(println)
      subsection("SELECT * FROM hiddenurl")
      (for (e <- HiddenUrl) yield e.*).foreach(println)
    }

    section("4.7.3 フィードフォワード") {
      println(net.getResult(Array(wWorld, wBank), Array(uWorldBank, uRiver, uEarth)).map("%.3f" format _).mkString("Array(", ",", ")"))
    }

    section("4.7.4 バックプロパゲーションによるトレーニング") {
      net.trainQuery(Array(wWorld, wBank), Array(uWorldBank, uRiver, uEarth), uWorldBank)
      println(net.getResult(Array(wWorld, wBank), Array(uWorldBank, uRiver, uEarth)).map("%.3f" format _).mkString("Array(", ",", ")"))
    }

    section("4.7.5 トレーニングのテスト") {
      val allUrls = Array(uWorldBank, uRiver, uEarth)
      for (i <- 0 until 30) {
        net.trainQuery(Array(wWorld, wBank), allUrls, uWorldBank)
        net.trainQuery(Array(wRiver, wBank), allUrls, uRiver)
        net.trainQuery(Array(wWorld), allUrls, uEarth)
      }
      println(net.getResult(Array(wWorld, wBank), allUrls).map("%.3f" format _).mkString("Array(", ",", ")"))
      println(net.getResult(Array(wRiver, wBank), allUrls).map("%.3f" format _).mkString("Array(", ",", ")"))
      println(net.getResult(Array(wBank), allUrls).map("%.3f" format _).mkString("Array(", ",", ")"))
    }
  }
}
