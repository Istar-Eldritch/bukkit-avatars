package io.ruben.minecraft.avatars

import io.ruben.minecraft.avatars.events.{AvatarCreatedEvent, AvatarLoginEvent}
import org.bukkit.Bukkit
import org.bukkit.command.{Command, CommandSender, CommandExecutor}
import org.bukkit.entity.Player

import DataAccess._
import slick.driver.H2Driver.api._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by istar on 13/09/15.
 */
object Commands extends CommandExecutor {
  override def onCommand(commandSender: CommandSender, command: Command, s: String, arguments: Array[String]): Boolean = {

    val fMsg:Unit = commandSender match {
      case player: Player =>
        val playerId = player.getUniqueId.toString

        arguments.headOption match {
          case Some(cmd) =>

            cmd match {
              case "create" =>

                arguments.tail.headOption match {
                  case Some(name) =>
                    val location:Location = Location.fromBukkit(player.getLocation)
                    db.run((locations returning locations.map(_.id)) += location).map(locationId => {
                      db.run((avatars returning avatars) += Avatar(name, playerId, locationId)).map(avatar =>
                        Bukkit.getPluginManager.callEvent(AvatarCreatedEvent(player, avatar)))
                    })
                  case None => player.sendMessage("You have to specify a name")
                }

              case "list" | "ls" =>
                db.run(userAvatars(playerId).map(_.name).result).map(nameSeq => player.sendMessage(nameSeq.mkString("\n")))
              case "switch" =>

                arguments.tail.headOption match {
                  case Some(name) =>
                    db.run(avatars.filter(_.userId === playerId).filter(_.name === name).result.headOption).map {
                      case Some(avatar) => Bukkit.getServer.getPluginManager.callEvent(AvatarLoginEvent(player, avatar))
                      case None => player.sendMessage(s"You don't have an avatar named $name")
                }
                  case None => player.sendMessage("You have to specify a name")
                }

              case _ => player.sendMessage("You have to specify a name")
            }

          case None => player.sendMessage(s"${command.getUsage}\nYou have to specify a subcommand!")
        }
      case _ => commandSender.sendMessage("You are not a player!")
    }

    true
  }


}