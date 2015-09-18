package io.ruben.minecraft.avatars

import java.util.logging.Level

import io.ruben.minecraft.avatars.events.{AvatarQuitEvent, AvatarCreatedEvent, AvatarLoginEvent}
import org.bukkit.Bukkit
import org.bukkit.command.{Command, CommandSender, CommandExecutor}
import org.bukkit.entity.Player

import DataAccess._
import slick.driver.H2Driver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
 * Created by istar on 13/09/15.
 */
object Commands extends CommandExecutor {
  override def onCommand(commandSender: CommandSender, command: Command, s: String, arguments: Array[String]): Boolean = {

    commandSender match {
      case player: Player =>
        val playerId = player.getUniqueId.toString

        arguments.headOption match {
          case Some(cmd) =>

            cmd match {
              case "create" | "cr" =>

                arguments.tail.headOption match {
                  case Some(name) =>
                    val location: Location = Location.fromBukkit(player.getLocation)
                    db.run((locations returning locations.map(_.id)) += location).onSuccess {
                      case locationId: Int =>
                        val newAvatar = Avatar(name, playerId, locationId)
                        db.run((avatars returning avatars.map(_.id)) += newAvatar).onSuccess {
                          case idAvatar: Int =>
                            val avatar = Avatar(name, playerId, locationId, Option(idAvatar))
                            Bukkit.getPluginManager.callEvent(AvatarCreatedEvent(player, avatar))
                        }
                    }
                  case None => player.sendMessage("You have to specify a name")
                }


              case "list" | "ls" =>
                db.run(userAvatars(playerId).map(_.name).result).onSuccess {
                  case nameSeq: Seq[String] => player.sendMessage(nameSeq.mkString("\n"))
                }
              case "switch" | "sw" =>

                arguments.tail.headOption match {
                  case Some(name) =>

                    db.run(avatars.filter(_.userId === playerId).filter(_.name === name).result.headOption).map {
                      case Some(avatar) =>
                        val oldName = player.getDisplayName

                        db.run(avatars.filter(_.userId === playerId).filter(_.name === oldName).result.headOption).map {
                          case Some(oldAvatar) =>
                            if(oldAvatar.id == avatar.id) {
                              player.sendMessage(s"You are already playing as ${avatar.name}")
                            } else {
                              Bukkit.getServer.getPluginManager.callEvent(AvatarQuitEvent(player, oldAvatar))
                              Bukkit.getServer.getPluginManager.callEvent(AvatarLoginEvent(player, avatar))
                            }
                          case None =>
                            Bukkit.getServer.getPluginManager.callEvent(AvatarLoginEvent(player, avatar))
                        }

                      case None => player.sendMessage(s"You don't have an avatar named $name")
                    }
                  case None => player.sendMessage("You have to specify a name")
                }

              case _ => player.sendMessage("The subcommand is not valid")
            }

          case None => player.sendMessage(s"${command.getUsage}\nYou have to specify a subcommand!")
        }
      case _ => commandSender.sendMessage("You are not a player!")
    }

    true
  }


}