package controllers

import models.Task
import com.nicta.rng._
import scalaz._
import std.anyVal._
import syntax.foldable._

package object logic {

  val taskRepo: TasksRepo = infiniteTaskRepo
  
  val infiniteTaskRepo: TasksRepo = new InfiniteTaskRepo(taskStream)

  import Rng._
  def numberList2Gen(max: Int): Rng[List[Int]] =
    for {
      n ← chooseint(1,10)
      lst ← chooseint(1,10).list1(Size(10))
    } yield n :: lst.list

  def combinedGen(taskGenerators: NonEmptyList[Rng[Task]]): Rng[Task] =
    for {
      gen ← oneofL(taskGenerators)
      task ← gen
    } yield task

  val mathTaskGen: Rng[Task] = {
    val funLst: NonEmptyList[((Int, Int) ⇒ Int, String)] = NonEmptyList(
      ({(i: Int, j: Int) ⇒ i + j}, " + "),
      ({(i: Int, j: Int) ⇒ i - j}, " - "),
      ({(i: Int, j: Int) ⇒ i / j}, " / "),
      ({(i: Int, j: Int) ⇒ i * j}, " * ")
    )
    for {
      lst ← numberList2Gen(10)
      res ← oneofL(funLst)
      (fun, desc) = res
    } yield Task(s"What is the result of ${lst.mkString(desc)}?",
      _ == lst.reduceRight(fun).toString)
  }

  val matcherTaskGen: Rng[Task] = {
    case class Entity(val name: String, is: String, answer: String)
    val entities = NonEmptyList(
      Entity("apple", "color", "red"),
      Entity("banana", "color", "yellow"),
      Entity("bottle", "beverage", "beer")
    )
    for {
      entity ← oneofL(entities)
    } yield Task(s"What is the ${entity.is} of ${entity.name}?",
      _ == entity.answer)
  }

  val mathTaskStream    = getTaskStream(mathTaskGen)
  val matcherTaskStream = getTaskStream(matcherTaskGen)
  val taskStream        = getTaskStream(combinedGen(
    NonEmptyList(mathTaskGen, matcherTaskGen)))

  def getTaskStream(gen: Rng[Task]) =
    Iterator.continually {
      gen.list1(Size(100)).run.unsafePerformIO.list
    }.flatten
}
