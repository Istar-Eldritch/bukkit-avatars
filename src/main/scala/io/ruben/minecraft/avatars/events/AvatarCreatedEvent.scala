package io.ruben.minecraft.avatars.events

import io.ruben.minecraft.avatars.Avatar
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

/**
 * Created by istar on 14/09/15.
 */
case class AvatarCreatedEvent(player: Player, avatar: Avatar) extends AvatarEvent {
  override def getHandlers: HandlerList = AvatarCreatedEvent.getHandlerList
}

object AvatarCreatedEvent {
  private[this] val handlers = new HandlerList

  def getHandlerList: HandlerList = handlers
}
