package com.twalford

import sbt.Logger

private object SbtCompat {
  // in sbt 1.x there is a built in implicit conversion all the way
  def convert(log: Logger): scala.sys.process.ProcessLogger = log
}
