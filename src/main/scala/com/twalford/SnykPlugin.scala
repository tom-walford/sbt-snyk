package com.twalford

import net.virtualvoid.sbt.graph.DependencyGraphPlugin
import sbt._
import sbt.Keys._
import SnykTasks._

object SnykPlugin extends AutoPlugin {
  self =>
  override def requires = DependencyGraphPlugin
  object autoImport {
    val SnykPlugin: AutoPlugin = self
    val snykBinary = SnykTasks.snykBinary
    val snykOrganization = SnykTasks.snykOrganization
    val snykProject = SnykTasks.snykProject
  }

  override def globalSettings: Seq[Def.Setting[_]] = Seq(
    (concurrentRestrictions in Global) += Tags.exclusive(snykTag),
    aggregate in snykAuth := false,
    snykAuth := snykAuthTask.value,

    snykBinary := "snyk"
  )

  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    snykTest := snykTestTask.value,
    snykMonitor := snykMonitorTask.value,
    snykAuth := snykAuthTask.value,

    snykProject := name.value
  )
}
