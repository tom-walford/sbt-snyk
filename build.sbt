name := "sbt-snyk"
organization := "com.twalford"
version := "0.1-SNAPSHOT"

scalaVersion := "2.12.8"

enablePlugins(SbtPlugin)

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.2")