package com.example.nageshhulkapps.data.models

import android.content.Context
import com.example.nageshhulkapps.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject


class Mock @Inject constructor(private val context: Context) {
    fun loadMockData(): ArrayList<Video> {
        val mockData = context.resources.openRawResource(R.raw.movie)
        val dataString = mockData.bufferedReader().readText()

        val gson = Gson()
        val hulkVideo = gson.fromJson<HulkVideo>(dataString, HulkVideo::class.java)

        return hulkVideo.categories[0].videos as ArrayList<Video>
    }
}