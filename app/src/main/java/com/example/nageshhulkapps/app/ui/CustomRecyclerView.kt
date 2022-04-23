package com.example.nageshhulkapps.app.ui

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.nageshhulkapps.MainApplication
import com.example.nageshhulkapps.app.adapter.VideoRecyclerViewAdapterCustom
import com.example.nageshhulkapps.data.local.IVideoDao
import com.example.nageshhulkapps.data.models.Video
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import kotlinx.coroutines.*


class CustomRecyclerView : RecyclerView {
    private lateinit var iVideoDao: IVideoDao
    private val mTAG = "CustomRecyclerView"
    private lateinit var contextApp: Context
    private lateinit var videoSurface: StyledPlayerView
    private var thumbnail: ImageView? = null
    private var progressBar: ProgressBar? = null
    private var viewParent: View? = null
    private var videoPlayer: ExoPlayer? = null
    private var isVideoViewAdded: Boolean = false
    private var playPosition = -1
    private var requestManager: RequestManager? = null
    private var frameLayout: FrameLayout? = null
    private var videoSurfaceHeightDef: Int = 0
    private var screenHeightDef: Int = 0
    private lateinit var videoArrayList: ArrayList<Video>

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        contextApp = context.applicationContext
        screenHeightDef = context.resources.displayMetrics.heightPixels

        videoSurface = StyledPlayerView(contextApp)
        videoSurface.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM

        videoPlayer = ExoPlayer.Builder(contextApp).build()
        videoSurface.useController = true
        videoSurface.player = videoPlayer

        addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == SCROLL_STATE_IDLE) {
                    if (null != thumbnail) {
                        thumbnail?.visibility = View.VISIBLE
                    }

                    playVideo(!recyclerView.canScrollVertically(0))
                }
            }
        })

        addOnChildAttachStateChangeListener(object : OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {

            }

            override fun onChildViewDetachedFromWindow(view: View) {
                if (null != viewParent && viewParent == view) {
                    resetVideoView()
                }
            }

        })

        videoPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        Log.d(mTAG, "onPlayWhenReadyChanged: buffering")
                        progressBar?.visibility = VISIBLE
                    }
                    Player.STATE_ENDED -> {
                        Log.d(mTAG, "onPlayWhenReadyChanged: video end")
                        videoPlayer?.seekTo(0)
                    }
                    Player.STATE_READY -> {
                        Log.d(mTAG, "onPlayerStateChanged: player ready")
                        progressBar?.visibility = GONE
                        if (!isVideoViewAdded) {
                            addVideoView()
                        }
                    }

                    Player.STATE_IDLE -> {
                    }
                }
            }
        })

    }

    internal fun playVideo(isListEnd: Boolean) {
        val targetPos: Int

        if (!isListEnd) {
            val startPos = (layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
            var endPos = (layoutManager as LinearLayoutManager?)!!.findLastVisibleItemPosition()

            if (endPos - startPos > 1) {
                endPos = startPos + 1
            }

            if (startPos < 0 || endPos < 0) {
                return
            }

            targetPos = if (startPos != endPos) {
                val startPosVideoHeight = getVisibleVideoSurfaceHeight(startPos)
                val endPosVideoHeight = getVisibleVideoSurfaceHeight(endPos)

                if (startPosVideoHeight > endPosVideoHeight) {
                    startPos
                } else {
                    endPos
                }
            } else {
                startPos
            }
        } else {
            targetPos = if (this::videoArrayList.isInitialized) {
                videoArrayList.size - 1
            } else {
                0
            }
        }
        Log.d(mTAG, "playVideo at targetPos: $targetPos")

        if (targetPos == playPosition) {
            return
        }
        if (::videoArrayList.isInitialized) {
            if (playPosition != -1) {
                videoArrayList[playPosition].time = videoPlayer?.currentPosition
            }
        }

        playPosition = targetPos
        videoSurface.visibility = View.INVISIBLE
        removeVideoView(videoSurface)

        val currentPos =
            targetPos - ((layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition())

        val child = getChildAt(currentPos) ?: return

        val holder = child.tag as VideoRecyclerViewAdapterCustom.VideoViewHolderCustom

        thumbnail = holder.binding.thumbnail
        progressBar = holder.binding.progressBar
        viewParent = holder.binding.root
        requestManager = holder.requestManager
        frameLayout = holder.binding.mediaC

        videoSurface.player = videoPlayer

       val  httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)

//        val defaultDataSourceFactory = DefaultDataSourceFactory(
//            contextApp, httpDataSourceFactory
//        )

        //A DataSource that reads and writes a Cache.
        val cacheDataSourceFactory = MainApplication.simpleCache?.let {
            CacheDataSource.Factory()
                .setCache(it)
                .setUpstreamDataSourceFactory(httpDataSourceFactory)
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        }


        if (null != holder.videoObject) {
            val mediaUrl = holder.videoObject!!.sources[0]
            val videoSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory!!).createMediaSource(
                MediaItem.fromUri(Uri.parse(mediaUrl))
            )
            videoPlayer?.setMediaSource(videoSource)
            videoPlayer?.prepare()
            videoPlayer?.playWhenReady = true

            CoroutineScope(Dispatchers.Main).launch {
                if (::iVideoDao.isInitialized){
                    iVideoDao.insert(holder.videoObject!!)
                }
            }


        }
    }

    private fun getVisibleVideoSurfaceHeight(playPos: Int): Int {
        val atPos = playPos - (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        Log.d(mTAG, "getVisibleVideoSurfaceHeight: $atPos")

        val child = getChildAt(atPos) ?: return 0
        videoSurfaceHeightDef = child.height
        val location = IntArray(2)
        child.getLocationInWindow(location)

        return if (location[1] < 0) {
            location[1] + videoSurfaceHeightDef
        } else {
            screenHeightDef - location[1]
        }

    }

    // Remove the old player from holder
    private fun removeVideoView(videoView: StyledPlayerView) {
        val parent = videoView.parent as? ViewGroup ?: return
        val index = parent.indexOfChild(videoView)
        if (index >= 0) {
            parent.removeViewAt(index)
            isVideoViewAdded = false
            viewParent?.setOnClickListener(null)
        }
    }

    private fun addVideoView() {
        frameLayout?.addView(videoSurface)
        isVideoViewAdded = true
        videoSurface.requestFocus()
        videoSurface.visibility = VISIBLE
        videoSurface.alpha = 1F
        thumbnail?.visibility = GONE
        //seek video to resume from previous position
        if (::videoArrayList.isInitialized) {
            if (-1 != playPosition) {
                videoPlayer?.seekTo(videoArrayList[playPosition].time ?: 0)
                videoArrayList[playPosition].time = 0L
            }
        }
    }

    private fun resetVideoView() {
        if (isVideoViewAdded) {
            //save time of previous playing video
            if (::videoArrayList.isInitialized) {
                if (playPosition != -1) {
                    videoArrayList[playPosition].time = videoPlayer?.currentPosition
                }
            }
            removeVideoView(videoSurface)
            playPosition = -1
            videoSurface.visibility = INVISIBLE
            thumbnail!!.visibility = VISIBLE
        }
    }

    fun releasePlayer() {
        videoPlayer?.release()
        videoPlayer = null
        viewParent = null
    }

    fun pausePlayer() {
        videoPlayer?.pause()
    }

    fun resumePlayer() {
        videoPlayer?.play()
    }

    fun setVideoArray(videoArrayList: ArrayList<Video>) {
        if (::videoArrayList.isInitialized) {
            this.videoArrayList.clear()
            this.videoArrayList = videoArrayList
        }
    }

    fun setIVideoDao(iVideoDao: IVideoDao) {
        this.iVideoDao = iVideoDao
    }
}