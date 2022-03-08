package com.tiriig.soocelifariimaha.di

import android.app.Application
import androidx.room.Room
import com.tiriig.soocelifariimaha.data.database.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDbModule {

    @Provides
    @Singleton
    fun provideAppDatabase(application: Application): Database {
        return Room
            .databaseBuilder(application, Database::class.java, "database")
            .fallbackToDestructiveMigrationFrom()
            .build()
    }
}