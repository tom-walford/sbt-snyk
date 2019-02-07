package com.twalford

import com.twalford.SbtCompat._
import sbt.ConcurrentRestrictions.Tag
import sbt.{Def, settingKey, taskKey, UnprintableException}
import sbt.Keys.{name, streams, thisProject}
import sbt.Logger

import scala.sys.process.Process

object SnykTasks {
  private lazy val authEnvVar = "SNYK_TOKEN"

  val snykBinary = settingKey[String]("The snyk command to run. Defaults to 'snyk' for the case when snyk is on the $PATH; alternatively e.g. './node_modules/.bin/snyk'")
  val snykOrganization = settingKey[String]("The snyk organization to report against (i.e. `synk --org`)")
  val snykProject = settingKey[String]("The snyk project to report against (i.e. `snyk --project-name`).  Ideally the appId, but it must be unique.  Defaults to sbt project name.")

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
    run(List(snykBinary.value, "test", s"--org=${snykOrganization.value}", s"--project-name=${snykProject.value}", "--", escape(s"project $id")), log)
  }.tag(snykTag)

  lazy val snykMonitorTask = Def.task {
    val id = thisProject.value.id
    val log = streams.value.log
    run(List(snykBinary.value, "monitor", s"--org=${snykOrganization.value}", s"--project-name=${snykProject.value}", "--",
     escape(s"project $id")), log)
  }.tag(snykTag)

  lazy val snykAuthTask = Def.task {
    val log = streams.value.log
    val cmd = List(snykBinary.value, "auth", s"--org=${snykOrganization.value}")
    sys.env.get(authEnvVar) match {
      case None =>
        log.info(s"No auth set up, but presumed we're running locally. Requesting auth via `${cmd.mkString(" ")}`")
        run(cmd, log)
      case Some(auth) =>
        log.debug("Snyk using environment variable authorization, continuing")
        run(cmd :+ auth, log)
    }
  }.tag(snykTag)

  private def run(cmds: List[String], log: Logger): Unit = {
    val shell = if (sys.props("os.name").contains("Windows")) {
      List("cmd", "/c")
    } else {
      Nil
    }
    log.info(s"Running `${(shell ::: cmds).mkString(" ")}`")
    val responseCode = Process(shell ::: cmds) ! convert(log)
    if (responseCode != 0) {
      throw SnykError
    }
  }

  object SnykError extends Error("Snyk process failed") with UnprintableException
}
