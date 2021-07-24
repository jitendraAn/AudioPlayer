package com.example.audioplayer.adapter

import androidx.recyclerview.widget.AsyncListDiffer
import com.bumptech.glide.Glide
import com.example.audioplayer.R
import kotlinx.android.synthetic.main.song_item_layout.view.*
import kotlinx.android.synthetic.main.swipe_item.view.*

class SwipeSongAdapter  : BaseAdapter(R.layout.swipe_item) {


    override var differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {

        val mediaItemData = songs.get(position)

        holder.itemView.apply {
            tv_song_name.text = mediaItemData.title
            tv_subtitle.text = mediaItemData.subtitle
        }
        holder.itemView.setOnClickListener {

            onItemClickListener?.let {
                it(mediaItemData)
            }

        }

    }

}