package io.ruben.minecraft.avatars

import org.bukkit.command.{Command, CommandSender, CommandExecutor}
import org.bukkit.entity.Player

import scala.concurrent.Future
import scala.util.{Failure, Success}
import DataAccess._
import slick.driver.H2Driver.api._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by istar on 13/09/15.
 */
object Commands extends CommandExecutor {
  override def onCommand(commandSender: CommandSender, command: Command, s: String, arguments: Array[String]): Boolean = {

    val fMsg:Future[String] = commandSender match {
      case player: Player =>
        val playerId = player.getUniqueId.toString

        arguments.headOption match {
          case Some(cmd) =>

            cmd match {
              case "create" =>

                arguments.tail.headOption match {
                  case Some(name) =>
                    val location:Location = Location.fromBukkit(player.getLocation)
                    db.run((locations returning locations.map(_.id)) += location).map[String](locationId => {
                      db.run(avatars += Avatar(name, playerId, locationId))
                      "Avatar created!"
                    })
                  case None => Future("You have to specify a name")
                }

              case "list" | "ls" =>
                db.run(userAvatars(playerId).map(_.name).result).map[String](nameSeq => nameSeq.mkString("\n"))
              case "switch" =>

                arguments.tail.headOption match {
                  case Some(name) =>
                    val query = for {
                      a <- avatars if a.userId === playerId && a.name === name
                      l <- locations if a.locationId === l.id
                    } yield (a, l)
                    db.run(query.result.headOption).map[String] {
                      case Some((avatar, location)) => {
                        player.teleport(location.toBukkit)
                        player.setDisplayName(avatar.name)
                        player.setPlayerListName(avatar.name)
                        s"Switching to ${avatar.name}"
                      }
                      case None => s"You don't have an avatar named $name"
                }
                  case None => Future("You have to specify a name")
                }

              case _ => Future("The option is not valid")
            }

          case None => Future(s"${command.getUsage}\nYou have to specify a subcommand!");
        }
      case _ => Future("You are not a player!")
    }

    fMsg.onComplete {
      case Success(msg) => commandSender.sendMessage(msg)
      case Failure(err) => commandSender.sendMessage(err.getMessage)
    }
    true
  }


}