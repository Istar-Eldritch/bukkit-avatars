package io.ruben.minecraft.avatars.models

import java.util.UUID

import io.ruben.minecraft.avatars.DataAccess._
import org.bukkit.{World, Bukkit}
import driver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by istar on 13/09/15.
 */
case class Location(world: String, x: Double, y: Double, z: Double, pitch: Float, yaw: Float, id: UUID = UUID.randomUUID()) {

  def save: Future[Location] = db.run(locations.insertOrUpdate(this)).map { _ => this }

  def toBukkit: org.bukkit.Location = {

    val bukkitWorld:World = Bukkit.getServer.getWorld(world)

    val location: org.bukkit.Location = new org.bukkit.Location(bukkitWorld, x, y, z)

    location.setPitch(pitch)
    location.setYaw(yaw)

    location
  }
}

object Location {
  def fromBukkit(bukkitLocation: org.bukkit.Location): Location = {
    val world = bukkitLocation.getWorld.getName
    val x = bukkitLocation.getX
    val y = bukkitLocation.getY
    val z = bukkitLocation.getZ
    val pitch = bukkitLocation.getPitch
    val yaw = bukkitLocation.getYaw

    Location(world, x, y, z, pitch, yaw)
  }

}

class Locations(tag: Tag)
  extends Table[Location](tag, "LOCATIONS") {

  def id = column[UUID]("id", O.PrimaryKey, O.SqlType("UUID"))
  def world = column[String]("world")
  def x = column[Double]("x")
  def y = column[Double]("y")
  def z = column[Double]("z")
  def pitch = column[Float]("pitch")
  def yaw = column[Float]("yaw")

  def * = (world, x, y, z, pitch, yaw, id) <> ((Location.apply _).tupled, Location.unapply)
}