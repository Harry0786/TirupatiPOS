package com.tirupati.pos.core.util.logging

interface Logger {
    fun d(tag: String, message: String)
    fun e(tag: String, message: String, throwable: Throwable? = null)
}

object DebugLogger : Logger {
    override fun d(tag: String, message: String) = println("D/$tag: $message")
    override fun e(tag: String, message: String, throwable: Throwable?) {
        println("E/$tag: $message")
        throwable?.printStackTrace()
    }
}

object ProductionLogger : Logger {
    override fun d(tag: String, message: String) { /* no-op for production placeholder */ }
    override fun e(tag: String, message: String, throwable: Throwable?) { /* placeholder */ }
}
