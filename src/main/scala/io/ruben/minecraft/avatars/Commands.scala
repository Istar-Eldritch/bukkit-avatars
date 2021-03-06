package io.ruben.minecraft.avatars

import io.ruben.minecraft.avatars.events.{AvatarQuitEvent, AvatarCreatedEvent, AvatarLoginEvent}
import io.ruben.minecraft.avatars.models._
import org.bukkit.{World, Bukkit}
import org.bukkit.command.{Command, CommandSender, CommandExecutor}
import org.bukkit.entity.Player

import DataAccess._
import driver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}

/**
 * Created by istar on 13/09/15.
 */
object Commands extends CommandExecutor {
  override def onCommand(commandSender: CommandSender, command: Command, s: String, arguments: Array[String]): Boolean = {

    commandSender match {
      case player: Player =>
        val playerId = player.getUniqueId

        arguments.headOption match {
          case Some(cmd) =>

            cmd match {
              case "create" | "cr" =>

                arguments.tail.headOption match {
                  case Some(name) =>
                    //TODO Load world spawn from configuration
                    Location.fromBukkit(Bukkit.getWorld("world").getSpawnLocation).save.onComplete {
                      case Success(location) =>
                        Avatar(name, playerId, location.id).save.onComplete {
                          case Success(avatar) =>
                            Bukkit.getPluginManager.callEvent(AvatarCreatedEvent(player, avatar))
                          case Failure(err) =>
                            player.sendMessage(s"There's already an avatar called $name!")
                        }
                      case Failure(err) => err.printStackTrace()
                    }
                  case None => player.sendMessage("You have to specify a name")
                }


              case "list" | "ls" =>
                db.run(avatars.filter(_.userId === playerId).map(_.name).result).onSuccess {
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