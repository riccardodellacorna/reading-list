package com.riccardodellacorna.readinglist.core.common

fun interface Clock {
    fun nowMillis(): Long
}

object SystemTimeClock : Clock {
    override fun nowMillis(): Long = System.currentTimeMillis()
}
