package controllers

import models.Task
import com.nicta.rng._
import scalaz._
import std.anyVal._
import syntax.foldable._

package object logic {

  lazy val infiniteTaskRepo: TasksRepo = new InfiniteTaskRepo(taskStream)

  import Rng._
  def numberList2Gen(max: Int): Rng[List[Int]] =
    for {
      n   ← chooseint(1, 10)
      lst ← chooseint(1, 10).list1(Size(10))
    } yield n :: lst.list

  def combinedGen(taskGenerators: NonEmptyList[Rng[Task]]): Rng[Task] =
    for {
      gen ← oneofL(taskGenerators)
      task ← gen
    } yield task

  lazy val mathTaskGen: Rng[Task] = {
    val funLst: NonEmptyList[((Int, Int) ⇒ Int, String, Int)] =
      NonEmptyList(
        ({(i: Int, j: Int) ⇒ i + j}, " + ", 0),
        ({(i: Int, j: Int) ⇒ i - j}, " - ", 0),
        ({(i: Int, j: Int) ⇒ i / j}, " / ", 1),
        ({(i: Int, j: Int) ⇒ i * j}, " * ", 1)
      )
    for {
      lst ← numberList2Gen(10)
      res ← oneofL(funLst)
      (fun, desc, score) = res
    } yield Task(s"What is the result of ${lst.mkString(desc)}?",
      _ == lst.reduceRight(fun).toString, score + lst.size)
  }

  lazy val matcherTaskGen: Rng[Task] = {
    case class Entity(val name: String, is: String,
      answer: String, score: Int)
    val entities = NonEmptyList(
      Entity("apple", "color", "red", 30),
      Entity("banana", "color", "yellow", 30),
      Entity("bottle", "beverage", "beer", 30)
    )
    for {
      entity ← oneofL(entities)
    } yield Task(s"What is the ${entity.is} of ${entity.name}?",
      _ == entity.answer, entity.score)
  }

  lazy val selectOneGen: Rng[Task] = {
    val questions: NonEmptyList[(String, List[Int] ⇒ Int, Int)] =
      NonEmptyList(
        ("max", _.max, 15),
        ("min", _.min, 15),
        ("first", _.head, 0),
        ("last", _.last, 0)
      )

    for {
      lst   ← chooseint(1, 10).list1(Size(10))
      quest ← oneofL(questions)
      (name, fn, score) = quest
    } yield Task(s"What is the $name of ${lst.list mkString ", "}",
      _ == fn(lst.list).toString, score + lst.size)
  }

  lazy val streamTaskGen: Rng[Task] = {
    lazy val fibs: Stream[Int] =
      0 #:: 1 #:: fibs.zip(fibs.tail).map { case(m, n) ⇒ n + m }
    lazy val naturals: Stream[Int] = 0 #:: naturals.map(_ + 1)
    lazy val primes = 2 #:: sieve(3)
    def sieve(n: Int) : Stream[Int] =
      if (primes.takeWhile(p ⇒ p*p <= n).exists(n % _ == 0)) sieve(n + 2)
      else n #:: sieve(n + 2)

    val streams = NonEmptyList(
      (fibs, "Fibonacci", 20),
      (naturals, "Naturals", 0),
      (primes, "Primes", 50))

    for {
      n   ← chooseint(1, 5)
      res ← oneofL(streams)
      (stream, name, score) = res
    } yield Task(
      s"What is the first $n elements of $name (space-separated)?",
      _ == stream.take(n).mkString(" "), score + n)
  }

  lazy val mathTaskStream    = getTaskStream(mathTaskGen)
  lazy val matcherTaskStream = getTaskStream(matcherTaskGen)
  lazy val taskStream        = getTaskStream(combinedGen(
    NonEmptyList(mathTaskGen, matcherTaskGen,
      streamTaskGen, selectOneGen)))

  def getTaskStream(gen: Rng[Task]) =
    Iterator.continually {
      gen.list1(Size(100)).run.unsafePerformIO.list
    }.flatten

  lazy val taskRepo: TasksRepo = infiniteTaskRepo
}
