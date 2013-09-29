import sbt._
import Keys._
import play.Project._
//import com.typesafe.sbt.SbtAtmosPlay.atmosPlaySettings

object ApplicationBuild extends Build {

  val appName         = "extreme-play"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    filters,
    "org.scala-lang" %% "scala-pickling" % "0.8.0-SNAPSHOT",
    "com.typesafe.slick" %% "slick" % "1.0.1",
    "net.liftweb" %% "lift-json-scalaz7" % "2.5",
    "com.stackmob" %% "newman" % "1.0.0",
    "io.spray" % "spray-client" % "1.2-20130822"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    scalaVersion := "2.10.2",
    resolvers += Resolver.sonatypeRepo("snapshots"),
    resolvers += "spray nightly" at "http://nightlies.spray.io"
    // Add your own project settings here
  )//.settings((atmosPlaySettings): _*)      //https://groups.google.com/forum/#!topic/typesafe-console/RgvV-yf2Ce8

}
