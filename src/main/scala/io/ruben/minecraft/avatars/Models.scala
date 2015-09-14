package io.ruben.minecraft.avatars

import org.bukkit.{Bukkit, World}

/**
 * Created by istar on 13/09/15.
 */
case class User(id: String, name: String)
case class Avatar(name: String, userId: String, locationId: Int, id: Option[Int] = None)
case class Location(world: String, x: Double, y: Double, z: Double, pitch: Float, yaw: Float, id: Option[Int] = None) {

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