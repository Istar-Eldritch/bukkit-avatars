package io.ruben.minecraft.avatars.events

import io.ruben.minecraft.avatars.models.Avatar
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

/**
 * Created by istar on 14/09/15.
 */
case class AvatarQuitEvent(player: Player, avatar: Avatar) extends AvatarEvent {
  override def getHandlers: HandlerList = AvatarQuitEvent.getHandlerList
}

object AvatarQuitEvent {
  private[this] val handlers = new HandlerList
  def getHandlerList: HandlerList = handlers
}
