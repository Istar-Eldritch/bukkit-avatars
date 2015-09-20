package io.ruben.minecraft.avatars

import java.util.logging.Level._

import io.ruben.minecraft.avatars.listeners.{AvatarListeners, PlayerListeners}
import org.bukkit.plugin.java.JavaPlugin
import DataAccess._
import driver.api._
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success
import scala.util.Failure
/**
* Created by istar on 13/09/15.
*/
class AvatarsPlugin extends JavaPlugin {

  override def onEnable(): Unit = {

    //Setup user login listener
    getServer.getPluginManager.registerEvents(PlayerListeners, this)
    getServer.getPluginManager.registerEvents(AvatarListeners, this)

    //Setup command listener
    getCommand("avatars").setExecutor(Commands)

    //Setup database

    db.run(MTable.getTables(users.baseTableRow.tableName)).onComplete {
      case Success(tables) =>
        if(tables.isEmpty) {
          getLogger.info("Creating tables for first time")

          val setup: DBIO[Unit] = DBIO.seq(
            (users.schema ++ avatars.schema ++ locations.schema).create
          )
          db.run(setup).andThen {
            case _ =>  getLogger.info("Tables created")
          }

        }
        else {
          getLogger.info("Initialized storage found")
        }
      case Failure(f) =>
        f.printStackTrace()
        getLogger.log(SEVERE, "Couldn't read/write the database")
    }
  }

}