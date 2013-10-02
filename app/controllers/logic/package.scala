package controllers

import models.Task
import com.nicta.rng._

package object logic {

  val simpleTaskRepo: TasksRepo = new SimpleTaskRepo(Seq(
    Task("Multiply 5 with 4!", answer => answer == 20)
  ))

  val infiniteTaskRepo: TasksRepo =
    new InfiniteTaskRepo(infiniteTaskStream)

  import Rng._
  val simpleRandomTaskGen: Rng[Task] =
    for {
      query ← propernounstring(Size(10))
    } yield Task(query, _ ⇒ true)

  val infiniteTaskStream = Iterator.continually {
    simpleRandomTaskGen.list1(Size(100)).run.unsafePerformIO.list
  }.flatten
}
