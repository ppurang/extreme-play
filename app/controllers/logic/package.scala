package controllers

import models.Task

package object logic {

  val taskRepo: TasksRepo = new SimpleTaskRepo(Seq(
    Task("Multiply 5 with 4!", answer => answer == 20)
  ))
  
}
