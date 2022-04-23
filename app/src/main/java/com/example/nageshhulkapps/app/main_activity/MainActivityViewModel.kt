package com.example.nageshhulkapps.app.main_activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nageshhulkapps.data.models.NetworkResult
import com.example.nageshhulkapps.data.models.Video
import com.example.nageshhulkapps.reprository.MoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(val moviesRepository: MoviesRepository): ViewModel() {
    init {
        fetchMovies()
    }

    private var _error = MutableLiveData<String>()
    val error:LiveData<String> get() = _error

    private var _movieVideos = MutableLiveData<List<Video>>()
    val movieVideos:LiveData<List<Video>> get() = _movieVideos

    private var _hasProgressBar = MutableLiveData<Boolean>()
    val hasProgressBar:LiveData<Boolean> get() = _hasProgressBar

    private fun fetchMovies(){
        viewModelScope.launch(Dispatchers.Main) {
            val movies = moviesRepository.getMovies()
            when (movies){
                is NetworkResult.Success -> {
                 _movieVideos.value = movies.data
                  _hasProgressBar.value = false
                }
                is NetworkResult.Error -> {
                    _error.value = movies.error
                    _hasProgressBar.value = false
                }
            }
        }
    }
}