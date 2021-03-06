package parser

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._

import scala.util.Try

object Parser {
  val browser = JsoupBrowser()
  val pageURL = "https://tproger.ru/category/news/"
  val pageURLtag = "https://tproger.ru/tag/"

  def getArticleLink(pageURL: String): List[String] = {
    val doc = browser.get(pageURL)
    doc >?> attr("href")("link[rel=next]") match {
      case None => doc >> elementList(".entry-title") >> attr("href")("a")
      case a => (doc >> elementList(".entry-title") >> attr("href")("a")) ::: getArticleLink(a.get)
    }
  }

  def getArticleURL: List[String] = {
    //todo rewrite - get only today's news
    getArticleLink(pageURL)
  }

  def getArticleURLbyTag(tag: String): List[String] = {
    Try(getArticleLink(pageURLtag + tag + "/")).getOrElse(Nil)
  }

  def getDataByLink(pageURL: String) = {
    try {
      val doc = browser.get(pageURL)
      val title = doc >> text("h1[class=entry-title]")
      val imageURL = doc >> attr("href")("a[rel=bookmark]")
      val tags = (doc >> element("footer[class=entry-meta clearfix]") >> element("ul") >> texts("a")).filterNot(_ == "")
      (pageURL, title, imageURL, tags)
    } catch {
      case _ => ("", "", "", Nil)
    }
  }
}