package io.ruben.minecraft.avatars

import io.ruben.minecraft.avatars.models._
import org.bukkit.plugin.java.JavaPlugin
import slick.driver.H2Driver.api._


/**
 * Created by istar on 13/09/15.
 */
object DataAccess {
  val plugin:AvatarsPlugin = JavaPlugin.getPlugin(classOf[AvatarsPlugin])
  val db = Database.forURL(s"jdbc:h2:${plugin.getDataFolder.getAbsolutePath}/avatars", driver = "org.h2.Driver")
  val users = TableQuery[Users]
  val avatars = TableQuery[Avatars]
  val locations = TableQuery[Locations]
}
