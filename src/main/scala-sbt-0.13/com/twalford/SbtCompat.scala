package com.twalford

import sbt.Logger

object SbtCompat {

  def convert(log: Logger): scala.sys.process.ProcessLogger = {
    val l: sbt.ProcessLogger = Logger.log2PLog(log)

    new scala.sys.process.ProcessLogger {
      def out(s: => String): Unit = l.info(s)
      def err(s: => String): Unit = l.error(s)
      def buffer[T](f: => T): T = l.buffer(f)
    }
  }

}
