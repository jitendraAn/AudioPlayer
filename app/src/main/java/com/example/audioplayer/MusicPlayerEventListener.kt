package com.example.audioplayer

import android.util.Log
import android.widget.Toast
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player

class MusicPlayerEventListener(
    private val musicService: MusicService
) : Player.Listener{
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

        if(playbackState==Player.STATE_READY && !playWhenReady){
            musicService.stopForeground(false)
        }
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        super.onPlayWhenReadyChanged(playWhenReady, reason)
        Log.d("TAG", "onPlayWhenReadyChanged: "+reason)
        if(reason==Player.STATE_READY && !playWhenReady){
          musicService.stopForeground(false)
        }
    }
    override fun onPlaybackStateChanged(state: Int) {
        super.onPlaybackStateChanged(state)
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        Toast.makeText(musicService,"An Unknown error occured",Toast.LENGTH_LONG).show()
    }
}