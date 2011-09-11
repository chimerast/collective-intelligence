object Section {
  val from = (2, 10)

  def section[T](title: String)(content: => T): T = {
    val Array(chapter, section) = title.split("(\\.| )").take(2).map(_.toInt)
    if (chapter < from._1 || (chapter == from._1 && section < from._2)) {
      return null.asInstanceOf
    }

    println("## " + title)
    val ret = content
    println()
    ret
  }

  def subsection(title: String): Unit = {
    println("### " + title)
  }

  def output(line: Any): Unit = {
    println("    " + line)
  }
}
