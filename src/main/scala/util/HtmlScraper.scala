package util

import java.lang.CharSequence

import scala.collection.JavaConversions._

import org.jaxen.jericho.DocumentNavigator
import org.jaxen.jericho.JerichoXPath

import net.htmlparser.jericho.Segment
import net.htmlparser.jericho.Source

class HtmlScraper(segment: Segment) {
  def eval(xpath: String): List[AnyRef] = {
    new JerichoXPath(xpath).evaluate(segment) match {
      case list: java.util.List[AnyRef] =>
        list.toList
      case elem =>
        List(elem)
    }
  }
}

object HtmlScraper {
  def apply(url: String, args: Any*): Segment = {
    DocumentNavigator.getInstance.getDocument(url.format(args: _*)).asInstanceOf[Source]
  }

  def parse(text: CharSequence): Segment = {
    val doc = new Source(text)
    doc.fullSequentialParse
    doc
  }

  implicit def segmentWrapper(segment: Segment) = new HtmlScraper(segment)
}
