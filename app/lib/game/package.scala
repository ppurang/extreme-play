package lib

import scala.concurrent.duration._
import scala.util.Random

package object game {
  type TaskIntervalGenerator = () => FiniteDuration
  val defaultTaskIntervalGen: TaskIntervalGenerator = () => 10.seconds
}
