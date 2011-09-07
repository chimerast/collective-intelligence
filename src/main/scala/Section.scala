object Section {
  def section(title: String)(content: => Unit) {
    println("## " + title)
    content
    println()
  }

  def subsection(title: String) {
    println("### " + title)
  }
}
