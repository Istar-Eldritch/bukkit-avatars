package io.ruben.minecraft.avatars

import io.ruben.minecraft.ScalaLang
import io.ruben.minecraft.avatars.models._
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin


/**
 * Created by istar on 13/09/15.
 */
object DataAccess {
  val plugin:AvatarsPlugin = JavaPlugin.getPlugin(classOf[AvatarsPlugin])
  val scalaLang = Bukkit.getPluginManager.getPlugin("ScalaLang").asInstanceOf[ScalaLang]
  val db = scalaLang.getDb
  val driver = scalaLang.getDriver
  import driver.api._
  val users = TableQuery[Users]
  val avatars = TableQuery[Avatars]
  val locations = TableQuery[Locations]
}
