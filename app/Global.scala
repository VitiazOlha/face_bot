import play.api.{Logger, _}

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    models.Subscription.init
  }
}