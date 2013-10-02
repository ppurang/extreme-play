package controllers

import models.Task
import com.nicta.rng._

package object logic {

  val taskRepo: TasksRepo = new SimpleTaskRepo(Seq(
    Task("Multiply 5 with 4!", answer => answer == 20)
  ))

  import Rng._
  val simpleRandomTaskGen: Rng[Task] =
    for {
      query ← propernounstring(Size(10))
    } yield Task(query, _ ⇒ true)

}
