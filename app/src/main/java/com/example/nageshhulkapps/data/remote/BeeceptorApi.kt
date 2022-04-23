package com.example.nageshhulkapps.data.remote

import com.example.nageshhulkapps.data.models.HulkVideo
import retrofit2.http.GET

interface BeeceptorApi {

    @GET("movieslist")
    suspend fun fetchMoviesList() : HulkVideo

}