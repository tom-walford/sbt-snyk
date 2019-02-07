name := "sbt-snyk"
organization := "com.github.tom-walford"
version := "0.2.1-PR-SNAPSHOT"

scalaVersion := "2.12.8"

crossSbtVersions := Seq("1.2.8", "0.13.17")

enablePlugins(SbtPlugin)

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.2")

publishTo := sonatypePublishTo.value
sonatypeProfileName := organization.value

publishMavenStyle := true

licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))

homepage := Some(url("https://github.com/tom-walford/sbt-snyk"))
scmInfo := Some(
  ScmInfo(
    url("https://github.com/tom-walford/sbt-snyk"),
    "scm:git@github.com:tom-walford/sbt-snyk.git"
  )
)
developers := List(
  Developer(id="twalford", name="Tom Walford", email="tom@example.com", url=url("https://github.com/tom-walford"))
)
