package com.riccardodellacorna.readinglist.testutil

import com.riccardodellacorna.readinglist.core.common.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher

class TestDispatcherProvider(
    override val io: CoroutineDispatcher,
) : DispatcherProvider
