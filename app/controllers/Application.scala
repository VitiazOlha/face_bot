package controllers

import play.api._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

object Application extends Controller {

  def index = Action { request =>
//    if (request.getQueryString("hub.verify_token").get == "vohello")
      Ok(request.getQueryString("hub.challenge").get)
//    else
//      Ok("Error")
  }

  def postQuery: Action[JsValue] = Action (parse.json) { request =>
//    val json = request.body.asJson.get
    (request.body \ "recipient_id").asOpt[String].map { recipient_id =>
      (request.body \ "message_id").asOpt[String].map { message_id =>
        Ok(Json.toJson(
          Map(
            "recipient" -> Json.toJson(Map("id" -> "recipient_id")),
            "message" -> Json.toJson(Map("text" -> "hello")))
        ))
      }.getOrElse(Ok("Error"))
    }.getOrElse(Ok("Error"))
  }
}

