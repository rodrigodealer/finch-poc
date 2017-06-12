name := """Knowledge base"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.2"

lazy val versions = new {
  val guice = "4.1.0"
  val twitterInject = "2.8.0"
  val mockito = "1.10.5"
  val scalatest = "3.0.1"
  val twitterServerVersion = "1.30.0"

}

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "com.github.finagle" %% "finch-core" % "0.15.0" changing(),
  "com.github.finagle" %% "finch-circe" % "0.15.0" changing(),
  "io.circe" %% "circe-generic" % "0.8.0",
  "com.twitter" %% "twitter-server" % versions.twitterServerVersion,
  "org.scalactic" %% "scalactic" % versions.scalatest,
  "org.scalatest" %% "scalatest" % versions.scalatest % "test",
  "org.mockito" % "mockito-core" % versions.mockito % "test"
)

//assemblyMergeStrategy in assembly <<= (assemblyMergeStrategy in assembly) {
//  (old) => {
//    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
//    case x => MergeStrategy.first
//  }
//}

//assemblyJarName in assembly := "kbase.jar"

// test in assembly := {}
