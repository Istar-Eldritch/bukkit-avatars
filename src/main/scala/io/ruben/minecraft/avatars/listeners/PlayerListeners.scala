package io.ruben.minecraft.avatars.listeners

import io.ruben.minecraft.avatars.DataAccess._
import io.ruben.minecraft.avatars.events.AvatarQuitEvent
import io.ruben.minecraft.avatars.models._
import org.bukkit.Bukkit
import org.bukkit.event.player.{PlayerJoinEvent, PlayerQuitEvent}
import org.bukkit.event.{EventHandler, Listener}
import driver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
 * Created by istar on 13/09/15.
 */
object PlayerListeners extends Listener {

  @EventHandler
  def onUserJoin(playerJoinEvent: PlayerJoinEvent): Unit = {
    val player = playerJoinEvent.getPlayer

    //TODO Hide the user join message

    val query = users.filter(_.id === player.getUniqueId)
    db.run(query.result.head).onComplete {
      case Success(user) =>
        player.sendMessage("Switch to your avatar with /avatars sw")
        //TODO Force the user to use an avatar

      case Failure(f) =>
        player.sendMessage("You don't have any avatars, create one with /avatars cr")
        UserInfo(player.getUniqueId, player.getName).save.onFailure {
          case err => err.printStackTrace();
        }
        //TODO Force the user to register a new avatar

    }
  }

  @EventHandler
  def onUserLeave(playerQuitEvent: PlayerQuitEvent): Unit = {
    val player = playerQuitEvent.getPlayer
    val playerId = player.getUniqueId
    db.run(avatars.filter(_.userId === playerId).result.head).onSuccess {
      case avatar: Avatar => Bukkit.getPluginManager.callEvent(AvatarQuitEvent(player, avatar))
    }
  }
}
