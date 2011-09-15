package util

import net.reduls.igo.Tagger
import scala.collection.JavaConversions._
import net.reduls.igo.Morpheme

object Igo {
  private val tagger = new Tagger("ipadic")

  def parse(str: String): List[Morpheme] = {
    tagger.parse(str).toList
  }

  val DIGIT_PUNCT = """[\p{Digit}\p{Punct}０-９]+""".r

  def detectNoun(str: String): List[String] = {
    parse(str).filter(_.feature.startsWith("名詞,")).filter {
      _.surface match {
        case DIGIT_PUNCT() => false
        case _ => true
      }
    }.map(_.surface)
  }
}
