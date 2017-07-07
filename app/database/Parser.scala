package database

import net.ruippeixotog.scalascraper.browser.{_, JsoupBrowser}
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._

object Parser {
  val browser = JsoupBrowser()
  val pageURL = "https://tproger.ru/category/news/"

  def getArticleLink(pageURL: String): List[String] = {
    val doc = browser.get(pageURL)
    doc >?> attr("href")("link[rel=next]") match {
      case None => doc >> elementList(".entry-title") >> attr("href")("a")
      case a => (doc >> elementList(".entry-title") >> attr("href")("a")) ::: getArticleLink(a.get)
    }
  }

  def getArticleURL: List[String] = {
    getArticleLink(pageURL)
  }

  def getDataByLink(pageURL: String): (String, String, String, Iterable[String]) = {
    val doc = browser.get(pageURL)
    val title = doc >> text("h1[class=entry-title]")
    val imageURL = doc >> attr("href")("a[rel=bookmark]")
    val tags = (doc >> element("footer[class=entry-meta clearfix]") >> element("ul") >> texts("a")).filterNot(_ == "")
    (pageURL, title, imageURL, tags)
  }

  def get_all: List[String] = {
    getArticleURL
  }
}