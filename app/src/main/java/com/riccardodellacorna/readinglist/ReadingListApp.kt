package com.riccardodellacorna.readinglist

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import com.riccardodellacorna.readinglist.core.di.appModules

class ReadingListApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@ReadingListApp)
            modules(appModules)
        }
    }
}
