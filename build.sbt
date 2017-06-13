name := """Knowledge base"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.2"

lazy val versions = new {
  val guice = "4.1.0"
  val twitterInject = "2.8.0"
  val mockito = "1.10.5"
  val scalatest = "3.0.1"
  val twitterServerVersion = "1.30.0"
  val finch = "0.15.0"

}

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "com.github.finagle" %% "finch-core" % versions.finch changing(),
  "com.github.finagle" %% "finch-circe" % versions.finch changing(),
  "io.circe" %% "circe-generic" % "0.8.0",
  "com.netflix.governator" % "governator" % "1.17.2",
  "com.google.inject" % "guice" % "4.1",
  "net.codingwell" %% "scala-guice" % "4.1.0",
  "com.google.inject.extensions" % "guice-multibindings" % "4.1",
  "com.twitter" %% "twitter-server" % versions.twitterServerVersion,
  "org.scalactic" %% "scalactic" % versions.scalatest,
  "org.scalatest" %% "scalatest" % versions.scalatest % "test",
  "org.mockito" % "mockito-core" % versions.mockito % "test"
)
