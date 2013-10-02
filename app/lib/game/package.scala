package lib

import scala.concurrent.duration._
import scala.util.Random

package object game {
  type TaskIntervalGenerator = () => FiniteDuration
  val defaultTaskIntervalGen: TaskIntervalGenerator = () => (Random.nextInt(5000) + 100).seconds
}
