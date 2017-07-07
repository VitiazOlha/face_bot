package database

import play.api.Logger
import play.api.db.DB
import slick.jdbc.H2Profile.api._
import play.api.Play.current


object DBConnector {
//
//  class Article(tag: Tag) extends Table[(String, String, String)](tag, "ARTICLE") {
//    def url = column[String]("URL", O.PrimaryKey)
//    def title = column[String]("TITLE")
//    def imageURL = column[String]("IMAGE_URL")
//
//    // Every table needs a * projection with the same type as the table's type parameter
//    def * = (url, title, imageURL)
//  }
//
//  val articles = TableQuery[Article]
//
//  // Definition of the COFFEES table
//  class Tags(tag: Tag) extends Table[(String, String)](tag, "TAGS") {
//    def name = column[String]("TAG_NAME", O.PrimaryKey)
//
//    def url = column[String]("URL")
//
//    def * = (name, url)
//
//    // A reified foreign key relation that can be navigated to create a join
//    def supplier = foreignKey("URL_FK", url, articles)(_.url)
//  }
//
//  val tags = TableQuery[Tags]

  def subTag = ???

  def getData = {
    var out = ""
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT tick FROM article")

      while (rs.next) {
        out += "Read from DB: " + rs.getTimestamp("tick") + "\n"
      }
    } finally {
      conn.close()
      out
    }
  }


  def setData = ???

  def update = {
//    val db = Database.forConfig("h2mem1")
//    try {
//      Logger.debug(" 1 ")
//      Logger.debug(" 2 ")
//
//      val setup = DBIO.seq(
//        (articles.schema ++ tags.schema).create,
//        articles ++= articleLinks.map(e => (e._1, e._2, e._3)),
//        tags ++= articleLinks.flatMap(e => for (t <- e._4) yield (e._1, t))
//      )
//
//      Logger.debug(" 3 ")
//      val setupFuture = db.run(setup)
//      Logger.debug(" 4 ")
//    } finally db.close

    val articleLinks = Parser.getArticleURL.map(url => Parser.getDataByLink(url))
    var out = ""
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS article (url, title, imageURL)")
      articleLinks.map(e => {
        stmt.executeUpdate("INSERT INTO article VALUES (e._1, e._2, e._3)")
      })


      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS tags (url, tag)")
      articleLinks.foreach(e => for (t <- e._4) stmt.executeUpdate("INSERT INTO article VALUES (e._1, t)"))
    } finally {
      conn.close()
    }
  }
}

