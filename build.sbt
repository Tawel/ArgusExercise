name := "untitled"

version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  val akkaVersion = "2.3.9"
  val sprayVersion = "1.3.2"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "io.spray" %% "spray-can" % sprayVersion,
    "io.spray" %% "spray-routing" % sprayVersion,
    "io.spray" %% "spray-json" % "1.3.1",
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.1.2",
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "io.spray" %% "spray-testkit" % sprayVersion % "test",
    "org.specs2" %% "specs2" % "2.3.13" % "test"
  )
}
libraryDependencies += "io.spray" % "spray-client_2.11" % "1.3.4"
// https://mvnrepository.com/artifact/org.json4s/json4s-jackson_2.11
libraryDependencies += "org.json4s" % "json4s-jackson_2.11" % "3.5.3"

