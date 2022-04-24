package com.example.nageshhulkapps.app.main_activity

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.example.nageshhulkapps.R
import com.example.nageshhulkapps.app.adapter.VideoRecyclerViewAdapterCustom
import com.example.nageshhulkapps.data.local.IVideoDao
import com.example.nageshhulkapps.data.models.Video
import com.example.nageshhulkapps.databinding.ActivityMainBinding
import com.example.nageshhulkapps.databinding.SwitchLayoutBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object{
        const val TAG = "MainActivity"
    }

    private var videos: ArrayList<Video>? = null
    private var _binding:ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val mainActivityViewModel:MainActivityViewModel by viewModels()
    private var adapter: VideoRecyclerViewAdapterCustom? = null

    @Inject
    lateinit var iVideoDao: IVideoDao

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

        mainActivityViewModel.hasProgressBar.observe(this){
            it?.let {
                if (it){
                    //Progressbar visible
                    binding.progressBar.visibility = View.VISIBLE
                }else{
                    //Progressbar disable
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        mainActivityViewModel.movieVideos.observe(this){
            it?.let { videos ->
                this.videos = videos as ArrayList<Video>
                setVideos(videos as ArrayList<Video>)
            }
        }
    }

    private fun setVideos(videos: ArrayList<Video>) {
        adapter?.setVideoArray(videos)
        binding.recyclerView.setVideoArray(videos)
        Handler(mainLooper).postDelayed({
            binding.recyclerView.playVideo(videos.size == 1)
        }, 1000)
    }

    private fun loadData(){
        val actionBar = supportActionBar
        val switchLayoutBinding = SwitchLayoutBinding.inflate(layoutInflater)
        actionBar?.setCustomView(switchLayoutBinding.root)
        actionBar?.setTitle(R.string.app_name)
        actionBar?.displayOptions = ActionBar.DISPLAY_SHOW_HOME or ActionBar.DISPLAY_SHOW_CUSTOM

        val layoutManager = LinearLayoutManager(this)
        adapter = VideoRecyclerViewAdapterCustom(this,arrayListOf(), getGlide())
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        binding.recyclerView.setIVideoDao(iVideoDao)

        switchLayoutBinding.mySwitchItem.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: CompoundButton?, isChecked: Boolean) {
                if (isChecked){
                    lifecycleScope.launch {
                        val allVideo = iVideoDao.getAllVideo()
                        allVideo?.let { videos->
                            setVideos(videos as ArrayList<Video>)
                        }
                    }

                }else{
                    videos?.let {
                        setVideos(it)
                    }
                }
            }
        })

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