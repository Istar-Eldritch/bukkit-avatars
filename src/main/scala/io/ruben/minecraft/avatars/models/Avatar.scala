package io.ruben.minecraft.avatars.models

import java.util.UUID

import io.ruben.minecraft.avatars.DataAccess._
import driver.api._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

/**
 * Created by istar on 18/09/15.
 */
case class Avatar(name: String, userId: UUID, locationId: UUID, id: UUID = UUID.randomUUID(), inventoryId: Option[UUID] = None) {
  def save: Future[Avatar] = db.run(avatars.insertOrUpdate(this)).map[Avatar] { case _ => this }
}

class Avatars(tag: Tag)
  extends Table[Avatar](tag, "AVATAR") {

  def id = column[UUID]("id", O.PrimaryKey, O.SqlType("UUID"))
  def name = column[String]("name")
  def userId = column[UUID]("user")
  def locationId = column[UUID]("location")
  def inventoryId = column[Option[UUID]]("inventory")

  def * = (name, userId, locationId, id, inventoryId) <> (Avatar.tupled, Avatar.unapply)

  def idx = index("index_name", name, unique = true)
}