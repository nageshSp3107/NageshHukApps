package com.example.nageshhulkapps.app

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nageshhulkapps.data.models.Video
import com.example.nageshhulkapps.databinding.ActivityVideoDetailsBinding
import com.example.nageshhulkapps.utils.AppConstants
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource

class VideoDetailsActivity : AppCompatActivity() {
    private var videoPlayer: ExoPlayer? = null
    private var _binding: ActivityVideoDetailsBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityVideoDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadData()
    }

    private fun loadData(){
        val video:Video? = intent.getParcelableExtra<Video>(AppConstants.VIDEO_KEY)
        video?.let {
            setTitle(it.title)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            binding.titleDes.text = it.description
            videoPlayer = ExoPlayer.Builder(this).build()
            binding.mediaC.useController = true
            binding.mediaC.player = videoPlayer

            val dataSourceFactory = DefaultDataSource.Factory(this)
            val mediaUrl = it.sources[0]
            val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
                MediaItem.fromUri(Uri.parse(mediaUrl))
            )
            videoPlayer?.setMediaSource(videoSource)
            videoPlayer?.prepare()
            videoPlayer?.playWhenReady = true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return false
    }

    override fun onResume() {
        super.onResume()
        videoPlayer?.play()
    }

    override fun onPause() {
        super.onPause()
        videoPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoPlayer?.release()
        videoPlayer = null
        _binding = null

    }
}