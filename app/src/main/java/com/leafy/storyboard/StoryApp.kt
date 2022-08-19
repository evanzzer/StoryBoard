package com.leafy.storyboard

import android.app.Application
import com.leafy.storyboard.core.di.CoreModule
import com.leafy.storyboard.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class StoryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@StoryApp)
            modules(
                listOf(
                    CoreModule.databaseModule,
                    CoreModule.networkModule,
                    CoreModule.repositoryModule,
                    AppModule.useCaseModule,
                    AppModule.viewModelModule
                )
            )
        }
    }
}