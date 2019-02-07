package com.twalford

import sbt.Logger

object SbtCompat {

  def convert(log: Logger): scala.sys.process.ProcessLogger = {
    // even in sbt 0.13 there is a built in implicit conversion for this part, but then a further conversion is necessary
    val l: sbt.ProcessLogger = log

    new scala.sys.process.ProcessLogger {
      def out(s: => String): Unit = l.info(s)
      def err(s: => String): Unit = l.error(s)
      def buffer[T](f: => T): T = l.buffer(f)
    }
  }

}
