// Turn this project into a Scala.js project by importing these settings

val webapp = crossProject.settings(
  scalaVersion := "2.11.7",
  version := "0.1-SNAPSHOT",
  scalacOptions ++= Seq("-feature"),
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "utest" % "0.3.1",
    "com.lihaoyi" %%% "upickle" % "0.3.6",
    "com.lihaoyi" %%% "autowire" % "0.2.5",
    "com.lihaoyi" %%% "scalatags" % "0.5.3"
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
    "io.spray" %% "spray-can" % "1.3.3",
    "io.spray" %% "spray-routing" % "1.3.3",
    "com.typesafe.akka" %% "akka-actor" % "2.4.0",
    //    "org.webjars" % "bootstrap" % "3.3.4",
    "com.esotericsoftware.kryo" % "kryo" % "2.24.0" % "test",
    "org.scalatest" % "scalatest_2.11" % "2.2.5" % "test"
  )
)

val webappJS = webapp.js
val webappJVM = webapp.jvm.settings(
  (resources in Compile) += {
    (fastOptJS in(webappJS, Compile)).value
    (artifactPath in(webappJS, Compile, fastOptJS)).value
  }
)