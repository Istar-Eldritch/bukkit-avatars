package io.ruben.minecraft.avatars.listeners

import java.util.logging.Level._
import io.ruben.minecraft.avatars.events.{AvatarQuitEvent, AvatarCreatedEvent, AvatarLoginEvent}
import io.ruben.minecraft.avatars.models.{UserInfo, Location}
import io.ruben.minecraft.avatars.ExtraStorageAdapter
import org.bukkit.event.{EventHandler, Listener}

import io.ruben.minecraft.avatars.DataAccess._
import driver.api._
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

    avatar.inventoryId.collect {
      case id =>
        ExtraStorageAdapter.plugin.collect {
          case storage =>
            storage.getData.get(id).onComplete {
              case Success(inv) =>
                val playerInv = player.getInventory
                playerInv.setContents(inv.contents)
                inv.armor.collect { case arm => playerInv.setArmorContents(arm) }
              case Failure(err) => err.printStackTrace()
            }
        }
    }

    db.run(users.filter(_.id === playerId).result.head).onSuccess {
      case user => user.copy(currentAvatar = Some(avatar.id)).save
    }

    db.run(locations.filter(_.id === avatar.locationId).result.head).onComplete {
      case Success(location) =>
        player.teleport(location.toBukkit)

      case Failure(err) =>
        err.printStackTrace()
        plugin.getLogger.log(SEVERE, err.getMessage)
    }
  }

  @EventHandler
  def onAvatarCreated(event: AvatarCreatedEvent): Unit = {
    event.player.sendMessage(s"The avatar ${event.avatar.name} was created")
    ExtraStorageAdapter.plugin.collect {
      case storage =>
        storage.getData.create.save.onSuccess {
          case newInv => event.avatar.copy(inventoryId = Some(newInv.id)).save
        }
    }
  }

  @EventHandler
  def onAvatarQuit(event: AvatarQuitEvent): Unit = {
    Location.fromBukkit(event.player.getLocation).copy(id=event.avatar.locationId).save
    val contents = event.player.getInventory.getContents
    val armor = event.player.getInventory.getArmorContents
    ExtraStorageAdapter.plugin.collect {
      case storage =>
        event.avatar.inventoryId match {
          case Some(id) =>
            storage.getData.get(id).onComplete {
              case Success(oldInv) => oldInv.setContents(contents).setArmor(Some(armor)).save
              case Failure(err) => err.printStackTrace()
            }
          case None =>
            storage.getData.create.setContents(contents).setArmor(Some(armor)).save.onSuccess {
              case newInv => event.avatar.copy(inventoryId = Some(newInv.id)).save
            }
        }
    }
  }

}
