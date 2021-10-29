/*
 * Copyright 2018 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.audioplayer

import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem
import androidx.recyclerview.widget.RecyclerView
import com.example.audioplayer.adapter.BaseAdapter
import com.example.audioplayer.adapter.SongAdapter
import java.io.Serializable

/**
 * Data class to encapsulate properties of a [MediaItem].
 *
 * If an item is [browsable] it means that it has a list of child media items that
 * can be retrieved by passing the mediaId to [MediaBrowserCompat.subscribe].
 *
 * Objects of this class are built from [MediaItem]s in
 * [MediaItemFragmentViewModel.subscriptionCallback].
 */
data class MediaItemData(
    val mediaId: String="",
    val title: String="",
    val subtitle: String="",
    var songURL: String="",
    var imageURL: String="",
    var download:Boolean=false,
    var downloadingState:Boolean=false,
    var percent:Int=0,
    var holder: RecyclerView.ViewHolder?=null,
    var status: String?="",
    val projectId: String?="",
    val albumId: String?="",
    var isBlueIndicate:Boolean=false
) : Serializable {

}
