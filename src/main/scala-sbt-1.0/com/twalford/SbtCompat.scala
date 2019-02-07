package com.twalford

import sbt.Logger

private object SbtCompat {
  def convert(log: Logger): scala.sys.process.ProcessLogger =
    Logger.log2PLog(log)
}
