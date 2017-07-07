import play.api.{Logger, _}
import database.DBConnector

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    DBConnector.update
  }

}