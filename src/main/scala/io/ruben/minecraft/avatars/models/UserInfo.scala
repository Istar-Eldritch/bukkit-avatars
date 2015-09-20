package io.ruben.minecraft.avatars.models

import java.util.UUID
import io.ruben.minecraft.avatars.DataAccess._
import driver.api._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by istar on 18/09/15.
 */
case class UserInfo(id: UUID, name: String, currentAvatar: Option[UUID] = None) {
  def setCurrentAvatar(uuid: Option[UUID]):UserInfo = UserInfo(id, name, uuid)
  def save: Future[UserInfo] = db.run(users.insertOrUpdate(this)).map{ case _ => this }
}


class Users(tag: Tag)
  extends Table[UserInfo](tag, "USER_INFO") {

  def id =  column[UUID]("id", O.PrimaryKey, O.SqlType("UUID"))
  def name = column[String]("name")
  def currentAvatar = column[Option[UUID]]("currentAvatar")
  def * = (id, name, currentAvatar) <> (UserInfo.tupled, UserInfo.unapply)
}