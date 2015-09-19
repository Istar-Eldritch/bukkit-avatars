package io.ruben.minecraft.avatars

import io.ruben.minecraft.inventories.api.ExtraStorage
import org.bukkit.Bukkit

/**
 * Created by istar on 19/09/15.
 */

object ExtraStorageAdapter {
  val NAME: String = "ExtraStorage"
  def plugin: Option[ExtraStorage] = {
    val manager = Bukkit.getPluginManager
    if (manager.isPluginEnabled(NAME)) manager.getPlugin(NAME) match {
      case plugin: ExtraStorage => Some(plugin)
      case _ => None // Maybe there's other plugin with the same name out there.
    }
    else None
  }
}
