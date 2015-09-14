package io.ruben.minecraft.avatars

import java.util.logging.Level._

import io.ruben.minecraft.avatars.listeners.{AvatarListeners, PlayerListeners}
import org.bukkit.plugin.java.JavaPlugin
import slick.driver.H2Driver.api._
import DataAccess._
import slick.jdbc.meta.MTable
import scala.concurrent.duration._

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success
import scala.util.Failure
/**
* Created by istar on 13/09/15.
*/
class AvatarsPlugin extends JavaPlugin {

  def configPath = s"${getDataFolder.getAbsolutePath}/config"

  override def onEnable(): Unit = {

    //Setup user login listener
    getServer.getPluginManager.registerEvents(PlayerListeners, this)
    getServer.getPluginManager.registerEvents(AvatarListeners, this)

    //Setup command listener
    getCommand("avatars").setExecutor(Commands)

    //Setup database

    db.run(MTable.getTables).onComplete {
      case Success(tables) => {
        if(tables.nonEmpty) {
          getLogger.info("Database already exist")
        }
        else {
          getLogger.info("Creating database for first time")

          val setup: DBIO[Unit] = DBIO.seq(
            (users.schema ++ avatars.schema ++ locations.schema).create
          )
          val setupDb: Future[Unit] = db.run(setup)

          Await.result(setupDb, Duration.Inf)
          getLogger.info("Database created")
        }
      }
      case Failure(f) => {
        getLogger.log(SEVERE, "Couldn't read/write the database")
      }
    }
  }

}