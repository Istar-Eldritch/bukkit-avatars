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
    val msg:Future[String] = if(commandSender.isInstanceOf[Player])
    {
      val player = commandSender.asInstanceOf[Player]
      val playerId = player.getUniqueId.toString
      arguments.headOption match {
        case Some(cmd) => {
          cmd match {
            case "create" => {
              arguments.tail.headOption match {
                case Some(name) => {

                  val location:Location = Location.fromBukkit(player.getLocation)

                  db.run((locations returning locations.map(_.id)) += location).map[String](locationId => {
                    db.run(avatars += Avatar(name, playerId, locationId))
                    "Avatar created!"
                  })
                }
                case None => Future("You have to specify a name")
              }
            }
            case "list" | "ls" => {
              db.run(userAvatars(playerId).map(_.name).result).map[String](nameSeq => nameSeq.mkString(" "))
            }
            case _ => Future("The option is not valid")
          }
        }
        case None => Future(s"${command.getUsage}\nYou have to specify a subcommand!");
      }
    } else Future("You are not a player!")

    msg.onComplete {
      case Success(msg) => commandSender.sendMessage(msg)
      case Failure(err) => commandSender.sendMessage(err.getMessage)
    }
    true
  }
}