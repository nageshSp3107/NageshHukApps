package com.example.nageshhulkapps.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "Video",indices = [Index(value = ["title"], unique = true)])
data class Video(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String,
    val sources: List<String>,
    val subtitle: String,
    val thumb: String,
    val title: String,
    var time: Long? = 0
) : Parcelable