package controllers

import models.Task
import com.nicta.rng._
import scalaz._
import std.anyVal._
import syntax.foldable._

package object logic {

  val taskRepo: TasksRepo = stubTaskRepo
  
  val stubTaskRepo: TasksRepo = new SimpleTaskRepo(Seq(
    Task("Multiply 5 with 4!", answer => answer == 20)
  ))

  val infiniteTaskRepo: TasksRepo =
    new InfiniteTaskRepo(infiniteRandomTaskStream)

  import Rng._
  val simpleRandomTaskGen: Rng[Task] =
    for {
      query ← propernounstring(Size(10))
    } yield Task(query, _ ⇒ true)

  val simpleSumTaskGen: Rng[Task] =
    for {
      n ← chooseint(1, 10)
      m ← chooseint(1, 10)
    } yield Task(s"What is the sum of $n and $m?", _ == (n + m).toString)

  val generalizedSumTaskGen: Rng[Task] =
    for {
      n   ← chooseint(1, 10)
      lst ← chooseint(1, 10).list1(Size(10))
    } yield Task(s"What is the sum of $n + ${lst.list.mkString(" + ")}",
      _ == (n + lst.suml).toString)

  def numberList2Gen(max: Int): Rng[List[Int]] =
    for {
      n ← chooseint(1,10)
      lst ← chooseint(1,10).list1(Size(10))
    } yield n :: lst.list

  val generalizedMathTaskGen: Rng[Task] = {
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
    } yield Task(s"What is ${lst.mkString(desc)}?",
      _ == lst.reduceRight(fun).toString)
  }

  val infiniteRandomTaskStream = getTaskStream(simpleRandomTaskGen)
  val infiniteSumTaskGen = getTaskStream(simpleSumTaskGen)
  val infiniteGeneralizedSumTaskStream =
    getTaskStream(generalizedSumTaskGen)
  val generalizedMathTaskStream = getTaskStream(generalizedMathTaskGen)

  def getTaskStream(gen: Rng[Task]) =
    Iterator.continually {
      gen.list1(Size(100)).run.unsafePerformIO.list
    }.flatten
}
