import scala.concurrent.Future

import play.api.mvc._
import play.api.mvc.Results._

package object controllers {
  def FeaturedAction(featureName: String) = new ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[SimpleResult]) = {
      if (play.api.Play.current.configuration.getBoolean(featureName).getOrElse(false)) {
        block(request)
      }
      else Future.successful(NotFound("Feature is not enabled"))
    }
  }
}