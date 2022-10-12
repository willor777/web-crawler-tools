package com.das.webcrawlertools

import java.util.logging.Level
import java.util.logging.Logger

fun Logger.w(tag: String, msg: String) {
    Logger.getGlobal().warning("$tag\t$msg")
}


fun Logger.d(tag: String, msg: String) {
    Logger.getGlobal().log(Level.parse("DEBUG"), "$tag\t$msg")
}