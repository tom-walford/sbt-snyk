package com.twalford

import sbt.Logger

object SbtCompat {
  private def sbtProcessLoggerLtoSSPProcessLogger(l: sbt.ProcessLogger): scala.sys.process.ProcessLogger = {
    new scala.sys.process.ProcessLogger {
      def out(s: => String): Unit = l.info(s)
      def err(s: => String): Unit = l.error(s)
      def buffer[T](f: => T): T = l.buffer(f)
    }
  }

  def convert(log: Logger): scala.sys.process.ProcessLogger =
    sbtProcessLoggerLtoSSPProcessLogger(Logger.log2PLog(log))
}
