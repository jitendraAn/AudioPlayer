package com.example.audioplayer

import android.content.ComponentName
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.audioplayer.adapter.SongAdapter
import com.example.audioplayer.adapter.SwipeSongAdapter
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.song_item_layout.view.*
import kotlinx.coroutines.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), DownloadTracker.Listener {
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    lateinit var swipeSongAdapter: SwipeSongAdapter

    private var curPlayingSong: MediaItemData? = null
    private var playbackState: PlaybackStateCompat? = null
    private var list: ArrayList<MediaItemData>? = null
    private var isPlay: Boolean = true
    private val pieProgressDrawable: PieProgressDrawable by lazy {
        PieProgressDrawable().apply {
            setColor(ContextCompat.getColor(this@MainActivity, R.color.black))
        }
    }
    private var progressDrawable: ImageView? = null
    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var bd: Bundle = Bundle()

        val list = ArrayList<MediaItemData>()
        val mediaItemData = MediaItemData(
            "wake_up_01",
            "Intro - The Way Of Waking Up (feat. Alan Watts)",
            "Wake Up",
            "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/01_-_Intro_-_The_Way_Of_Waking_Up_feat_Alan_Watts.mp3",
            imageURL = "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/art.jpg"
        )
        list!!.add(mediaItemData)
        val mediaItemData1 = MediaItemData(
            "wake_up_02",
            "Voyage I - Waterfall",
            "Wake Up",
            "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/03_-_Voyage_I_-_Waterfall.mp3",
            imageURL =
            "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/art.jpg"

        )
        list!!.add(mediaItemData1)
        val mediaItemData2 = MediaItemData(
            "wake_up_03",
            "Geisha",
            "The Kyoto Connection",
            "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/02_-_Geisha.mp3",

            imageURL = "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/art.jpg"

        )
        list!!.add(mediaItemData2)
        val mediaItemData3 = MediaItemData(
            "wake_up_04",
            "Geisha",
            "The Kyoto Connection",
            "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/02_-_Geisha.mp3",

            imageURL = "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/art.jpg"

        )
        list!!.add(mediaItemData3)
        val mediaItemData4 = MediaItemData(
            "wake_up_05",
            "Geisha",
            "The Kyoto Connection",
            "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/02_-_Geisha.mp3",

            imageURL = "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/art.jpg"

        )
        list!!.add(mediaItemData4)
        val mediaItemData5 = MediaItemData(
            "wake_up_06",
            "Geisha",
            "The Kyoto Connection",
            "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/02_-_Geisha.mp3",

            imageURL = "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/art.jpg"

        )
        list!!.add(mediaItemData5)
        val mediaItemData6 = MediaItemData(
            "wake_up_07",
            "Geisha",
            "The Kyoto Connection",
            "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/02_-_Geisha.mp3",

            imageURL = "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/art.jpg"

        )
        list!!.add(mediaItemData6)
        val mediaItemData7 = MediaItemData(
            "wake_up_08",
            "Geisha",
            "The Kyoto Connection",
            "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/02_-_Geisha.mp3",

            imageURL = "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/art.jpg"

        )
        list!!.add(mediaItemData7)
        val mediaItemData8 = MediaItemData(
            "wake_up_09",
            "Geisha",
            "The Kyoto Connection",
            "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/02_-_Geisha.mp3",

            imageURL = "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/art.jpg"

        )
        list!!.add(mediaItemData8)
        val mediaItemData9 = MediaItemData(
            "wake_up_10",
            "Geisha",
            "The Kyoto Connection",
            "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/02_-_Geisha.mp3",

            imageURL = "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/art.jpg"

        )
        list!!.add(mediaItemData9)
        val mediaItemData10 = MediaItemData(
            "wake_up_11",
            "Geisha",
            "The Kyoto Connection",
            "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/02_-_Geisha.mp3",

            imageURL = "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/art.jpg"

        )
        list!!.add(mediaItemData10)
        try {
            DownloadService.start(this, MyDownloadService::class.java)
        } catch (e: IllegalStateException) {
            DownloadService.startForeground(this, MyDownloadService::class.java)
        }
        DownloadUtil.getDownloadTracker(this).addListener(this)

        val musicSource = MusicSource(list!!)
        serviceScope.launch {
            musicSource.fetchMediaData()
        }
        bd.putSerializable("mediaItem", musicSource)

        val musicServiceConnection: MusicServiceConnection
        musicServiceConnection = MusicServiceConnection.getInstance(
            this,
            ComponentName(this, MusicService::class.java),
            bd
        )

        mainActivityViewModel = MainActivityViewModel(musicServiceConnection)

        val rcv = findViewById<RecyclerView>(R.id.rcv_songs_list)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar2)
        val constraintLayout_music_bar =
            findViewById<ConstraintLayout>(R.id.constraintLayout_music_bar)
        val img_next = findViewById<ImageView>(R.id.img_next)
        val img_play_pause = findViewById<ImageView>(R.id.img_play_pause)
        val img_previous = findViewById<ImageView>(R.id.img_previous)
        val image = findViewById<ShapeableImageView>(R.id.shapeableImageView)
//        val tv_song_name = findViewById<TextView>(R.id.tv_song_name)
//        val tv_subtitle = findViewById<TextView>(R.id.tv_subtitle)
        val vpSong = findViewById<ViewPager2>(R.id.vpSong)


        val linearLayoutManager = LinearLayoutManager(this)
        rcv.layoutManager = linearLayoutManager
        rcv.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        val songAdapter = SongAdapter()
        songAdapter.songs = list!!
        rcv.adapter = songAdapter
        songAdapter.setOnClickItemListener {
            if (!constraintLayout_music_bar.isVisible) {
                constraintLayout_music_bar.visibility = View.VISIBLE
            }
            mainActivityViewModel.playOrTOggleSong(it)
            switchViewPagerToCurrentSong(it)

            progressDrawable = it.holder?.itemView?.download_state

            val mediaItem = MediaItem.Builder()
                .setUri(it?.songURL)
                .setMediaMetadata(MediaMetadata.Builder().setTitle(it?.title).build())
                .setTag(MediaItemTag(-1, it?.title!!))
                .build()

////
//            if (DownloadUtil.getDownloadTracker(this).isDownloaded(mediaItem)) {
//                Toast.makeText(this, "You've already downloaded the video", Toast.LENGTH_SHORT)
////                    .setAction("Delete") {
////                      //  DownloadUtil.getDownloadTracker(this@PlayerActivity).removeDownload(mediaItem.playbackProperties?.uri)
////                    }
//                    .show()
//            } else {
//                val item = mediaItem.buildUpon()
//                    .setTag((mediaItem.playbackProperties?.tag as MediaItemTag).copy(duration = MusicService.curSongDuration))
//                    .build()
//                if (!DownloadUtil.getDownloadTracker(this)
//                        .hasDownload(item.playbackProperties?.uri)
//                ) {
//                    DownloadUtil.getDownloadTracker(this).toggleDownloadDialogHelper(this, item)
//                } else {
////                    DownloadUtil.getDownloadTracker(this)
////                        .toggleDownloadPopupMenu(this, this, item.playbackProperties?.uri)
//                }
//            }

        }

        mainActivityViewModel.downloadPercent.observe(this) {
            it?.let {
                pieProgressDrawable.level = it.roundToInt()
                progressDrawable?.invalidate()
            }
        }

//        mainActivityViewModel.mediaItems.observe(this, {
//            when (it.statu) {
//                Status.SUCCESS -> {
//                    progressBar.visibility = View.GONE
//                    it.data?.let {
//                        songAdapter.songs = it
//                    }
//                }
//                Status.ERROR -> {
//                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
//                }
//                Status.LOADING -> {
//                    progressBar.visibility = View.GONE
//                }
//            }
//        })
        swipeSongAdapter = SwipeSongAdapter()
        swipeSongAdapter.songs = list!!
        vpSong.adapter = swipeSongAdapter

        vpSong.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (playbackState?.isPlaying == true) {
                    mainActivityViewModel.playOrTOggleSong(swipeSongAdapter.songs[position])
                } else {
                    curPlayingSong = swipeSongAdapter.songs[position]
                }
            }
        })

        mainActivityViewModel.playbackState.observe(this) {
            playbackState = it

        }


        swipeSongAdapter.setOnClickItemListener {
            val playMusicFragment = PlayMusicFragment()
            playMusicFragment.mainActivityViewModel = mainActivityViewModel
            playMusicFragment.musicServiceConnection = musicServiceConnection
            playMusicFragment.show(supportFragmentManager, "playMusicFragment")
        }



        img_next.setOnClickListener {
            mainActivityViewModel.skipToNextSong()
        }
        img_play_pause.setOnClickListener {
            if (isPlay) {
                mainActivityViewModel.songPlay_Pause(isPlay)
                img_play_pause.setImageResource(android.R.drawable.ic_media_play)

                isPlay = false
            } else {
                mainActivityViewModel.songPlay_Pause(isPlay)
                img_play_pause.setImageResource(android.R.drawable.ic_media_pause)

                isPlay = true
            }

        }
        img_previous.setOnClickListener {
            mainActivityViewModel.skipToPrivousSong()
        }
        mainActivityViewModel.isConnected.observe(this, {
            if (it.peekContent().data!!) {
                Toast.makeText(this, "connected", Toast.LENGTH_LONG).show()
            }
        })

        mainActivityViewModel.curPlayingSong.observe(this, {

            val id = it?.description?.mediaId
            val subtitle = it?.description?.subtitle
            val imageUrl = it?.description?.iconUri
            val d = list?.find { id == it.mediaId }
            if (d != null)
                switchViewPagerToCurrentSong(d)
//            tv_song_name.text = title
//            tv_subtitle.text = subtitle
//            image.setImageBitmap(imageUrl)
            img_play_pause.setImageResource(android.R.drawable.ic_media_pause)
            Glide.with(this).load(imageUrl).into(image)

        })

//        mainActivityViewModel.mediaItems.observe(this) {
//            it?.let { result ->
//                when (result.statu) {
//                    Status.SUCCESS -> {
//                        result.data?.let { songs ->
//                            swipeSongAdapter.songs = songs
//                            if (songs.isNotEmpty()) {
//                                Glide.with(this).load((curPlayingSong ?: songs[0]).imageURL)
//                                    .into(imageView)
//                            }
//                            switchViewPagerToCurrentSong(curPlayingSong ?: return@observe)
//                        }
//                    }
//                    Status.ERROR -> Unit
//                    Status.LOADING -> Unit
//                }
//            }
//        }
    }


    private fun switchViewPagerToCurrentSong(song: MediaItemData) {
        val newItemIndex = swipeSongAdapter.songs.indexOf(song)
        if (newItemIndex != -1) {
            vpSong.currentItem = newItemIndex
            curPlayingSong = song
        }
    }

    override fun onStop() {
        super.onStop()
        mainActivityViewModel.stopFlow()
    }
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        DownloadUtil.getDownloadTracker(this).removeListener(this)
    }

    override fun onDownloadsChanged(download: Download) {
        when (download.state) {
            Download.STATE_DOWNLOADING -> {
                if (progressDrawable?.drawable !is PieProgressDrawable) progressDrawable?.setImageDrawable(
                    pieProgressDrawable
                )
                mainActivityViewModel.startFlow(this, download.request.uri)
            }
            Download.STATE_QUEUED, Download.STATE_STOPPED -> {
                progressDrawable?.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this,
                        R.drawable.app_logo
                    )
                )
            }
            Download.STATE_COMPLETED -> {
                progressDrawable?.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this,
                        R.drawable.ic_launcher_foreground
                    )
                )
            }
            Download.STATE_REMOVING -> {
                progressDrawable?.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this,
                        R.drawable.ic_download
                    )
                )
            }
            Download.STATE_FAILED, Download.STATE_RESTARTING -> {
            }
        }
    }
}