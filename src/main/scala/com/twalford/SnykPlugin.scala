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
    val snykOrganization = SnykTasks.snykOrganization
  }

  override def globalSettings: Seq[Def.Setting[_]] = Seq(
    Global / concurrentRestrictions += Tags.exclusive(snykTag),
    aggregate in snykAuth := false
  )

  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    snykTest := snykTestTask.value,
    snykMonitor := snykMonitorTask.value,
    snykAuth := snykAuthTask.value
  )
}
