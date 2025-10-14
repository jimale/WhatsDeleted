package com.tiriig.whatsdeleted.di

import android.app.Application
import androidx.room.Room
import com.tiriig.whatsdeleted.data.database.Database
import com.tiriig.whatsdeleted.data.database.MIGRATION_2_3
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
            .addMigrations(MIGRATION_2_3)
            .build()
    }
}