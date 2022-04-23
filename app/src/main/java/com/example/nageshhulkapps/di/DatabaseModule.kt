package com.example.nageshhulkapps.di

import android.app.Application
import androidx.room.Room
import com.example.nageshhulkapps.data.local.IVideoDao
import com.example.nageshhulkapps.data.local.MoviesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(application: Application, callback: MoviesDatabase.Callback): MoviesDatabase {
        return Room.databaseBuilder(application, MoviesDatabase::class.java, "movies_database")
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()
    }

    @Provides
    fun provideVideoDao(db: MoviesDatabase): IVideoDao{
        return db.getVideoDao()
    }
}