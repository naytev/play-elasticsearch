name := "play-elasticsearch"

version := "1.4.6"

organization := "com.naytev"

scalaVersion := "2.10.4"

lazy val project = Project ( "play-elasticsearch", file("."), settings = Defaults.defaultSettings)

resolvers ++= Seq(
  "play Repository" at "http://repo.typesafe.com/typesafe/simple/maven-releases/",
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.3.1" % "provided",
  "com.typesafe.play" % "play-exceptions" % "2.3.1" % "provided",
  "com.typesafe.play" %% "play-test" % "2.3.1" % "test",
  "com.sksamuel.elastic4s" %% "elastic4s" % "1.4.6"
)

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pgpReadOnly := false

licenses := Seq("Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

homepage := Some(url("https://github.com/naytev/play-elasticsearch"))

pomExtra := (
  <scm>
    <url>git://github.com/naytev/play-elasticsearch.git</url>
    <connection>scm:git://github.com/naytev/play-elasticsearch.git</connection>
  </scm>
  <developers>
    <developer>
      <id>mbseid</id>
      <name>Michael Seid</name>
      <url>http://github.com/mbseid</url>
    </developer>
  </developers>)
