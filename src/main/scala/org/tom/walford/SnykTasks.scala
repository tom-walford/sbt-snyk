package org.tom.walford

import sbt.ConcurrentRestrictions.Tag
import sbt.{Def, settingKey, taskKey}
import sbt.Keys.{name, streams}
import sbt.internal.util.ManagedLogger

import scala.sys.process.Process

object SnykTasks {
  private lazy val authEnvVar = "SNYK_TOKEN"

  val snykOrganization = settingKey[String]("The organization snyk should be run for")
  val snykAuth = taskKey[Unit]("Authorizes a local snyk instance")
  val snykTest = taskKey[Unit]("Runs snyk test on the local project")
  val snykMonitor = taskKey[Unit]("Runs snyk monitor on the local project")

  val snykTag = Tag("snyk-exclusive")


  lazy val snykTestTask = Def.task {
    val log = streams.value.log
    run(s"""snyk test -- "\\"project ${name.value}\\""""", log)
  }.tag(snykTag)

  lazy val snykMonitorTask = Def.task {
    val log = streams.value.log
    val projectName = name.value
    run(s"""snyk monitor --org=${snykOrganization.value} --project-name=$projectName -- "\\"project $projectName\\""""", log)
  }.tag(snykTag)

  lazy val snykAuthTask = Def.task {
    val log = streams.value.log
    checkForAuth(log)
  }.tag(snykTag)

  private def checkForAuth(log: ManagedLogger): Unit = {
    Option(System.getenv(authEnvVar)) match {
      case None =>
        log.info("No auth set up, but presumed we're running locally. Requesting auth via `snyk auth`")
        run("snyk auth", log)
      case Some(auth) =>
        log.debug("Snyk using environment variable authorization, continuing")
        run(s"snyk auth $auth", log)
    }
  }

  private def run(cmd: String, log: ManagedLogger): Unit = {
    val shell = if (sys.props("os.name").contains("Windows")) {
      List("cmd", "/c")
    } else {
      List("bash", "-c")
    }
    Process(shell ::: cmd.split(' ').toList) ! log
  }
}
