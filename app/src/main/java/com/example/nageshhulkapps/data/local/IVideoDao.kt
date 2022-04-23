package com.example.nageshhulkapps.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nageshhulkapps.data.models.Video

@Dao
interface IVideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(video:Video)

    @Query("SELECT * FROM Video")
    suspend fun getAllVideo(): List<Video>?

    @Query("SELECT * FROM Video WHERE title = :titleValue")
    suspend fun getVideo(titleValue:String): Video?

}