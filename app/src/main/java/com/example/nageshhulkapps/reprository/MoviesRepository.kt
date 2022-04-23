package com.example.nageshhulkapps.reprository

import com.example.nageshhulkapps.data.models.Mock
import com.example.nageshhulkapps.data.models.NetworkResult
import com.example.nageshhulkapps.data.models.Video
import com.example.nageshhulkapps.data.remote.BeeceptorApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoviesRepository @Inject constructor(private val beeceptorApi: BeeceptorApi, private val mock: Mock) {

    suspend fun getMovies(): NetworkResult {
        return try {
            val fetchMoviesList = beeceptorApi.fetchMoviesList()
            NetworkResult.Success(fetchMoviesList.categories[0].videos)
        }catch (ex: Exception){
            NetworkResult.Error(ex.message.toString())
        }
    }

    suspend fun getMoviesFromRawFile(): NetworkResult {
        return try {
            val fetchMoviesList = mock.loadMockData()
            NetworkResult.Success(fetchMoviesList as List<Video>)

        }catch (ex: Exception){
            NetworkResult.Error(ex.message.toString())
        }
    }
}