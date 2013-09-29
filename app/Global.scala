import play.api._
import play.api.mvc._
import play.filters.gzip.GzipFilter

object Global extends WithFilters(new GzipFilter()) with GlobalSettings {
  // onStart, onStop etc...
}