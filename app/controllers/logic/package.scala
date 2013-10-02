package controllers

import models.Task
import com.nicta.rng._
import scalaz._
import std.anyVal._
import syntax.foldable._

package object logic {
  
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

  val streamTaskGen: Rng[Task] = {
    lazy val fibs: Stream[Int] =
      0 #:: 1 #:: fibs.zip(fibs.tail).map { case(m, n) ⇒ n + m }
    lazy val naturals: Stream[Int] = 0 #:: naturals.map(_ + 1)
    val primes = 2 #:: sieve(3)
    def sieve(n: Int) : Stream[Int] =
      if (primes.takeWhile(p => p*p <= n).exists(n % _ == 0)) sieve(n + 2)
      else n #:: sieve(n + 2)

    val streams = NonEmptyList(
      (fibs, "Fibonacci"),
      (naturals, "Naturals"),
      (primes, "Primes"))

    for {
      n   ← chooseint(1, 5)
      res ← oneofL(streams)
      (stream, name) = res
    } yield Task(
      s"What is the first $n elements of $name (space-separated)?",
      _ == stream.take(n).mkString(" "))
  }

  val mathTaskStream    = getTaskStream(mathTaskGen)
  val matcherTaskStream = getTaskStream(matcherTaskGen)
  val taskStream        = getTaskStream(combinedGen(
    NonEmptyList(mathTaskGen, matcherTaskGen, streamTaskGen)))

  def getTaskStream(gen: Rng[Task]) =
    Iterator.continually {
      gen.list1(Size(100)).run.unsafePerformIO.list
    }.flatten

  val taskRepo: TasksRepo = infiniteTaskRepo
}
