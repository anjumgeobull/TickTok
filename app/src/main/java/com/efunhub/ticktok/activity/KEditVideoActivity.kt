package com.efunhub.ticktok.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.efunhub.ticktok.R
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_k_edit_video.*
import ly.img.android.pesdk.VideoEditorSettingsList
import ly.img.android.pesdk.assets.filter.basic.FilterPackBasic
import ly.img.android.pesdk.assets.font.basic.FontPackBasic
import ly.img.android.pesdk.assets.frame.basic.FramePackBasic
import ly.img.android.pesdk.assets.overlay.basic.OverlayPackBasic
import ly.img.android.pesdk.assets.sticker.animated.StickerPackAnimated
import ly.img.android.pesdk.assets.sticker.emoticons.StickerPackEmoticons
import ly.img.android.pesdk.assets.sticker.shapes.StickerPackShapes
import ly.img.android.pesdk.backend.model.EditorSDKResult
import ly.img.android.pesdk.backend.model.constant.OutputMode
import ly.img.android.pesdk.backend.model.state.LoadSettings
import ly.img.android.pesdk.backend.model.state.VideoEditorSaveSettings
import ly.img.android.pesdk.ui.activity.VideoEditorBuilder
import ly.img.android.pesdk.ui.model.state.*
import ly.img.android.pesdk.ui.panels.item.PersonalStickerAddItem
import ly.img.android.serializer._3.IMGLYFileWriter
import java.io.File
import java.io.IOException

class KEditVideoActivity : AppCompatActivity() ,View.OnClickListener{
    private val KEY_VIDEO_URI = "video_uri"
    private var videoUri: String? = null
    private var mPlayer: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0;
   /* private val hlsUrl =
            "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"*/



    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun createVESDKSettingsList() =
            VideoEditorSettingsList()
                    .configure<UiConfigFilter> {
                        it.setFilterList(FilterPackBasic.getFilterPack())
                    }
                    .configure<UiConfigText> {
                        it.setFontList(FontPackBasic.getFontPack())
                    }
                    .configure<UiConfigFrame> {
                        it.setFrameList(FramePackBasic.getFramePack())
                    }
                    .configure<UiConfigOverlay> {
                        it.setOverlayList(OverlayPackBasic.getOverlayPack())
                    }
                    .configure<UiConfigSticker> {
                        it.setStickerLists(
                                PersonalStickerAddItem(),
                                StickerPackEmoticons.getStickerCategory(),
                                StickerPackShapes.getStickerCategory(),
                                StickerPackAnimated.getStickerCategory()
                        )
                    }
                    .configure<VideoEditorSaveSettings> {
                        // Set custom editor video export settings
                        it.setOutputToGallery(Environment.DIRECTORY_DCIM)
                        it.outputMode = OutputMode.EXPORT_IF_NECESSARY
                    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_k_edit_video)

        //        getSupportActionBar().hide();
        if (intent.hasExtra(KEY_VIDEO_URI)) {
            videoUri = "";
            videoUri = intent.getStringExtra(KEY_VIDEO_URI)
        }

        val rlSticker = findViewById<RelativeLayout>(R.id.rlSticker)
        val rlFilter = findViewById<RelativeLayout>(R.id.rlFilter)
        val rlMusic=findViewById<RelativeLayout>(R.id.rlMusic);
        val rlNext=findViewById<RelativeLayout>(R.id.rlNext);
        val rlBack=findViewById<RelativeLayout>(R.id.rlBack);
        rlFilter.setOnClickListener(this)
        rlSticker.setOnClickListener(this)
        rlMusic.setOnClickListener(this)
        rlNext.setOnClickListener(this)
        rlBack.setOnClickListener(this)
        /*rlSticker.setOnClickListener {
           // openSystemGalleryToSelectVideo()
            openEditor(Uri.parse(videoUri))
        }*/

    }



    private fun initPlayer() {
        mPlayer = SimpleExoPlayer.Builder(this).build()
        // Bind the player to the view.
        playerView.player = mPlayer
        mPlayer!!.playWhenReady = true
      //  mPlayer!!.seekTo(playbackPosition)
        mPlayer!!.prepare(buildMediaSource(), false, false)
        mPlayer!!.setRepeatMode(Player.REPEAT_MODE_ONE)
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initPlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Util.SDK_INT < 24 || mPlayer == null) {
            initPlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        playerView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    private fun releasePlayer() {
        if (mPlayer == null) {
            return
        }
        playWhenReady = mPlayer!!.playWhenReady
        playbackPosition = mPlayer!!.currentPosition
        currentWindow = mPlayer!!.currentWindowIndex
        mPlayer!!.release()
        mPlayer = null
    }

    private fun buildMediaSource(): MediaSource {
      /*  val userAgent =
                Util.getUserAgent(playerView.context, playerView.context.getString(R.string.app_name))

        val dataSourceFactory = DefaultDataSourceFactory(playerView.context,userAgent)
        val hlsMediaSource =
                HlsMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(videoUri))*/

        //1
        val userAgent = Util.getUserAgent(playerView.context, playerView.context.getString(R.string.app_name))
        //2
        val mediaSource = ExtractorMediaSource
                .Factory(DefaultDataSourceFactory(playerView.context, userAgent))
                .setExtractorsFactory(DefaultExtractorsFactory())
                .createMediaSource(Uri.parse(videoUri))

        return mediaSource
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        intent ?: return

        if (resultCode == Activity.RESULT_OK && requestCode == KVideoEditorDemoActivity.CAMERA_AND_GALLERY_RESULT) {
            // Open Editor with some uri in this case with an video selected from the system gallery.
            val selectedVideo = intent.data
            if (selectedVideo != null) {
                openEditor(selectedVideo)
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == KVideoEditorDemoActivity.VESDK_RESULT) {
            // Editor has saved an Video.
            val data = EditorSDKResult(intent)


            //play video which get result from edited page
            videoUri= data.resultUri.toString()

            releasePlayer()
            if (Util.SDK_INT >= 24) {
                initPlayer()
            }

            Log.i("VESDK", "Source video is located here ${data.sourceUri}")
            Log.i("VESDK", "Result video is located here ${data.resultUri}")

            // TODO: Do something with the result video

            // OPTIONAL: read the latest state to save it as a serialisation
            val lastState = data.settingsList
            try {
                IMGLYFileWriter(lastState).writeJson(
                        File(
                                Environment.getExternalStorageDirectory(),
                                "serialisationReadyToReadWithPESDKFileReader.json"
                        )
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }

        } else if (resultCode == Activity.RESULT_CANCELED && requestCode == KVideoEditorDemoActivity.VESDK_RESULT) {
            // Editor was canceled
            val data = EditorSDKResult(intent)

            val sourceURI = data.sourceUri
            Log.d("sourceURI", sourceURI.toString())
            // TODO: Do something with the source...
        }else if (requestCode == 1) {
           // val selectedVideo = intent.data
           // val returnString = data!!.getStringExtra("keyName")
            videoUri = intent!!.getStringExtra("filter_video_uri")
            if (videoUri == null) {
                return
            }
            Log.d("filter_video_uri", videoUri.toString())
            releasePlayer()
            if (Util.SDK_INT >= 24) {
                initPlayer()
            }
        }else if(requestCode == 2){
           /* if (musicPath == null) {
                return
            }*/
           // musicPath = data.getStringExtra("musicPath")
            videoUri = intent!!.getStringExtra("mergeAudioVideoPath")

            releasePlayer()
            if (Util.SDK_INT >= 24) {
                initPlayer()
            }
        }
    }

    fun openEditor(inputSource: Uri?) {
        val settingsList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            createVESDKSettingsList()
        } else {
            Toast.makeText(this, "Video support needs Android 4.3", Toast.LENGTH_LONG).show()
            return
        }

        settingsList.configure<LoadSettings> {
            it.source = inputSource
        }

        settingsList[LoadSettings::class].source = inputSource

        VideoEditorBuilder(this)
                .setSettingsList(settingsList)
                .startActivityForResult(this, KVideoEditorDemoActivity.VESDK_RESULT)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.rlSticker -> {
                    openEditor(Uri.parse(videoUri))
                }
                R.id.rlFilter -> {
                    val intent = Intent(this, FilterActivity::class.java)
                    intent.putExtra("video_uri", videoUri)
                    startActivityForResult(intent, 1)
                    mPlayer?.stop()
                }
                R.id.rlMusic ->{
                    // startActivity(new Intent(EditVideoActivity.this,MusicActivity.class));
                    val intentMusic = Intent(this, MusicActivity::class.java).putExtra("MuxAudioVideo", "Yes").putExtra("video_uri", videoUri)
                    startActivityForResult(intentMusic, 2)
                }
                R.id.rlNext -> {
                    startActivity(Intent(this, ShareVideoActivity::class.java).putExtra("video_uri", videoUri))
                }
                R.id.rlBack -> {
                    finish()
                }
            }
        }
    }

    /*val clickListener = View.OnClickListener {view ->

        when (view.getId()) {
            R.id.textview -> firstFun()
            R.id.button -> secondFun()
        }
    }*/

}