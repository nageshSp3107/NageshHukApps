package com.example.nageshhulkapps.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.nageshhulkapps.data.models.Video
import com.example.nageshhulkapps.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Video::class], version = 1)
@TypeConverters(Converters::class)
abstract class MoviesDatabase: RoomDatabase() {
    abstract fun getVideoDao(): IVideoDao
    class Callback @Inject constructor(
        private val database: Provider<MoviesDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback()
}