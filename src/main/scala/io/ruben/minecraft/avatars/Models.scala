package io.ruben.minecraft.avatars

/**
 * Created by istar on 13/09/15.
 */
case class User(id: String, name: String)
case class Avatar(name: String, userId: String, locationId: Int, id: Option[Int] = None)
case class Location(x: Double, y: Double, z: Double, pitch: Float, yaw: Float, id: Option[Int] = None) {
  def toBukkit: org.bukkit.Location = {
    val location: org.bukkit.Location = org.bukkit.Location
    location.setX(x)
    location.setY(y)
    location.setZ(z)
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