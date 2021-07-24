package com.example.audioplayer

import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.android.synthetic.main.fragment_play_music.*
import kotlinx.android.synthetic.main.fragment_play_music.tv_song_name
import kotlinx.android.synthetic.main.swipe_item.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [PlayMusicFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlayMusicFragment : DialogFragment() {

    internal var mainActivityViewModel: MainActivityViewModel? = null
        set(value) {
            field = value
        }
    internal var musicServiceConnection: MusicServiceConnection? = null
    set(value) {
        field=value
        songViewModel = SongViewModel(value!!)
    }
    private var songViewModel: SongViewModel?=null

    private var curPlayingSong: MediaItemData? = null

    private var playbackState: PlaybackStateCompat? = null

    private var shouldUpdateSeekbar = true
    var tv_duration_last: TextView?=null
    var tv_duration_start: TextView?=null
    var img_song: ImageView?=null
    var tv_song_name: TextView?=null
    var tv_subtitle: TextView?=null
    private var isPlay: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle1)
    }

    override fun onStart() {
        super.onStart()
        try {
            val dialog = dialog
            if (dialog != null) {
                val width = ViewGroup.LayoutParams.MATCH_PARENT
                val height = ViewGroup.LayoutParams.MATCH_PARENT
                val window = dialog.window
                if (window != null) {
                    window.setGravity(Gravity.NO_GRAVITY)
                    window.setLayout(width, height)
//                    window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    window.setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_play_music, container, false)
        val img_next = v.findViewById<ImageView>(R.id.img_next)
        val img_play_pause = v.findViewById<ImageView>(R.id.img_play_pause)
        val img_previous = v.findViewById<ImageView>(R.id.img_previous)
        img_song = v.findViewById<ImageView>(R.id.img_song)

        tv_song_name = v.findViewById<TextView>(R.id.tv_song_name)
        tv_subtitle = v.findViewById<TextView>(R.id.tv_subtitle)

        tv_duration_last = v.findViewById<TextView>(R.id.tv_duration_last)
        tv_duration_start = v.findViewById<TextView>(R.id.tv_duration_start)
      val  seekBar = v.findViewById<SeekBar>(R.id.seekBar)


        img_play_pause.setOnClickListener {
//            curPlayingSong?.let {
//                mainActivityViewModel?.playOrTOggleSong(it, true)
//            }
            if (isPlay) {
                mainActivityViewModel?.songPlay_Pause(isPlay)
                img_play_pause.setImageResource(android.R.drawable.ic_media_play)

                isPlay = false
            } else {
                mainActivityViewModel?.songPlay_Pause(isPlay)
                img_play_pause.setImageResource(android.R.drawable.ic_media_pause)

                isPlay = true
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser) {
                    setCurPlayerTimeToTextView(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                shouldUpdateSeekbar = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    mainActivityViewModel?.seekTo(it.progress.toLong())
                    shouldUpdateSeekbar = true
                }
            }
        })

        img_previous.setOnClickListener {
            mainActivityViewModel?.skipToPrivousSong()
        }

        img_next.setOnClickListener {
            mainActivityViewModel?.skipToNextSong()
        }

        subscribeToObservers()
        return v
    }
    private fun subscribeToObservers() {
//        mainActivityViewModel?.mediaItems.observe(viewLifecycleOwner) {
//            it?.let { result ->
//                when(result.status) {
//                    SUCCESS -> {
//                        result.data?.let { songs ->
//                            if(curPlayingSong == null && songs.isNotEmpty()) {
//                                curPlayingSong = songs[0]
//                                updateTitleAndSongImage(songs[0])
//                            }
//                        }
//                    }
//                    else -> Unit
//                }
//            }
//        }
        mainActivityViewModel?.curPlayingSong?.observe(viewLifecycleOwner) {
            if(it == null) return@observe
            curPlayingSong = it.toSong()
            updateTitleAndSongImage(curPlayingSong!!)
        }
        mainActivityViewModel?.playbackState?.observe(viewLifecycleOwner) {
            playbackState = it
//            ivPlayPauseDetail.setImageResource(
//                if(playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
//            )
            seekBar.progress = it?.position?.toInt() ?: 0
        }
        songViewModel?.curPlayerPosition?.observe(viewLifecycleOwner) {
            if(shouldUpdateSeekbar) {
                seekBar.progress = it.toInt()
                Log.d("Jitendra", "shouldUpdateSeekbar: "+it.toInt())
                setCurPlayerTimeToTextView(it)
            }
        }
        songViewModel?.curSongDuration?.observe(viewLifecycleOwner) {

            seekBar.max = it.toInt()
            val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
            tv_duration_last?.text = dateFormat.format(it)
            Log.d("Jitendra", "total duration: "+it.toInt()+" "+dateFormat.format(it))

        }
    }

    private fun setCurPlayerTimeToTextView(ms: Long) {

        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        tv_duration_start?.text = dateFormat.format(ms)
        Log.d("Jitendra", "setCurPlayerTimeToTextView: "+ms +" "+dateFormat.format(ms))
    }

    private fun updateTitleAndSongImage(song: MediaItemData) {
        tv_song_name?.text = song.title
        tv_subtitle?.text = song.subtitle
        Glide.with(requireActivity()).load(song.imageURL).into(img_song!!)
    }
}