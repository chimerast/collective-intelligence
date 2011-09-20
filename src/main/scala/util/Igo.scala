package util

import net.reduls.igo.Tagger
import scala.collection.JavaConversions._
import net.reduls.igo.Morpheme

object Igo {
  private val tagger = new Tagger("ipadic")

  private def parseInternal(str: String): List[Morpheme] = {
    tagger.parse(str).toList
  }

  def parse(str: String): List[(String, String)] = {
    parseInternal(str).map { m =>
      (m.surface, m.feature.split(",")(0))
    }
  }

  val DIGIT_PUNCT = """[\p{Digit}\p{Punct}０-９]+""".r

  def detectNoun(str: String): List[String] = {
    parseInternal(str).filter(_.feature.startsWith("名詞,")).filter {
      _.surface match {
        case DIGIT_PUNCT() => false
        case _ => true
      }
    }.map(_.surface)
  }
}
