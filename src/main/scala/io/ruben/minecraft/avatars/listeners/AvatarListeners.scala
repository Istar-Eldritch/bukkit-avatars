package io.ruben.minecraft.avatars.listeners

import java.util.logging.Level._

import io.ruben.minecraft.avatars.Location
import io.ruben.minecraft.avatars.events.{AvatarQuitEvent, AvatarCreatedEvent, AvatarLoginEvent}
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
    val avatar = avatarLoginEvent.avatar

    player.setDisplayName(avatar.name)
    player.setPlayerListName(avatar.name)
    player.sendMessage(s"Switched to ${avatar.name}")
    //TODO Broadcast avatar connection

    db.run(locations.filter(_.id === avatar.locationId).result.head).onComplete {
      case Success(location) => player.teleport(location.toBukkit)
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
    val currentLoc = Location.fromBukkit(event.player.getLocation).copy(id=Some(event.avatar.locationId))
    val query = for {
      l <- locations if l.id === event.avatar.locationId
    } yield l
    db.run(query.update(currentLoc)).map(
      _ => None)
  }

}
