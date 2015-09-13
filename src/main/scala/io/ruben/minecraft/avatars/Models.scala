package io.ruben.minecraft.avatars

import org.bukkit.{Bukkit, World}
import org.bukkit.plugin.java.JavaPlugin

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

    return location
  }
}

//object Location {
//
//  def fromBukkit(location: org.bukkit.Location): Location = {
//    val x = location.getX
//    val y = location.getY
//    val z = location.getZ
//    val pitch = location.getPitch
//    val yaw = location.getYaw
//    Location(x, y, z, pitch, yaw)
//  }
//
//}