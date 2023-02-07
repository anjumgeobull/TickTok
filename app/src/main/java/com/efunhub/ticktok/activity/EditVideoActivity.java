package com.efunhub.ticktok.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.coremedia.iso.boxes.Container;
import com.daasuu.gpuv.composer.FillMode;
import com.daasuu.gpuv.composer.GPUMp4Composer;
import com.daasuu.gpuv.composer.Rotation;
import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.filter.GlFilterGroup;
import com.daasuu.gpuv.egl.filter.GlMonochromeFilter;
import com.daasuu.gpuv.egl.filter.GlVignetteFilter;
import com.daasuu.gpuv.player.GPUPlayerView;
import com.daasuu.gpuv.player.PlayerScaleType;
import com.efunhub.ticktok.R;
import com.efunhub.ticktok.adapter.EffectAdapter;
import com.efunhub.ticktok.backgroundservice.BackgroundSoundService;
import com.efunhub.ticktok.effect.FilterAdapter;
import com.efunhub.ticktok.effect.FilterAdjuster;
import com.efunhub.ticktok.effect.FilterType;
import com.efunhub.ticktok.effect.PlayerActivity;
import com.efunhub.ticktok.model.AudioModel;
import com.efunhub.ticktok.utility.VideoPlayerConfig;
import com.efunhub.ticktok.widget.MovieWrapperView;
import com.efunhub.ticktok.widget.PlayerTimer;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.googlecode.mp4parser.authoring.Edit;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class EditVideoActivity extends AppCompatActivity implements View.OnClickListener,Player.EventListener {

    private String TAG="EditVideoActivity";
    RelativeLayout rlNext,rlMusic,rlFilter,rlSticker,rl_Back;

    private final String KEY_VIDEO_URI = "video_uri";
    String videoUri;

    PlayerView videoFullScreenPlayer;
    SimpleExoPlayer player;
    Handler mHandler;
    Runnable mRunnable;

    //video mute and merge
    String musicPath="";
    private ProgressDialog progressDialog;
    String filePathMergeVideoAudio="";
    Boolean result;
    private String waterMarkVideoPath;
    //mute video
    private com.daasuu.gpuv.composer.GPUMp4Composer GPUMp4Composer;
    private GlFilter filter;
    private GlFilter glFilter = new GlFilterGroup(new GlMonochromeFilter(), new GlVignetteFilter());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_video);
        init();

    }


    private void init() {
        rlNext=findViewById(R.id.rlNext);
        rlNext.setOnClickListener(this);

        rl_Back=findViewById(R.id.rlBack);
        rl_Back.setOnClickListener(this);

        rlMusic=findViewById(R.id.rlMusic);
        rlMusic.setOnClickListener(this);

        rlFilter=findViewById(R.id.rlFilter);
        rlFilter.setOnClickListener(this);

        rlSticker=findViewById(R.id.rlSticker);
        rlSticker.setOnClickListener(this);

        videoFullScreenPlayer=findViewById(R.id.videoFullScreenPlayer);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        videoFullScreenPlayer.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
//        getSupportActionBar().hide();
        if (getIntent().hasExtra(KEY_VIDEO_URI)) {
            videoUri = getIntent().getStringExtra(KEY_VIDEO_URI);
        }
        setUp(videoUri);
    }

    private void setUp(String videoUri) {
        initializePlayer();
        if (videoUri == null) {
            return;
        }
        buildMediaSource(Uri.parse(videoUri));
    }
    /* @OnClick(R.id.imageViewExit)
     public void onViewClicked() {
         finish();
     }*/
    private void initializePlayer() {
        if (player == null) {
            // 1. Create a default TrackSelector
           /* LoadControl loadControl = new DefaultLoadControl(
                    new DefaultAllocator(true, 16),
                    VideoPlayerConfig.MIN_BUFFER_DURATION,
                    VideoPlayerConfig.MAX_BUFFER_DURATION,
                    VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
                    VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER, -1, true);
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);*/
            // 2. Create the player
           /* player =
                    ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), trackSelector,
                            loadControl);*/

            //player =  new SimpleExoPlayer.Builder(this).build();
            LoadControl   loadControl = new DefaultLoadControl.Builder()
                    .setBufferDurationsMs(25000, 50000, 1500, 2000).createDefaultLoadControl();

            @DefaultRenderersFactory.ExtensionRendererMode int extensionRendererMode = DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER;

            RenderersFactory renderersFactory = new DefaultRenderersFactory(this) .setExtensionRendererMode(extensionRendererMode);


// Create a player instance.
            player =  new SimpleExoPlayer.Builder(this,renderersFactory).setLoadControl(loadControl).build();
            videoFullScreenPlayer.setPlayer(player);
        }
    }

 /* private void SetupPlayer(){
        SimpleExoPlayer simpleExoPlayer;
// Create a data source factory.
      *//*  dataSourceFactory =
                new DefaultHttpDataSourceFactory(Util.getUserAgent(this
                        , getApplicationInfo().loadLabel(getPackageManager()).toString()));*//*
// Passing Load Control
      LoadControl   loadControl = new DefaultLoadControl.Builder()
                .setBufferDurationsMs(25000, 50000, 1500, 2000).createDefaultLoadControl();

        @DefaultRenderersFactory.ExtensionRendererMode int extensionRendererMode = DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER;

       RenderersFactory renderersFactory = new DefaultRenderersFactory(this) .setExtensionRendererMode(extensionRendererMode);


// Create a player instance.
        simpleExoPlayer =  new SimpleExoPlayer.Builder(this,renderersFactory).setLoadControl(loadControl).build();
// Prepare the player with the media source.

    }*/
    private void buildMediaSource(Uri mUri) {
        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)), bandwidthMeter);
        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mUri);
        // Prepare the player with the source.
        player.prepare(videoSource);
        player.setPlayWhenReady(true);
        player.addListener(this);
        player.setRepeatMode(Player.REPEAT_MODE_ONE);
    }
    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
    private void pausePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }
    private void resumePlayer() {
        if (player != null) {
            player.setPlayWhenReady(true);
            player.getPlaybackState();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        pausePlayer();
        if (mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }


    }
    @Override
    protected void onRestart() {
        super.onRestart();
        resumePlayer();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        //stop music service
        stopService();
    }


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
    }
    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }
    @Override
    public void onLoadingChanged(boolean isLoading) {
    }
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case Player.STATE_BUFFERING:
                //spinnerVideoDetails.setVisibility(View.VISIBLE);
                break;
            case Player.STATE_ENDED:
                // Activate the force enable
                break;
            case Player.STATE_IDLE:
                break;
            case Player.STATE_READY:
                //spinnerVideoDetails.setVisibility(View.GONE);
                break;
            default:
                // status = PlaybackStatus.IDLE;
                break;
        }
    }
    @Override
    public void onRepeatModeChanged(int repeatMode) {
    }
    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
    }
    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }
    @Override
    public void onPositionDiscontinuity(int reason) {
    }
    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }
    @Override
    public void onSeekProcessed() {
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rlNext:
                 goToNextFinalPage();
                break;
            case R.id.rlMusic:
               // startActivity(new Intent(EditVideoActivity.this,MusicActivity.class));
                Intent intentMusic = new Intent(EditVideoActivity.this, MusicActivity.class).putExtra("MuxAudioVideo","Yes").putExtra("video_uri",videoUri);
                startActivityForResult(intentMusic, 2);
                break;
            case R.id.rlFilter:
                Intent intent = new Intent(EditVideoActivity.this, FilterActivity.class);
                intent.putExtra("video_uri",videoUri);
                startActivityForResult(intent, 1);
               // startActivity(new Intent(EditVideoActivity.this,FilterActivity.class).putExtra("video_uri",videoUri));
                break;
            case R.id.rlSticker:
                startActivity(new Intent(EditVideoActivity.this,KVideoEditorDemoActivity.class));
                break;
            case R.id.rlBack:
                finish();
                break;
        }
    }

    private void goToNextFinalPage() {
       startCodec(videoUri);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1) {
                videoUri = data.getStringExtra("filter_video_uri");
                if (videoUri == null) {
                    return;
                }
                Log.d("filter_video_uri",videoUri);
                releasePlayer();
                setUp(videoUri);

            }else if(requestCode==2){
                if (musicPath == null) {
                    return;
                }
                musicPath = data.getStringExtra("musicPath");
                videoUri=data.getStringExtra("mergeAudioVideoPath");

                releasePlayer();
                setUp(videoUri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public  String getVideoFilePath() {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("videoDir", Context.MODE_PRIVATE);
        File file = new File(directory,System.currentTimeMillis() + ".mp4");
        Uri imgUri = Uri.fromFile(file);
        String fileAbsolutePath = file.getAbsolutePath();
        return fileAbsolutePath;
    }


    private void startCodec(String sourceVideoPath) {

        final List<FilterType> filterTypes = FilterType.createFilterList();


        glFilter = null;
        glFilter = FilterType.createGlFilter(filterTypes.get(37), this);


        waterMarkVideoPath= getVideoFilePath();
        // final ProgressBar progressBar = findViewById(R.id.progress_bar);
        // progressBar.setMax(100);


        // findViewById(R.id.start_play_movie).setEnabled(false);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        progressDialog.show();

        GPUMp4Composer = null;
        GPUMp4Composer = new GPUMp4Composer(sourceVideoPath, waterMarkVideoPath)
                .rotation(Rotation.NORMAL)
                .size(720, 720)
                .fillMode(FillMode.PRESERVE_ASPECT_CROP)
                .filter(glFilter)
                // .videoBitrate((int) (0.25 * 30 * 720 * 720))
                // .mute(muteCheckBox.isChecked())
                // .flipHorizontal(flipHorizontalCheckBox.isChecked())
                // .flipVertical(flipVerticalCheckBox.isChecked())
                .listener(new GPUMp4Composer.Listener() {
                    @Override
                    public void onProgress(double progress) {
                        Log.d(TAG, "onProgress = " + progress);
                        //  runOnUiThread(() -> progressBar.setProgress((int) (progress * 100)));
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted()");
                        // exportMp4ToGallery(getApplicationContext(), muteVideofilepath);
                        runOnUiThread(() -> {

                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }

                            startActivity(new Intent(EditVideoActivity.this,PlayEditedVideoActivity.class).putExtra("video_uri",waterMarkVideoPath));

                        });
                    }

                    @Override
                    public void onCanceled() {

                    }

                    @Override
                    public void onFailed(Exception exception) {
                        Log.d(TAG, "onFailed()");
                    }
                })
                .start();

    }

    private void stopService(){
        if( isMyServiceRunning(BackgroundSoundService.class))
        {
            //stop previous running service
            Intent myService = new Intent(EditVideoActivity.this, BackgroundSoundService.class);
            stopService(myService);
        }
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService();
    }

}