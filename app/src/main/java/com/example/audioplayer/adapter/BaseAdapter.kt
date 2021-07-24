package com.example.audioplayer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.audioplayer.MediaItemData
import com.example.audioplayer.R
import kotlinx.android.synthetic.main.song_item_layout.view.*

abstract class BaseAdapter(
    private val layoutid:Int
) :RecyclerView.Adapter<BaseAdapter.SongViewHolder>() {

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    protected val diffCallback = object : DiffUtil.ItemCallback<MediaItemData>() {
        override fun areItemsTheSame(oldItem: MediaItemData, newItem: MediaItemData): Boolean {
            return oldItem.mediaId == newItem.mediaId
        }

        override fun areContentsTheSame(oldItem: MediaItemData, newItem: MediaItemData): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

   protected abstract var differ:AsyncListDiffer<MediaItemData>

    var songs: List<MediaItemData>
    get() = differ.currentList
    set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(
            LayoutInflater.from(parent.context).inflate(
               layoutid, parent, false)
        )
    }



    protected var onItemClickListener:((MediaItemData)->Unit)?=null

    fun setOnClickItemListener(listner:(MediaItemData)->Unit){
        onItemClickListener=listner
    }

    override fun getItemCount(): Int {
        return songs.size
    }
}