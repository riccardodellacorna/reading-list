package com.riccardodellacorna.readinglist.testutil

import com.riccardodellacorna.readinglist.core.common.Clock

class FakeClock(
    var now: Long = 0L,
) : Clock {
    override fun nowMillis(): Long = now
}
