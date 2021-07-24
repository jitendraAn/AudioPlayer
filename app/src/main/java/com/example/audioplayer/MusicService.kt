package com.example.audioplayer

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.cast.CastPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.gms.cast.MediaQueueItem
import com.google.android.gms.cast.framework.CastContext
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.math.log


internal const val UAMP_USER_AGENT = "uamp.next"
internal const val MEDIA_ROOT_ID = "root_id"
internal const val NETWORK_FAILURE = "NETWORK_FAILURE"
internal const val UPDATE_PLAYER_POSITION_INTERVAL = 100L

class MusicService : MediaBrowserServiceCompat() {


    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    var isForegroundService = false
    private var currentPlayingSong: MediaMetadataCompat? = null
    private var isPlayerInitailized = false
    private lateinit var musicSource: MusicSource

    private lateinit var notificationManager: MusicNotificationManager

    //    private lateinit var packageValidator: PackageValidator

    private lateinit var musicPlayerEventListener: MusicPlayerEventListener

    private var currentPlaylistItems: List<MediaMetadataCompat> = emptyList()


    private val dataSourceFactory: DefaultDataSourceFactory by lazy {
        DefaultDataSourceFactory(
            /* context= */ this,
            Util.getUserAgent(/* context= */ this, UAMP_USER_AGENT), /* listener= */
            null
        )
    }

    private val cacheDataSourceFactory: CacheDataSource.Factory by lazy {
        CacheDataSource.Factory().apply {
            setUpstreamDataSourceFactory(dataSourceFactory)
        }.setCache(DownloadUtil.getDownloadCache(this))

    }

    private val uAmpAudioAttributes = AudioAttributes.Builder()
        .setContentType(C.CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

//    private val playerListener = PlayerEventListener()

    /**
     * Configure ExoPlayer to handle audio focus for us.
     * See [Player.AudioComponent.setAudioAttributes] for details.
     */
    private val exoPlayer: ExoPlayer by lazy {
        SimpleExoPlayer.Builder(this)

            .build().apply {
                setAudioAttributes(uAmpAudioAttributes, true)
                setHandleAudioBecomingNoisy(true)

            }
    }

    companion object {
        var curSongDuration = 0L
            private set
    }


    override fun onCreate() {
        super.onCreate()


        val sessionActivityPendingIntent =
            packageManager?.getLaunchIntentForPackage(packageName)?.let { sessionIntent ->
                PendingIntent.getActivity(this, 0, sessionIntent, 0)
            }

        // Create a new MediaSession.
        mediaSession = MediaSessionCompat(this, "MusicService")
            .apply {
                setSessionActivity(sessionActivityPendingIntent)
                isActive = true
            }

        /**
         * In order for [MediaBrowserCompat.ConnectionCallback.onConnected] to be called,
         * a [MediaSessionCompat.Token] needs to be set on the [MediaBrowserServiceCompat].
         *
         * It is possible to wait to set the session token, if required for a specific use-case.
         * However, the token *must* be set by the time [MediaBrowserServiceCompat.onGetRoot]
         * returns, or the connection will fail silently. (The system will not even call
         * [MediaBrowserCompat.ConnectionCallback.onConnectionFailed].)
         */
        sessionToken = mediaSession.sessionToken

        /**
         * The notification manager will use our player and media session to decide when to post
         * notifications. When notifications are posted or removed our listener will be called, this
         * allows us to promote the service to foreground (required so that we're not killed if
         * the main UI is not visible).
         */
        notificationManager = MusicNotificationManager(
            this,
            mediaSession.sessionToken,
            MusicPlayerNotificationListener(this)
        ) {
            curSongDuration = exoPlayer.duration
        }


        // ExoPlayer will manage the MediaSession for us.
        mediaSessionConnector = MediaSessionConnector(mediaSession)

//        packageValidator = PackageValidator(this, R.xml.allowed_media_browser_callers)


        mediaSessionConnector.setQueueNavigator(MusicQueueNavigator(mediaSession))
        mediaSessionConnector.setPlayer(exoPlayer)
        musicPlayerEventListener = MusicPlayerEventListener(this)
        exoPlayer.addListener(musicPlayerEventListener)

        notificationManager.showNotificationForPlayer(exoPlayer)
    }

    private inner class MusicQueueNavigator(
        mediaSession: MediaSessionCompat
    ) : TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat =
            musicSource.songs[windowIndex].description
    }

    private fun preparePlayer(
        songs: List<MediaMetadataCompat>,
        itemToPlay: MediaMetadataCompat?,
        playNow: Boolean
    ) {
        val curSongIndex = if (currentPlayingSong == null) 0 else songs.indexOf(itemToPlay)
        exoPlayer.setMediaSource(musicSource.asMediaSource(cacheDataSourceFactory))
        exoPlayer.seekTo(curSongIndex, 0L)
        exoPlayer.playWhenReady = playNow
        exoPlayer.prepare()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): MediaBrowserServiceCompat.BrowserRoot? {

        val bd = rootHints
        if (bd != null) {
            val recent = bd.getBoolean("android.service.media.extra.RECENT", false)
            if (!recent) {
                val mediItems = bd.getSerializable("mediaItem")
                if (mediItems != null) {
                    musicSource = mediItems as MusicSource
                    if (musicSource != null) {
                        val musicPlaybackPrepare = MusicPlaybackPrepare(musicSource) {
                            currentPlayingSong = it
                            preparePlayer(musicSource.songs, it, true)
                        }

                        mediaSessionConnector.setPlaybackPreparer(musicPlaybackPrepare)
                    }
                }
            }
        }

        return BrowserRoot(MEDIA_ROOT_ID, null)

    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {


        when (parentId) {
            MEDIA_ROOT_ID -> {
                val resultsent = musicSource.whenReady { initialize ->
                    if (!initialize) {
                        result.sendResult(musicSource.asMediaItems())
                        if (!isPlayerInitailized && musicSource.songs.isNotEmpty()) {
                            preparePlayer(musicSource.songs, musicSource.songs[0], false)
                            isPlayerInitailized = true
                        }
                    } else {
                        mediaSession.sendSessionEvent(NETWORK_FAILURE, null)
                        result.sendResult(null)
                    }
                }
                if (!resultsent) {
                    result.detach()
                }
            }
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        exoPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
//        serviceScope.cancel()
        exoPlayer.removeListener(musicPlayerEventListener)
        exoPlayer.release()
    }
}
