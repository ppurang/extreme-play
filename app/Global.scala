import play.api._
import play.api.mvc._
import play.filters.gzip.GzipFilter

import lib.game.Scorer

object Global extends WithFilters(new GzipFilter()) with GlobalSettings {
  override def onStart(app: Application) {
  	val scorer = Akka.system.actorOf(Props[Scorer])
    Logger.info("Application has started")
  }  
  
  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }  
}