package io.ruben.minecraft.avatars.models

import java.util.UUID

import io.ruben.minecraft.avatars.DataAccess._
import slick.driver.H2Driver.api._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

/**
 * Created by istar on 18/09/15.
 */
case class Avatar(name: String, userId: UUID, locationId: UUID, id: UUID = UUID.randomUUID()) {
  def save: Future[Avatar] = db.run(avatars.insertOrUpdate(this)).map[Avatar] { case _ => this }
}

class Avatars(tag: Tag)
  extends Table[Avatar](tag, "AVATAR") {

  def id = column[UUID]("id", O.PrimaryKey, O.SqlType("UUID"))
  def name = column[String]("name")
  def userId = column[UUID]("user")
  def locationId = column[UUID]("location")

  def * = (name, userId, locationId, id) <> (Avatar.tupled, Avatar.unapply)
}