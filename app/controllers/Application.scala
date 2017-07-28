package controllers

import play.api.Play.current
import play.api.mvc._
import play.api.libs.json.{Json, _}
import play.api.libs.ws.{WS, WSResponse}
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future
import parser.Parser

object Application extends Controller {

  val PAT = "EAAESnta9mIwBANXgf6NVOSDsfujMwjM60kf778WGXwAKTbB0CdaZBQL2m10sHPODJq6IOiineaYBjxdGhqnyr6h3DkMOIZBEqQo3xOOouUnqy0YZCfiy3b6H6I1MXoptxvxnkmGRInneOZC7A4kGZB4vZAaF1ZCSkDv4mZAMuex1tQZDZD"

  def index = Action {
    Ok(models.Subscription.all().toString())
  }

  def getQuery = Action { request =>
    if (request.getQueryString("hub.verify_token").get == "vohello")
      Ok(request.getQueryString("hub.challenge").get)
    else
      Ok("Error")
  }

  def getMessage(sender: BigDecimal, text: String): Future[WSResponse] = text match {
    case "hello" =>
      sendMessege(sender, Json.toJson(Map("text" -> "Hello!")))
    case "help" =>
      sendMessege(sender, Json.toJson(Map("text" -> "Hello, i`m TProger bot.\n@\"tag\" - subscribe on news\n#\"tag\" - get all news about some teg")))
    case a if a.charAt(0) == '@' =>
      sendMessege(sender, Json.toJson(Map("text" -> ("sub " + a.tail))))
    case a if a.charAt(0) == '#' => {
      val urls = Parser.getArticleURLbyTag(a.tail)
      if (urls.nonEmpty) {
        for (i <- urls.sliding(5, 5)) {
          val res = sendMessege(sender, Json.toJson(Map("text" -> i.mkString("\n"))))
          res.map { jsResponse =>
            Ok(jsResponse.body)
          }
        }
        sendMessege(sender, Json.toJson(Map("text" -> "")))
      } else
        sendMessege(sender, Json.toJson(Map("text" -> "Inexisting tag")))
    }
    case _ =>
      sendMessege(sender, Json.toJson(Map("text" -> "Unknown command")))
  }

  def sendMessege(sender: BigDecimal, message: JsValue): Future[WSResponse] = {
    WS.url(s"https://graph.facebook.com/v2.6/me/messages?access_token=$PAT")
      .withHeaders("Content-Type" -> "application/json")
      .withRequestTimeout(1000)
      .post(Json.toJson(Map("recipient" -> Json.toJson(Map("id" -> JsNumber(sender))), "message" -> message)))
  }

  def postQuery: Action[JsValue] = Action.async(parse.json) { request =>
    val json = Json.toJson(request.body)
    val text = (((json \ "entry") (0) \ "messaging") (0) \ "message" \ "text").as[String]
    val sender = (((json \ "entry") (0) \ "messaging") (0) \ "sender" \ "id").as[BigDecimal]

    val res = getMessage(sender, text)

    res.map { jsResponse =>
      Ok(jsResponse.body)
    }
  }
}

