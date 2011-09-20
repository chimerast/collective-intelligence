package chapter4
import org.slf4j.LoggerFactory

object Searcher extends App {
  val dburl = "jdbc:h2:/data/h2/searchindex"

  val searcher = new Searcher(dburl)
  searcher.dao.db withSession {
    // crawler.dao.createIndexTables
  }
}

class Searcher(dburl: String) {
  protected val logger = LoggerFactory.getLogger(getClass)

  val dao = new DataAccess(dburl)
}
