package io.ruben.minecraft.avatars

import slick.driver.H2Driver.api._

/**
 * Created by istar on 13/09/15.
 */
class Users(tag: Tag)
  extends Table[User](tag, "USER") {

  def id =  column[String]("id", O.PrimaryKey)
  def name = column[String]("name")
  def * = (id, name) <> (User.tupled, User.unapply)
}

class Avatars(tag: Tag)
  extends Table[Avatar](tag, "AVATAR") {

  def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name: Rep[String] = column[String]("name")
  def userId = column[String]("user")
  def locationId = column[Int]("location")

  def * = (name, userId, locationId, id.?) <> (Avatar.tupled, Avatar.unapply)

  def user = foreignKey("user", userId, TableQuery[Users])(_.id)
}

class Locations(tag: Tag)
  extends Table[Location](tag, "LOCATIONS") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def x = column[Int]("x")
  def y = column[Int]("y")
  def z = column[Int]("z")
  def pitch = column[Float]("pitch")
  def yaw = column[Float]("yaw")

  def * = (x, y, z, pitch, yaw, id.?) <> (Location.tupled, Location.unapply)
}

