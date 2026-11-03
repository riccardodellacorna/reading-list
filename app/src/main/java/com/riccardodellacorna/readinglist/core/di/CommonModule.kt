package com.riccardodellacorna.readinglist.core.di

import com.riccardodellacorna.readinglist.core.common.Clock
import com.riccardodellacorna.readinglist.core.common.DefaultDispatcherProvider
import com.riccardodellacorna.readinglist.core.common.DispatcherProvider
import com.riccardodellacorna.readinglist.core.common.SystemTimeClock
import org.koin.dsl.module

val commonModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider }
    single<Clock> { SystemTimeClock }
}
