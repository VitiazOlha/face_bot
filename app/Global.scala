import play.api.{Logger, _}

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Application is started!!!")
  }

}