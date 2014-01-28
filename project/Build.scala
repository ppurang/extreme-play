import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "extreme-play"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    filters,
    "org.scalaz"        %% "scalaz-core"      % "7.0.5",
    "com.nicta"         %% "rng"              % "1.1",
    "com.typesafe.akka" %% "akka-testkit"     % "2.2.1" % "test",
    "org.scalatest"     %  "scalatest_2.10.0" % "1.8"   % "test"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    scalaVersion := "2.10.3",
    resolvers += Resolver.sonatypeRepo("snapshots"),
    resolvers += "spray nightly" at "http://nightlies.spray.io")

}
