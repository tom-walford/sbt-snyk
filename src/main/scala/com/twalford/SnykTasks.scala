package com.twalford

import sbt.ConcurrentRestrictions.Tag
import sbt.{Def, settingKey, taskKey, UnprintableException}
import sbt.Keys.{name, streams, thisProject}
import sbt.internal.util.ManagedLogger

import scala.sys.process.Process

object SnykTasks {
  private lazy val authEnvVar = "SNYK_TOKEN"

  val snykOrganization = settingKey[String]("The organization snyk should be run for")
  val snykAuth = taskKey[Unit]("Authorizes a local snyk instance")
  val snykTest = taskKey[Unit]("Runs snyk test on the local project")
  val snykMonitor = taskKey[Unit]("Runs snyk monitor on the local project")

  val snykTag = Tag("snyk-exclusive")

  private def escape(str: String) = if (sys.props("os.name").contains("Windows")) {
    s""""\\"$str\\"""""
  } else {
    s""""$str""""
  }

  lazy val snykTestTask = Def.task {
    val log = streams.value.log
    val id = thisProject.value.id
    run(List("snyk", "test", "--", escape(s"project $id")), log)
  }.tag(snykTag)

  lazy val snykMonitorTask = Def.task {
    val id = thisProject.value.id
    val log = streams.value.log
    val projectName = name.value
    run(List("snyk", "monitor", s"--org=${snykOrganization.value}", s"--project-name=$projectName", "--",
     escape(s"project $id")), log)
  }.tag(snykTag)

  lazy val snykAuthTask = Def.task {
    val log = streams.value.log
    checkForAuth(log)
  }.tag(snykTag)

  private def checkForAuth(log: ManagedLogger): Unit = {
    Option(System.getenv(authEnvVar)) match {
      case None =>
        log.info("No auth set up, but presumed we're running locally. Requesting auth via `snyk auth`")
        run(List("snyk", "auth"), log)
      case Some(auth) =>
        log.debug("Snyk using environment variable authorization, continuing")
        run(List("snyk", "auth", auth), log)
    }
  }

  private def run(cmds: List[String], log: ManagedLogger): Unit = {
    val shell = if (sys.props("os.name").contains("Windows")) {
      List("cmd", "/c")
    } else {
      Nil
    }
    val responseCode = Process(shell ::: cmds) ! log
    if (responseCode != 0) {
      throw SnykError
    }
  }

  object SnykError extends Error("Snyk process failed") with UnprintableException
}
