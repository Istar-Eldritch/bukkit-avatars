package io.ruben.minecraft.avatars.listeners

import java.util.logging.Level._
import io.ruben.minecraft.avatars.events.{AvatarQuitEvent, AvatarCreatedEvent, AvatarLoginEvent}
import io.ruben.minecraft.avatars.models.{UserInfo, Location}
import org.bukkit.event.{EventHandler, Listener}

import io.ruben.minecraft.avatars.DataAccess._
import slick.driver.H2Driver.api._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.util.{Failure, Success}


/**
 * Created by istar on 14/09/15.
 */
object AvatarListeners extends Listener {

  @EventHandler
  def onAvatarLogin(avatarLoginEvent: AvatarLoginEvent): Unit = {
    val player = avatarLoginEvent.player
    val playerId = player.getUniqueId
    val avatar = avatarLoginEvent.avatar

    player.setDisplayName(avatar.name)
    player.setPlayerListName(avatar.name)
    player.sendMessage(s"Switched to ${avatar.name}")
    //TODO Broadcast avatar connection

    db.run(locations.filter(_.id === avatar.locationId).result.head).onComplete {
      case Success(location) =>
        player.teleport(location.toBukkit)
        db.run(users.filter(_.id === playerId).result.head).onSuccess {
          case user => user.copy(currentAvatar = Some(avatar.id)).save
        }

      case Failure(err) =>
        err.printStackTrace()
        plugin.getLogger.log(SEVERE, err.getMessage)
    }
  }

  @EventHandler
  def onAvatarCreated(event: AvatarCreatedEvent): Unit =
    event.player.sendMessage(s"The avatar ${event.avatar.name} was created")

  @EventHandler
  def onAvatarQuit(event: AvatarQuitEvent): Unit = {
    Location.fromBukkit(event.player.getLocation).copy(id=event.avatar.locationId).save
  }

}
