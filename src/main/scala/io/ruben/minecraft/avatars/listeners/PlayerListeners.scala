package io.ruben.minecraft.avatars.listeners

import io.ruben.minecraft.avatars.DataAccess._
import io.ruben.minecraft.avatars.events.AvatarQuitEvent
import io.ruben.minecraft.avatars.{Avatar, AvatarsPlugin, Location, User}
import org.bukkit.Bukkit
import org.bukkit.event.player.{PlayerJoinEvent, PlayerQuitEvent}
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.plugin.java.JavaPlugin
import slick.driver.H2Driver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
 * Created by istar on 13/09/15.
 */
object PlayerListeners extends Listener {
  val plugin: AvatarsPlugin = JavaPlugin.getPlugin(classOf[AvatarsPlugin])

  @EventHandler
  def onUserJoin(playerJoinEvent: PlayerJoinEvent): Unit = {
    val player = playerJoinEvent.getPlayer

    val query = users.filter(_.id === player.getUniqueId.toString)
    db.run(query.result.head).onComplete {
      case Success(user) =>
        player.sendMessage("I already know you")
        //TODO Force the user to use an avatar

      case Failure(f) =>
        player.sendMessage("You are new in this lands")
        db.run(DBIO.seq(users += User(player.getUniqueId.toString, player.getName)))
        //TODO Force the user to register a new avatar

    }
  }

  @EventHandler
  def onUserLeave(playerQuitEvent: PlayerQuitEvent): Unit = {
    val player = playerQuitEvent.getPlayer
    val playerId = player.getUniqueId.toString
    db.run(avatars.filter(_.userId === playerId).result.head).onSuccess {
      case avatar: Avatar => Bukkit.getPluginManager.callEvent(AvatarQuitEvent(player, avatar))
    }
  }
}
