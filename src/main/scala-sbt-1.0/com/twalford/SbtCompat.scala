package com.twalford

private object SbtCompat {
  def convert(log: sbt.Logger): scala.sys.process.ProcessLogger =
    sbt.Logger.log2PLog(log)
}
