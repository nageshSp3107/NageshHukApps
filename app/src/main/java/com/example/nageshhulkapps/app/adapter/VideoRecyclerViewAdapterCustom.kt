package com.example.nageshhulkapps.app.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.nageshhulkapps.app.VideoDetailsActivity
import com.example.nageshhulkapps.data.models.Video
import com.example.nageshhulkapps.databinding.RecyclerViewListItemBinding
import com.example.nageshhulkapps.utils.AppConstants


class VideoRecyclerViewAdapterCustom(mContext: Context, videoArrayList: ArrayList<Video>, requestManager: RequestManager): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    internal var videoArrayList: ArrayList<Video> = arrayListOf()
    private var requestManager: RequestManager
    private var mContext:Context? = null
    init {
        this.videoArrayList = videoArrayList
        this.requestManager = requestManager
        this.mContext = mContext
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolderCustom {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RecyclerViewListItemBinding.inflate(layoutInflater, parent, false)
        return VideoViewHolderCustom(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as VideoViewHolderCustom).onBind(requestManager, videoArrayList[position])
    }

    override fun getItemCount(): Int {
        return videoArrayList.size
    }

    fun setVideoArray(videos: ArrayList<Video>) {
        this.videoArrayList = videos
        notifyDataSetChanged()
    }

    inner class VideoViewHolderCustom(val binding: RecyclerViewListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        var requestManager: RequestManager? = null
        var videoObject: Video ?= null

        fun onBind(manager: RequestManager, videoObject: Video) {
            if (videoObject.sources.isNullOrEmpty()) {
                binding.thumbnail.background = AppCompatResources.getDrawable(binding.root.context, android.R.color.holo_blue_dark)
            }
            requestManager = manager
            this.videoObject = videoObject
            binding.root.tag = this
            binding.titleTv.text = videoObject.title
            binding.titleDes.text = videoObject.description
            requestManager?.load(videoObject.sources[0])?.into(binding.thumbnail)

            binding.root.setOnClickListener {
                mContext?.startActivity(Intent(mContext, VideoDetailsActivity::class.java).apply {
                    putExtra(AppConstants.VIDEO_KEY, videoObject)
                })
            }
        }


        fun saveTime(currentPosition: Long?) {
            //videoObject?.time = currentPosition ?: 0
        }

    }
}