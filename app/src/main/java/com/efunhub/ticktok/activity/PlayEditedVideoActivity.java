package com.efunhub.ticktok.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.efunhub.ticktok.R;
import com.efunhub.ticktok.utility.VideoPlayerConfig;
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
import com.google.android.exoplayer2.util.Util;

public class PlayEditedVideoActivity extends AppCompatActivity implements View.OnClickListener,Player.EventListener{

    ImageView imgClose;
    //play video
    PlayerView videoFullScreenPlayer;
    //ProgressBar spinnerVideoDetails;
    String videoUri;
    SimpleExoPlayer player;
    Handler mHandler;
    Runnable mRunnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_edited_video);
        init();
        playVideo();
    }

    private void playVideo() {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        if (getIntent().hasExtra("video_uri")) {
            videoUri = getIntent().getStringExtra("video_uri");
        }
        setUp();
    }

    private void init() {
        imgClose=findViewById(R.id.imgClose);
        imgClose.setOnClickListener(this);
        videoFullScreenPlayer=findViewById(R.id.videoFullScreenPlayer);
        videoFullScreenPlayer.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);

    }

    private void setUp() {
        initializePlayer();
        if (videoUri == null) {
            return;
        }
        buildMediaSource(Uri.parse(videoUri));
    }
    private void initializePlayer() {
       /* if (player == null) {
            // 1. Create a default TrackSelector
            LoadControl loadControl = new DefaultLoadControl(
                    new DefaultAllocator(true, 16),
                    VideoPlayerConfig.MIN_BUFFER_DURATION,
                    VideoPlayerConfig.MAX_BUFFER_DURATION,
                    VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
                    VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER, -1, true);
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);
            // 2. Create the player
           *//* player =
                    ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), trackSelector,
                            loadControl);*//*
            player=ExoPlayerFactory.newSimpleInstance(PlayEditedVideoActivity.this,trackSelector,loadControl);
            player.setRepeatMode(Player.REPEAT_MODE_ONE);
            videoFullScreenPlayer.setPlayer(player);
        }*/
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
            LoadControl   loadControl = new DefaultLoadControl.Builder()
                    .setBufferDurationsMs(25000, 50000, 1500, 2000).createDefaultLoadControl();

            @DefaultRenderersFactory.ExtensionRendererMode int extensionRendererMode = DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER;

            RenderersFactory renderersFactory = new DefaultRenderersFactory(this) .setExtensionRendererMode(extensionRendererMode);


// Create a player instance.
            player =  new SimpleExoPlayer.Builder(this,renderersFactory).setLoadControl(loadControl).build();
            videoFullScreenPlayer.setPlayer(player);
        }
    }
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
        player.setRepeatMode(Player.REPEAT_MODE_ONE);
        player.addListener(this);
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
                // spinnerVideoDetails.setVisibility(View.GONE);
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
            case R.id.imgClose:
                  finish();
                break;

        }
    }


}