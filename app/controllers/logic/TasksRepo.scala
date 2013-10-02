package controllers.logic

import models.Task

trait TasksRepo {
  def select: Iterator[Task]
}

class SimpleTaskRepo(predefinedTasks: Seq[Task]) extends TasksRepo {
  def select: Iterator[Task] = predefinedTasks.iterator
}

class InfiniteTaskRepo(tasks: Iterator[Task]) extends TasksRepo {
  def select: Iterator[Task] = Iterator.fill(100)(()).map { x ⇒
    tasks.next
  }
}
