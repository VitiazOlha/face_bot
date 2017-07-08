package models

import play.api.Play.current
import play.api.db._
import anorm._
import anorm.SqlParser._

case class Subscription(id: Long, tag: String, user_id: Long)

object Subscription {

  val sub = {
    get[Long]("id") ~
      get[String]("tag") ~
      get[Int]("user_id") map {
      case id ~ tag ~ user_id => Subscription(id, tag, user_id)
    }
  }

  def all(): List[Subscription] = DB.withConnection { implicit c =>
    SQL("select * from subscription").as(sub *)
  }

  def create(tag: String, user_id: Long) = {
    DB.withConnection { implicit c =>
      SQL("insert into subscription (tag,user_id) values ({tag},{user_id})").on(
        'tag -> tag,
        'user_id -> user_id
      ).executeUpdate()
    }
  }

  def delete(tag: String, user_id: Long) {
    DB.withConnection { implicit c =>
      SQL("delete from subscription where tag = {tag} and user_id = {user_id}").on(
        'tag -> tag,
        'user_id->user_id
      ).executeUpdate()
    }
  }

  def get_sub(user_id: Long): List[Subscription] = DB.withConnection { implicit c =>
    SQL("select * from subscription where user_id = {user_id}").on('user_id->user_id).as(sub *)
  }
}