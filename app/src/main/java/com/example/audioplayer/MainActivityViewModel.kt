package com.example.audioplayer

import android.content.Context
import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class MainActivityViewModel(
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    private val _mediaItems = MutableLiveData<Resource<List<MediaItemData>>>()
    val mediaItems: LiveData<Resource<List<MediaItemData>>> = _mediaItems

    val isConnected = musicServiceConnection.isConnected
    val networkError = musicServiceConnection.networkError
    val curPlayingSong = musicServiceConnection.curPlayingSong
    val playbackState = musicServiceConnection.playbackState


    init {
        _mediaItems.postValue(Resource.loading(null))
        musicServiceConnection.subscribe(MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {
                override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>
                ) {
                    super.onChildrenLoaded(parentId, children)
                    val items = children.map {
                        MediaItemData(
                            it.mediaId!!,
                            it.description.title.toString(),
                            it.description.subtitle.toString(),
                            it.description.mediaUri.toString(),
                            false, 0,
                            it.description.iconUri.toString(),null
                        )
                    }
                    _mediaItems.postValue(Resource.success(items))
                }
            })
    }
    private val _downloadPercent = MutableLiveData<Float>()
    val downloadPercent: LiveData<Float>
        get() = _downloadPercent

    private var coroutineScope: CoroutineScope? = null


    fun startFlow(context: Context, uri: Uri) {
        coroutineScope?.cancel()
        val job = SupervisorJob()
        coroutineScope = CoroutineScope(Dispatchers.Main + job).apply {
            launch {
                DownloadUtil.getDownloadTracker(context).getCurrentProgressDownload(uri).collect {
                    _downloadPercent.postValue(it)
                }
            }
        }
    }

    fun stopFlow() {
        coroutineScope?.cancel()
    }

    fun skipToPrivousSong() {
        musicServiceConnection.transportControls.skipToPrevious()
    }

    fun skipToNextSong() {
        musicServiceConnection.transportControls.skipToNext()
    }

    fun songPlay_Pause(togle: Boolean) {
        if (togle)
            musicServiceConnection.transportControls.pause()
        else
            musicServiceConnection.transportControls.play()

    }


    fun seekTo(pos: Long) {
        musicServiceConnection.transportControls.seekTo(pos)
    }


    fun playOrTOggleSong(mediaItemData: MediaItemData, toggle: Boolean = false) {
        val isPrepared = playbackState.value?.isPrepared ?: false
        if (isPrepared && mediaItemData.mediaId == curPlayingSong.value?.getString(
                METADATA_KEY_MEDIA_ID
            )
        ) {
            playbackState.value?.let { playbackState ->
                {
                    when {
                        playbackState.isPlaying -> if (toggle) musicServiceConnection.transportControls.pause()
                        playbackState.isPlayEnabled -> if (toggle) musicServiceConnection.transportControls.play()
                        else -> Unit
                    }
                }
            }
        } else {
            musicServiceConnection.transportControls.playFromMediaId(mediaItemData.mediaId, null)
        }
    }

    override fun onCleared() {
        super.onCleared()

        // And then, finally, unsubscribe the media ID that was being watched.
        musicServiceConnection.unsubscribe(
            MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {})
    }
}