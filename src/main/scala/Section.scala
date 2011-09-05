object Section {
  def apply(title: String)(content: => Unit) {
    println("** " + title)
    content
    println()
  }
}
