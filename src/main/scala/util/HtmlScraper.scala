package util

import java.lang.CharSequence
import scala.collection.JavaConversions._
import org.jaxen.jericho.DocumentNavigator
import org.jaxen.jericho.JerichoXPath
import net.htmlparser.jericho.Segment
import net.htmlparser.jericho.Source
import java.io.IOException

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
  def apply(url: String): Segment = {
    DocumentNavigator.getInstance.getDocument(url).asInstanceOf[Source] match {
      case null =>
        throw new IOException("Could not open URL: " + url)
      case segment =>
        segment
    }
  }

  def parse(text: CharSequence): Segment = {
    val doc = new Source(text)
    doc.fullSequentialParse
    doc
  }

  implicit def segmentWrapper(segment: Segment) = new HtmlScraper(segment)
}
