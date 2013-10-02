package controllers.logic

import models.Task

trait TasksRepo {
  def select: Iterator[Task]
}

class InfiniteTaskRepo(tasks: Iterator[Task]) extends TasksRepo {
  def select: Iterator[Task] = tasks
}
