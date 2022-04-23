package com.example.nageshhulkapps.data.models

sealed class NetworkResult{
    data class Success(val data: List<Video>): NetworkResult()
    data class Error(val error: String): NetworkResult()
}
