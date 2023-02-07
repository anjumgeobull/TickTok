package com.efunhub.ticktok.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.daasuu.gpuv.composer.FillMode;
import com.daasuu.gpuv.composer.GPUMp4Composer;
import com.daasuu.gpuv.composer.Rotation;
import com.daasuu.gpuv.egl.filter.GlFilter;
import com.daasuu.gpuv.egl.filter.GlFilterGroup;
import com.daasuu.gpuv.egl.filter.GlMonochromeFilter;
import com.daasuu.gpuv.egl.filter.GlVignetteFilter;
import com.daasuu.gpuv.player.GPUPlayerView;
import com.efunhub.ticktok.R;
import com.efunhub.ticktok.adapter.EffectAdapter;
import com.efunhub.ticktok.compose.VideoItem;
import com.efunhub.ticktok.compose.VideoListAdapter;
import com.efunhub.ticktok.compose.VideoLoadListener;
import com.efunhub.ticktok.compose.VideoLoader;
import com.efunhub.ticktok.effect.FilterAdapter;
import com.efunhub.ticktok.effect.FilterAdjuster;
import com.efunhub.ticktok.effect.FilterType;
import com.efunhub.ticktok.effect.Mp4ComposeActivity;
import com.efunhub.ticktok.widget.MovieWrapperView;
import com.efunhub.ticktok.widget.PlayerTimer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FilterActivity extends AppCompatActivity implements View.OnClickListener, EffectAdapter.SelectEffectInterface{

    ImageView imgClose;
    String videoUri;
    private GPUPlayerView gpuPlayerView;
    private SimpleExoPlayer player;
    private Button button;
   // private SeekBar timeSeekBar;
    private SeekBar filterSeekBar;
    private PlayerTimer playerTimer;
    private GlFilter filter;
    private FilterAdjuster adjuster;

    RecyclerView rvEffect;
    EffectAdapter effectAdapter;

    //merge video with filter
    private VideoLoader videoLoader;

    private VideoItem videoItem = null;

    private static final String TAG = "SAMPLE";

    private static final int PERMISSION_REQUEST_CODE = 88888;

    private GPUMp4Composer GPUMp4Composer;

   // private CheckBox muteCheckBox;
    //private CheckBox flipVerticalCheckBox;
   // private CheckBox flipHorizontalCheckBox;

    private String videoPath;
    private AlertDialog filterDialog;
    private GlFilter glFilter = new GlFilterGroup(new GlMonochromeFilter(), new GlVignetteFilter());
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        setUpViews();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpSimpleExoPlayer();
        setUoGlPlayerView();
        setUpTimer();

        //merge video with filter
    /*    if (checkPermission()) {
            videoLoader = new VideoLoader(getApplicationContext());
            videoLoader.loadDeviceVideos(new VideoLoadListener() {
                @Override
                public void onVideoLoaded(final List<VideoItem> items) {

                    ListView lv = findViewById(R.id.video_list);
                    VideoListAdapter adapter = new VideoListAdapter(getApplicationContext(), R.layout.row_video_list, items);
                    lv.setAdapter(adapter);

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            videoItem = null;
                            videoItem = items.get(position);
                            findViewById(R.id.start_codec_button).setEnabled(true);
                        }
                    });
                }

                @Override
                public void onFailed(Exception e) {
                    e.printStackTrace();
                }
            });
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        releasePlayer();
        if (playerTimer != null) {
            playerTimer.stop();
            playerTimer.removeMessages(0);
        }
    }

    private void setUpViews() {
        imgClose=findViewById(R.id.imgClose);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        rvEffect = findViewById(R.id.rvEffect);

        imgClose.setOnClickListener(this);
        if (getIntent().hasExtra("video_uri")) {
            videoUri = getIntent().getStringExtra("video_uri");
        }


        // play pause
       /* button = (Button) findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player == null) return;

                if (button.getText().toString().equals(FilterActivity.this.getString(R.string.pause))) {
                    player.setPlayWhenReady(false);
                    button.setText(R.string.play);
                } else {
                    player.setPlayWhenReady(true);
                    button.setText(R.string.pause);
                }
            }
        });*/

        // seek
       /* timeSeekBar = (SeekBar) findViewById(R.id.timeSeekBar);
        timeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (player == null) return;

                if (!fromUser) {
                    // We're not interested in programmatically generated changes to
                    // the progress bar's position.
                    return;
                }

                player.seekTo(progress * 1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // do nothing
            }
        });*/

        filterSeekBar = (SeekBar) findViewById(R.id.filterSeekBar);
        filterSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (adjuster != null) {
                    adjuster.adjust(filter, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // do nothing
            }
        });

        // list
       /* ListView listView = findViewById(R.id.list);
        final List<FilterType> filterTypes = FilterType.createFilterList();
        listView.setAdapter(new FilterAdapter(this, R.layout.row_text, filterTypes));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                filter = FilterType.createGlFilter(filterTypes.get(position), getApplicationContext());
                adjuster = FilterType.createFilterAdjuster(filterTypes.get(position));
                findViewById(R.id.filterSeekBarLayout).setVisibility(adjuster != null ? View.VISIBLE : View.GONE);
                gpuPlayerView.setGlFilter(filter);
            }
        });*/

        setUpRecycler();
    }

    private void setUpRecycler() {
       /* videoList.clear();
        videoList.add("Candy");
        videoList.add("Golden");
        videoList.add("Retro");
        videoList.add("Selfie");
        videoList.add("Violet");
        videoList.add("3 veg");
        videoList.add("Clone");
        videoList.add("Candy");
        videoList.add("Golden");
        videoList.add("Retro");*/

        final List<FilterType> filterTypes = FilterType.createFilterList();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(FilterActivity.this, 2, RecyclerView.HORIZONTAL, false);
        rvEffect.setLayoutManager(gridLayoutManager);
        effectAdapter = new EffectAdapter(FilterActivity.this, filterTypes);
        rvEffect.setAdapter(effectAdapter);

    }


    private void setUpSimpleExoPlayer() {

        TrackSelector trackSelector = new DefaultTrackSelector();

        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "yourApplicationName"), defaultBandwidthMeter);
        MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(videoUri));

        // SimpleExoPlayer
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        // Prepare the player with the source.
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
        player.setRepeatMode(Player.REPEAT_MODE_ONE);


    }


    private void setUoGlPlayerView() {
        gpuPlayerView = new GPUPlayerView(this);
        gpuPlayerView.setSimpleExoPlayer(player);
        gpuPlayerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((MovieWrapperView) findViewById(R.id.layout_movie_wrapper)).addView(gpuPlayerView);
        gpuPlayerView.onResume();
    }


    private void setUpTimer() {
        playerTimer = new PlayerTimer();
        playerTimer.setCallback(new PlayerTimer.Callback() {
            @Override
            public void onTick(long timeMillis) {
                long position = player.getCurrentPosition();
                long duration = player.getDuration();

                if (duration <= 0) return;

              //  timeSeekBar.setMax((int) duration / 1000);
               // timeSeekBar.setProgress((int) position / 1000);
            }
        });
        playerTimer.start();
    }


    private void releasePlayer() {
        gpuPlayerView.onPause();
        ((MovieWrapperView) findViewById(R.id.layout_movie_wrapper)).removeAllViews();
        gpuPlayerView = null;
        player.stop();
        player.release();
        player = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgClose:
                finish();
                break;
        }
    }

    @Override
    public void selectEffect(FilterType filterType) {
        filter = FilterType.createGlFilter(filterType, getApplicationContext());
        adjuster = FilterType.createFilterAdjuster(filterType);
      //  findViewById(R.id.filterSeekBarLayout).setVisibility(adjuster != null ? View.VISIBLE : View.GONE);
        gpuPlayerView.setGlFilter(filter);

        glFilter = null;
        glFilter = FilterType.createGlFilter(filterType, this);
    }

    //merge video with filter
    private void init() {
        findViewById(R.id.start_codec_button).setEnabled(true);
       // muteCheckBox = findViewById(R.id.mute_check_box);
       // flipVerticalCheckBox = findViewById(R.id.flip_vertical_check_box);
      //  flipHorizontalCheckBox = findViewById(R.id.flip_horizontal_check_box);

        findViewById(R.id.start_codec_button).setOnClickListener(v -> {
            v.setEnabled(false);
            startCodec();
        });

        /*findViewById(R.id.cancel_button).setOnClickListener(v -> {
            if (GPUMp4Composer != null) {
                GPUMp4Composer.cancel();
            }
        });*/

       /* findViewById(R.id.start_play_movie).setOnClickListener(v -> {
            Uri uri = Uri.parse(videoPath);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setDataAndType(uri, "video/mp4");
            startActivity(intent);
        });*/

        /*findViewById(R.id.btn_filter).setOnClickListener(v -> {
            if (filterDialog == null) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Choose a filter");
                builder.setOnDismissListener(dialog -> {
                    filterDialog = null;
                });

                final FilterType[] filters = FilterType.values();
                CharSequence[] charList = new CharSequence[filters.length];
                for (int i = 0, n = filters.length; i < n; i++) {
                    charList[i] = filters[i].name();
                }
                builder.setItems(charList, (dialog, item) -> {
                    changeFilter(filters[item]);
                });
                filterDialog = builder.show();
            } else {
                filterDialog.dismiss();
            }
        });*/
    }

  /*  private void changeFilter(FilterType filter) {
        glFilter = null;
        glFilter = FilterType.createGlFilter(filter, this);
        Button button = findViewById(R.id.btn_filter);
        button.setText("Filter : " + filter.name());
    }*/

    private void startCodec() {
        videoPath = getVideoFilePath();

       // final ProgressBar progressBar = findViewById(R.id.progress_bar);
       // progressBar.setMax(100);
        progressDialog.show();

       // findViewById(R.id.start_play_movie).setEnabled(false);

        GPUMp4Composer = null;
        GPUMp4Composer = new GPUMp4Composer(videoUri, videoPath)
                 .rotation(Rotation.NORMAL)
                 .size(720, 720)
                .fillMode(FillMode.PRESERVE_ASPECT_CROP)
                .filter(glFilter)
               // .videoBitrate((int) (1.50 * 30 * 720 * 720))
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
                        exportMp4ToGallery(getApplicationContext(), videoPath);
                        runOnUiThread(() -> {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                          //  progressBar.setProgress(100);
                            findViewById(R.id.start_codec_button).setEnabled(true);
                           // findViewById(R.id.start_play_movie).setEnabled(true);
                            Toast.makeText(FilterActivity.this, "codec complete path =" + videoPath, Toast.LENGTH_SHORT).show();
                            //go back to previous acivity
                            Intent intent=new Intent();
                            intent.putExtra("filter_video_uri",videoPath);
                            setResult(1,intent);
                            finish();//finishing activity
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


    public File getAndroidMoviesFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
    }

   /* public String getVideoFilePath() {
        return getAndroidMoviesFolder().getAbsolutePath() + "/" + new SimpleDateFormat("yyyyMM_dd-HHmmss").format(new Date()) + "filter_apply.mp4";
    }*/

    public  String getVideoFilePath() {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("videoDir", Context.MODE_PRIVATE);
        File file = new File(directory,System.currentTimeMillis() + ".mp4");
        Uri imgUri = Uri.fromFile(file);
        String fileAbsolutePath = file.getAbsolutePath();
        return fileAbsolutePath;
    }

    /**
     * ギャラリーにエクスポート
     *
     * @param filePath
     * @return The video MediaStore URI
     */
    public static void exportMp4ToGallery(Context context, String filePath) {
        // ビデオのメタデータを作成する
        final ContentValues values = new ContentValues(2);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATA, filePath);
        // MediaStoreに登録
        context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                values);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + filePath)));
    }


    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        // request permission if it has not been grunted.
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(FilterActivity.this, "permission has been grunted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FilterActivity.this, "[WARN] permission is not grunted.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}