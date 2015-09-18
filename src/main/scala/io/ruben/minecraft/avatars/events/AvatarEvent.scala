package io.ruben.minecraft.avatars.events

import io.ruben.minecraft.avatars.models.Avatar
import org.bukkit.entity.Player
import org.bukkit.event.Event

/**
 * Created by istar on 14/09/15.
 */
trait AvatarEvent extends Event {
  val avatar: Avatar
  val player: Player
}

