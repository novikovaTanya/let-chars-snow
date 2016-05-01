name := """Try Akka Streams. Let It Snow"""

version := "1.0"

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-feature")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-experimental" % "2.4.4"
)