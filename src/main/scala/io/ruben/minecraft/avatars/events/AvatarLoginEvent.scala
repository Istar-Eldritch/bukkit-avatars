package io.ruben.minecraft.avatars.events

import io.ruben.minecraft.avatars.models.Avatar
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

/**
 * Created by istar on 14/09/15.
 */

case class AvatarLoginEvent(player: Player, avatar: Avatar) extends AvatarEvent {
  override def getHandlers: HandlerList = AvatarLoginEvent.getHandlerList
}

object AvatarLoginEvent {
  private[this] val handlers = new HandlerList
  def getHandlerList: HandlerList = handlers
}