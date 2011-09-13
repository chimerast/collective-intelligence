package chapter3

import scala.collection.mutable.Map
import scala.io._

import java.io._

import util._

object GenerateFeedVector extends App {
  implicit val codec = Codec.string2codec("UTF-8")

  generate()

  def generate(infile: String = "feedlist.txt", outfile: String = "blogdata.txt"): Unit = {
    val apcount = Map[String, Int]().withDefaultValue(0)
    val wordcounts = Map[String, Map[String, Int]]()
    val feedlist = using(Source.fromFile(infile)) { _.getLines.toList }
    var feedcount = 0

    for (feedurl <- feedlist) {
      try {
        val (title, wc) = RssFeeder.getWordCounts(feedurl)
        wordcounts(title) = wc
        for ((word, count) <- wc if count > 1) {
          apcount(word) += 1
        }
        feedcount += 1
      } catch {
        case e =>
          println("Failed to parse feed %s".format(feedurl))
      }
    }

    val wordlist = for (
      (w, bc) <- apcount;
      frac = bc.toDouble / feedcount;
      if frac > 0.1 && frac < 0.5
    ) yield w

    using(new PrintWriter(outfile, "UTF-8")) { out =>
      out.print("Blog")
      wordlist.foreach(word => out.print("\t%s".format(word)))
      out.println()
      for ((blog, wc) <- wordcounts) {
        out.print(blog)
        wordlist.foreach(word => out.print("\t%d" format wc(word)))
        out.println()
      }
    }
  }
}
