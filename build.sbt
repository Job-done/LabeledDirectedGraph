// Turn this project into a Scala.js project by importing these settings

val scalaV = "2.11.7"
scalaVersion := scalaV

val webapp = crossProject.settings(
  scalaVersion := scalaV,
  version := "0.1-SNAPSHOT",
  scalacOptions ++= Seq("-feature"),
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "autowire" % "0.2.5",
    "com.lihaoyi" %%% "scalatags" % "0.5.3",
    "com.lihaoyi" %%% "upickle" % "0.3.6",
    "com.lihaoyi" %%% "utest" % "0.3.1"
  ), testFrameworks += new TestFramework("utest.runner.Framework")
).jsSettings(
  workbenchSettings: _*
).jsSettings(
  name := "Client",
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.8.2"
  ),
  bootSnippet := "webapp.ScalaJSExample().main();"
).jvmSettings(
  Revolver.settings: _*
).jvmSettings(
  name := "Server",
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.4.1",
    "io.spray" %% "spray-can" % "1.3.3",
    "io.spray" %% "spray-routing" % "1.3.3",
    "io.spray" %% "spray-testkit" % "1.3.3",
    "com.esotericsoftware.kryo" % "kryo" % "2.24.0" % "test",
    "org.scalatest" %% "scalatest" % "2.2.5" % "test"/*,
    "org.specs2" %% "specs2-core" % "3.6.5" % "test"*/
  ),
  scalacOptions ++= Seq( "-deprecation", "-unchecked" ),
  scalacOptions in Test ++= Seq("-Yrangepos")
)

val webappJS = webapp.js
val webappJVM = webapp.jvm.settings(
  (resources in Compile) += {
    (fastOptJS in(webappJS, Compile)).value
    (artifactPath in(webappJS, Compile, fastOptJS)).value
  }
)