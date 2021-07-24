package com.example.audioplayer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.audioplayer.MediaItemData
import com.example.audioplayer.R
import kotlinx.android.synthetic.main.song_item_layout.view.*

class SongAdapter : BaseAdapter(R.layout.song_item_layout) {


     override var differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {

        val mediaItemData = songs.get(position)

        holder.itemView.apply {
            textView.text = mediaItemData.title
            textView2.text = mediaItemData.subtitle
            download_state
        }
        mediaItemData.holder=holder
        Glide.with(holder.itemView.context).load(mediaItemData.imageURL).into(holder.itemView.imageView)
        holder.itemView.setOnClickListener {
                onItemClickListener?.let {
                    it(mediaItemData)
                }

        }

    }

}