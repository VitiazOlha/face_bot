package database

import org.joda.time.{DateTime, Hours}
import play.api.Logger
import slick.jdbc.H2Profile.api._

object DBConnector {

  class Article(tag: Tag) extends Table[(String, String, String)](tag, "ARTICLE") {
    def url = column[String]("URL", O.PrimaryKey)

    // This is the primary key column
    def title = column[String]("TITLE")

    def imageURL = column[String]("IMAGE_URL")

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (url, title, imageURL)
  }

  val articles = TableQuery[Article]

  // Definition of the COFFEES table
  class Tags(tag: Tag) extends Table[(String, String)](tag, "TAGS") {
    def name = column[String]("TAG_NAME", O.PrimaryKey)

    def url = column[String]("URL")

    def * = (name, url)

    // A reified foreign key relation that can be navigated to create a join
    def supplier = foreignKey("URL_FK", url, articles)(_.url)
  }

  val tags = TableQuery[Tags]

  def subTag = ???

  def getData = ???

  def setData = ???

  def update = {
    val db = Database.forConfig("h2mem1")
    try {
      Logger.debug(" 0 ")
      val d1 = DateTime.now
      val articleL = Parser.getArticleURL
      val d2 = Hours.hoursBetween(d1, DateTime.now)
      Logger.debug(s"$d2")
      val articleLinks = articleL.map(url => Parser.getDataByLink(url))
      Logger.debug(" 1 ")
      val setup = DBIO.seq(
        (articles.schema ++ tags.schema).create,
        articles ++= articleLinks.map(e => (e._1, e._2, e._3)),
        tags ++= articleLinks.flatMap(e => for (t <- e._4) yield (e._1, t))
      )
      Logger.debug(" 2 ")

      val setupFuture = db.run(setup)
      "OK"
    } catch {
      case _ => "ERR"
    }    finally db.close
  }
}

