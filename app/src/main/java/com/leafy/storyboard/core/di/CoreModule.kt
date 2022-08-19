package com.leafy.storyboard.core.di

import androidx.room.Room
import com.leafy.storyboard.core.data.DataRepository
import com.leafy.storyboard.core.data.DataSource
import com.leafy.storyboard.core.data.local.room.StoryDatabase
import com.leafy.storyboard.core.data.remote.RemoteDataSource
import com.leafy.storyboard.core.data.remote.RemoteInterface
import com.leafy.storyboard.core.data.remote.network.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object CoreModule {
    val databaseModule = module {
        factory { get<StoryDatabase>().storyDao() }
        factory { get<StoryDatabase>().remoteKeysDao() }
        single {
            Room.databaseBuilder(
                androidContext(),
                StoryDatabase::class.java,
                "storyboard.db"
            ).fallbackToDestructiveMigration()
                .build()
        }
    }

    val networkModule = module {
        single {
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE))
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()
        }
        single {
            Retrofit.Builder()
                .baseUrl("https://story-api.dicoding.dev/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(get())
                .build()
                .create(ApiService::class.java)
        }
    }

    val repositoryModule = module {
        single<RemoteInterface> { RemoteDataSource(get(), get()) }
        single<DataSource> { DataRepository(get()) }
    }
}