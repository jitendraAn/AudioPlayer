package com.example.audioplayer

import android.support.v4.media.MediaMetadataCompat

fun MediaMetadataCompat.toSong(): MediaItemData? {
    return description?.let {
        MediaItemData(
            it.mediaId ?: "",
            it.title.toString(),
            it.subtitle.toString(),
            it.mediaUri.toString(),false,0,
            it.iconUri.toString(),null
        )
    }
}