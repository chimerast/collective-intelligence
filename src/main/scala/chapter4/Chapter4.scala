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

  val searcher = new Searcher(dburl)
  searcher.dao.db withSession {
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

    val net = new SearchNet(dburl)
    section("4.7.2 データベースのセットアップ") {
      // (HiddenNode.ddl ++ WordHidden.ddl ++ HiddenUrl.ddl).create

      net.generateHiddenNode(Array(101, 103), Array(201, 202, 203))
      subsection("SELECT * FROM wordhidden")
      (for (e <- WordHidden) yield e.*).foreach(println)
      subsection("SELECT * FROM hiddenurl")
      (for (e <- HiddenUrl) yield e.*).foreach(println)
    }
  }
}
