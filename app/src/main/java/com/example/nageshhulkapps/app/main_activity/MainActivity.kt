package com.example.nageshhulkapps.app.main_activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.example.nageshhulkapps.R
import com.example.nageshhulkapps.app.adapter.VideoRecyclerViewAdapterCustom
import com.example.nageshhulkapps.data.models.Video
import com.example.nageshhulkapps.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object{
        const val TAG = "MainActivity"
    }

    private var _binding:ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val mainActivityViewModel:MainActivityViewModel by viewModels()
    private var adapter: VideoRecyclerViewAdapterCustom? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        subscribeViewModel()
        loadData()
    }

    private fun subscribeViewModel(){
        mainActivityViewModel.error.observe(
            this,
        ) {
            it?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
            }
        }

        mainActivityViewModel.movieVideos.observe(this){
            it?.let { videos ->
                adapter?.let {
                    it.setVideoArray(videos as ArrayList<Video>)
                    binding.recyclerView.setVideoArray(videos as ArrayList<Video>)
                }
            }
        }
    }

    private fun loadData(){
        val layoutManager = LinearLayoutManager(this)
        adapter = VideoRecyclerViewAdapterCustom(this,arrayListOf(), getGlide())
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
    }

    private fun getGlide(): RequestManager {
        val requestOptions = RequestOptions().placeholder(R.color.purple_200).error(R.color.black)
        return Glide.with(this).setDefaultRequestOptions(requestOptions)
    }

    override fun onResume() {
        super.onResume()
        binding.recyclerView.resumePlayer()
    }

    override fun onPause() {
        super.onPause()
        binding.recyclerView.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.recyclerView.releasePlayer()
        _binding = null
    }
}