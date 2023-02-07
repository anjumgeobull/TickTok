package com.efunhub.ticktok.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.opengl.GLException;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.coremedia.iso.boxes.Container;
import com.daasuu.gpuv.camerarecorder.CameraRecordListener;
import com.daasuu.gpuv.camerarecorder.GPUCameraRecorder;
import com.daasuu.gpuv.camerarecorder.GPUCameraRecorderBuilder;
import com.daasuu.gpuv.camerarecorder.LensFacing;
import com.daasuu.gpuv.composer.FillMode;
import com.daasuu.gpuv.composer.GPUMp4Composer;
import com.daasuu.gpuv.composer.Rotation;
import com.efunhub.ticktok.R;
import com.efunhub.ticktok.adapter.EffectAdapter;
import com.efunhub.ticktok.adapter.VideoSpeedAdapter;
import com.efunhub.ticktok.backgroundservice.BackgroundSoundService;
import com.efunhub.ticktok.effect.FilterType;
import com.efunhub.ticktok.fragments.EffectFragments;
import com.efunhub.ticktok.model.VideoSpeedModel;
import com.efunhub.ticktok.widget.SampleCameraGLView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;

public class LibraryUploadVideoActivity extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback , EffectAdapter.SelectEffectInterface,VideoSpeedAdapter.VideoSpeedInterface{

    private String TAG1="UploadVideoActivity";
    private static int VIDEO_REQUEST = 101;
    private static int SELECT_IMAGE = 2;
    View root;
    Uri videoUri;
    ImageView imgStartVideo,imgNext;
    ImageView imgGallery, imgEffect;
    LinearLayout llVideoMenu;
    RelativeLayout llMusic,llFlip,rlTimer,rlSpeed;
    BottomSheetDialog bottomSheetDialog;
    TabLayout tbEffect;
    ViewPager vpEffect;

    RecyclerView rvEffect,rcVideoSpeed;
    ArrayList<String> videoList = new ArrayList<>();
    EffectAdapter effectAdapter;

    //video
    private boolean mIsRecordingVideo;

    private static final String TAG = "Camera2VideoFragment";
    private static final int REQUEST_VIDEO_PERMISSIONS = 1;
    private static final String FRAGMENT_DIALOG = "dialog";

    private static final String[] VIDEO_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    //effect
    private SampleCameraGLView sampleGLView;
    protected com.daasuu.gpuv.camerarecorder.GPUCameraRecorder GPUCameraRecorder;
    private String filepath;
    private TextView recordBtn;
    protected LensFacing lensFacing = LensFacing.BACK;
    protected int cameraWidth = 1280;
    protected int cameraHeight = 720;
    protected int videoWidth = 720;
    protected int videoHeight = 720;

    private boolean toggleClick = false;

    private ListView lv;

    TextView tvTimer;

    String filePathMergeVideoAudio="";


    //mute video
    private com.daasuu.gpuv.composer.GPUMp4Composer GPUMp4Composer;
    ProgressDialog progressDialog;
    private String muteVideofilepath;

    //new added
    String musicPath="";
    private FFmpeg ffmpeg;
    String DesfilePathVideoSpeed="";
    String mVideoSpeed="";

    private static final int PERMISSION_REQUEST_CODE = 200;
    String settingFlag="";

    TextView tvVideoTime;

    int videoRecordingTimeCount=0;

    private long VideoDuration;
    private long audioDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_upload_video);
        initData();
        onCreateActivity();
       /* if (!hasPermissionsGranted(VIDEO_PERMISSIONS)) {
            requestVideoPermissions();
            return;
        }*/
        if (!checkPermission()) {
            requestPermission();
        }
    }
    private void initData() {
        llFlip=findViewById(R.id.llFlip);
        llFlip.setOnClickListener(this);

        llMusic=findViewById(R.id.llMusic);
        llMusic.setOnClickListener(this);

        rlTimer=findViewById(R.id.rlTimer);
        rlTimer.setOnClickListener(this);

        imgStartVideo = findViewById(R.id.imgStartVideo);
        imgStartVideo.setOnClickListener(this);

        imgGallery = findViewById(R.id.imggallery);
        imgGallery.setOnClickListener(this);

        imgEffect = findViewById(R.id.imgEffect);
        imgEffect.setOnClickListener(this);

        llVideoMenu=findViewById(R.id.llVideoMenu);

        tvTimer=findViewById(R.id.tvTimer);

        imgNext=findViewById(R.id.imgNext);
        imgNext.setOnClickListener(this);

        rlSpeed=findViewById(R.id.rlSpeed);
        rlSpeed.setOnClickListener(this);

        rcVideoSpeed=findViewById(R.id.rcVideoSpeed);

        tvVideoTime=findViewById(R.id.tvVideoTime);

        setSpeedRecyclerView();

        //loadFFMpegBinary();
    }

    private void setSpeedRecyclerView() {
        //set recycler view
        ArrayList<VideoSpeedModel> videoSpeedArray=new ArrayList<>();
        videoSpeedArray.add(new VideoSpeedModel("Slow","0"));
        videoSpeedArray.add(new VideoSpeedModel("Normal","1"));
        videoSpeedArray.add(new VideoSpeedModel("Fast","0"));
        LinearLayoutManager layoutManager= new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rcVideoSpeed.setLayoutManager(layoutManager);
        VideoSpeedAdapter videoSpeedAdapter = new VideoSpeedAdapter(LibraryUploadVideoActivity.this,videoSpeedArray);
        rcVideoSpeed.setAdapter(videoSpeedAdapter);
    }

    private void startTimer(String startMusicFlag){
        tvTimer.setVisibility(View.VISIBLE);
        tvVideoTime.setVisibility(View.VISIBLE);
        new CountDownTimer(5000,1000) {
            int counter=5;
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText(String.valueOf(counter));
                counter--;
            }
            @Override
            public void onFinish() {
                tvTimer.setVisibility(View.GONE);
                mIsRecordingVideo = true;
                filepath = getVideoFilePath();
                GPUCameraRecorder.start(filepath);

                //start video recording timer
                videoRecordingTimer.start();

                llVideoMenu.setVisibility(View.GONE);
                imgNext.setVisibility(View.VISIBLE);
                imgStartVideo.setImageResource(R.drawable.ic_video_pause);
                imgEffect.setVisibility(View.INVISIBLE);

                //New logic
                if(startMusicFlag.equalsIgnoreCase("Yes"))
                {
                    //start music
                    if(isMyServiceRunning(BackgroundSoundService.class))
                    {
                        //stop previous running service
                        Intent myService = new Intent(LibraryUploadVideoActivity.this, BackgroundSoundService.class);
                        stopService(myService);


                        //start new service
                        Intent serviceIntent = new Intent(LibraryUploadVideoActivity.this, BackgroundSoundService.class);
                        serviceIntent.putExtra("MusicPath",musicPath);
                        startService(serviceIntent);
                    }else {
                        //start new service
                        Intent serviceIntent = new Intent(LibraryUploadVideoActivity.this, BackgroundSoundService.class);
                        serviceIntent.putExtra("MusicPath",musicPath);
                        startService(serviceIntent);
                    }
                }
            }
        }.start();
    }

   /* public void videoRecordingTimer(){

    }*/


    CountDownTimer videoRecordingTimer = new CountDownTimer( 60000 , 1000) {

        @Override
        public void onTick(long millisUntilFinished) {

            if(videoRecordingTimeCount==60 || videoRecordingTimeCount>60){
                //stop recording
            }else {
                videoRecordingTimeCount++;
                Log.d("count", String.valueOf(videoRecordingTimeCount));
                String time = new Integer(videoRecordingTimeCount).toString();

               /* long millis = videoRecordingTimeCount;
                int seconds = (int) (millis / 60);
                int minutes = seconds / 60;
                seconds     = seconds % 60;

                tvVideoTime.setText(String.format("%d:%02d:%02d", minutes, seconds,millis));*/

                tvVideoTime.setText(time +"s");
            }
        }

        @Override
        public void onFinish() {
            mIsRecordingVideo = false;
            GPUCameraRecorder.stop();
            //recordBtn.setText(getString(R.string.app_record));
            imgStartVideo.setImageResource(R.drawable.ic_video_recording);
            imgEffect.setVisibility(View.VISIBLE);
            llVideoMenu.setVisibility(View.VISIBLE);
        }
    };


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgStartVideo:
                if (mIsRecordingVideo) {
                    //stopRecordingVideo();

                    mIsRecordingVideo = false;
                    GPUCameraRecorder.stop();

                    //cancel video recording timer
                    videoRecordingTimer.cancel();

                    //recordBtn.setText(getString(R.string.app_record));
                    imgStartVideo.setImageResource(R.drawable.ic_video_recording);
                    imgEffect.setVisibility(View.VISIBLE);
                    llVideoMenu.setVisibility(View.VISIBLE);

                } else {
                    if(videoRecordingTimeCount==60 || videoRecordingTimeCount>60)
                    {
                       //if video recording limit is finished go to next page
                       goToNextPage();
                    }else {
                        if(musicPath.isEmpty()){
                            //  startRecordingVideo();

                            mIsRecordingVideo = true;
                            filepath = getVideoFilePath();
                            GPUCameraRecorder.start(filepath);

                            //start video recording timer
                            videoRecordingTimer.start();

                            tvVideoTime.setVisibility(View.VISIBLE);

                            imgStartVideo.setImageResource(R.drawable.ic_video_pause);
                            // recordBtn.setText("Stop");
                            imgEffect.setVisibility(View.INVISIBLE);
                            llVideoMenu.setVisibility(View.GONE);
                        }else {
                            startTimer("yes");
                        }
                    }
                }
                break;
            case R.id.imggallery:
                Intent galleryintent = new Intent();
                galleryintent.setType("image/*");
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryintent, "Select Picture"), SELECT_IMAGE);
                break;

            case R.id.imgEffect:
                OpenBottomSheet();
                break;
            case R.id.llFlip:
                releaseCamera();
                if (lensFacing == LensFacing.BACK) {
                    lensFacing = LensFacing.FRONT;
                } else {
                    lensFacing = LensFacing.BACK;
                }
                toggleClick = true;
                break;
            case R.id.llMusic:
                Intent intent = new Intent(LibraryUploadVideoActivity.this, MusicActivity.class).putExtra("MuxAudioVideo","No");
                startActivityForResult(intent, 2);
                break;
            case R.id.imgNext:
                goToNextPage();
                break;
            case R.id.rlTimer:
                startTimer("No");
                break;
            case R.id.rlSpeed:
                if(rcVideoSpeed.getVisibility()==View.VISIBLE)
                {
                    rcVideoSpeed.setVisibility(View.INVISIBLE);
                }else {
                    rcVideoSpeed.setVisibility(View.VISIBLE);
                }
                break;

        }
    }
    public void goToNextPage(){
        stopService();
       /* if(mVideoSpeed.isEmpty() || mVideoSpeed.equalsIgnoreCase("Normal")){
            if(musicPath.isEmpty())
            {
                startActivity(new Intent(LibraryUploadVideoActivity.this,EditVideoActivity.class).putExtra("video_uri",filepath));
            }else {
                //startCodec(filepath);
                calculateDuration(filepath);
            }
        }else {
            if(musicPath.isEmpty())
            {
                executeFastMotionVideoCommand("No");
            }else {
                //selected both music and speed
                executeFastMotionVideoCommand("Yes");
            }
        }*/
        startActivity(new Intent(LibraryUploadVideoActivity.this,EditVideoActivity.class).putExtra("video_uri",filepath));
      //  startActivity(new Intent(LibraryUploadVideoActivity.this,MergeVideoAudioActivity.class).putExtra("video_uri",filepath).putExtra("music",musicPath));
    }

    private void OpenBottomSheet() {
        bottomSheetDialog = new BottomSheetDialog(LibraryUploadVideoActivity.this);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        View sheetView = getLayoutInflater().inflate(R.layout.video_effect_bottomsheet, null);
        bottomSheetDialog.setContentView(sheetView);

        // lv = sheetView.findViewById(R.id.filter_list);
        tbEffect = sheetView.findViewById(R.id.tbEffect);
        vpEffect = sheetView.findViewById(R.id.vpEffect);

        rvEffect = sheetView.findViewById(R.id.rvEffect);

        //setupViewPagerTabs();
        setUpRecycler();
        //setUpRecyclerEffect();
        bottomSheetDialog.show();
    }
   /* private void setUpRecyclerEffect(){
        final List<FilterType> filterTypes = FilterType.createFilterList();
        lv.setAdapter(new FilterAdapter(this, R.layout.row_white_text, filterTypes).whiteMode());
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (GPUCameraRecorder != null) {
                    GPUCameraRecorder.setFilter(FilterType.createGlFilter(filterTypes.get(position), getApplicationContext()));
                }
            }
        });
    }*/

    private void setupViewPagerTabs() {

        LibraryUploadVideoActivity.ViewPagerAdapter adapter = new LibraryUploadVideoActivity.ViewPagerAdapter(getSupportFragmentManager());

        EffectFragments newFragment = new EffectFragments();

        adapter.addFragment(newFragment, "New");

        vpEffect.setAdapter(adapter);
        tbEffect.setupWithViewPager(vpEffect);

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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(LibraryUploadVideoActivity.this, 2, RecyclerView.HORIZONTAL, false);
        rvEffect.setLayoutManager(gridLayoutManager);
        effectAdapter = new EffectAdapter(LibraryUploadVideoActivity.this, filterTypes);
        rvEffect.setAdapter(effectAdapter);

    }

    @Override
    public void selectEffect(FilterType filterType) {
        if (GPUCameraRecorder != null) {
            GPUCameraRecorder.setFilter(FilterType.createGlFilter(filterType, getApplicationContext()));
        }
    }


    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mTitles = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return mFragments.get(i);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mTitles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }

    }






    private void stopService(){
        if( isMyServiceRunning(BackgroundSoundService.class))
        {
            //stop previous running service
            Intent myService = new Intent(LibraryUploadVideoActivity.this, BackgroundSoundService.class);
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


    //effect
    protected void onCreateActivity() {
      /*  findViewById(R.id.btn_flash).setOnClickListener(v -> {
            if (GPUCameraRecorder != null && GPUCameraRecorder.isFlashSupport()) {
                GPUCameraRecorder.switchFlashMode();
                GPUCameraRecorder.changeAutoFocus();
            }
        });*/


        /*findViewById(R.id.btn_image_capture).setOnClickListener(v -> {
            captureBitmap(bitmap -> {
                new Handler().post(() -> {
                    String imagePath = getImageFilePath();
                    saveAsPngImage(bitmap, imagePath);
                    exportPngToGallery(getApplicationContext(), imagePath);
                });
            });
        });*/

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }



    @Override
    protected void onStop() {
        super.onStop();
        releaseCamera();

        //stop music service
        stopService();
    }

    private void releaseCamera() {
        if (sampleGLView != null) {
            sampleGLView.onPause();
        }

        if (GPUCameraRecorder != null) {
            GPUCameraRecorder.stop();
            GPUCameraRecorder.release();
            GPUCameraRecorder = null;
        }

        if (sampleGLView != null) {
            ((FrameLayout) findViewById(R.id.wrap_view)).removeView(sampleGLView);
            sampleGLView = null;
        }
    }


    private void setUpCameraView() {
        runOnUiThread(() -> {
            FrameLayout frameLayout = findViewById(R.id.wrap_view);
            frameLayout.removeAllViews();
            sampleGLView = null;
            sampleGLView = new SampleCameraGLView(getApplicationContext());
            sampleGLView.setTouchListener((event, width, height) -> {
                if (GPUCameraRecorder == null) return;
                GPUCameraRecorder.changeManualFocusPoint(event.getX(), event.getY(), width, height);
            });
            frameLayout.addView(sampleGLView);
        });
    }


    private void setUpCamera() {
        setUpCameraView();

        GPUCameraRecorder = new GPUCameraRecorderBuilder(this, sampleGLView)
                //.recordNoFilter(true)
                .cameraRecordListener(new CameraRecordListener() {
                    @Override
                    public void onGetFlashSupport(boolean flashSupport) {
                        runOnUiThread(() -> {
                            //  findViewById(R.id.btn_flash).setEnabled(flashSupport);
                        });
                    }

                    @Override
                    public void onRecordComplete() {
                        // exportMp4ToGallery(getApplicationContext(), filepath);
                    }

                    @Override
                    public void onRecordStart() {
                        runOnUiThread(() -> {
                            imgEffect.setVisibility(View.INVISIBLE);
                            llVideoMenu.setVisibility(View.GONE);
                        });
                    }

                    @Override
                    public void onError(Exception exception) {
                        Log.e("GPUCameraRecorder", exception.toString());
                    }

                    @Override
                    public void onCameraThreadFinish() {
                        if (toggleClick) {
                            runOnUiThread(() -> {
                                setUpCamera();
                            });
                        }
                        toggleClick = false;
                    }

                    @Override
                    public void onVideoFileReady() {

                    }
                })
                .videoSize(videoWidth, videoHeight)
                .cameraSize(cameraWidth, cameraHeight)
                .lensFacing(lensFacing)
                .build();


    }

//    private void changeFilter(Filters filters) {
//        GPUCameraRecorder.setFilter(Filters.getFilterInstance(filters, getApplicationContext()));
//    }


    private interface BitmapReadyCallbacks {
        void onBitmapReady(Bitmap bitmap);
    }

    private void captureBitmap(final LibraryUploadVideoActivity.BitmapReadyCallbacks bitmapReadyCallbacks) {
        sampleGLView.queueEvent(() -> {
            EGL10 egl = (EGL10) EGLContext.getEGL();
            GL10 gl = (GL10) egl.eglGetCurrentContext().getGL();
            Bitmap snapshotBitmap = createBitmapFromGLSurface(sampleGLView.getMeasuredWidth(), sampleGLView.getMeasuredHeight(), gl);

            runOnUiThread(() -> {
                bitmapReadyCallbacks.onBitmapReady(snapshotBitmap);
            });
        });
    }

    private Bitmap createBitmapFromGLSurface(int w, int h, GL10 gl) {

        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);

        try {
            gl.glReadPixels(0, 0, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
            int offset1, offset2, texturePixel, blue, red, pixel;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    texturePixel = bitmapBuffer[offset1 + j];
                    blue = (texturePixel >> 16) & 0xff;
                    red = (texturePixel << 16) & 0x00ff0000;
                    pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            Log.e("CreateBitmap", "createBitmapFromGLSurface: " + e.getMessage(), e);
            return null;
        }

        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
    }

    public void saveAsPngImage(Bitmap bitmap, String filePath) {
        try {
            File file = new File(filePath);
            FileOutputStream outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*public static void exportMp4ToGallery(Context context, String filePath) {
        final ContentValues values = new ContentValues(2);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATA, filePath);
        context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                values);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + filePath)));
    }*/

    /*public static String getVideoFilePath() {
        return getAndroidMoviesFolder().getAbsolutePath() + "/" + new SimpleDateFormat("yyyyMM_dd-HHmmss").format(new Date()) + "GPUCameraRecorder.mp4";
    }*/

    public  String getVideoFilePath() {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("videoDir", Context.MODE_PRIVATE);
        File file = new File(directory,System.currentTimeMillis() + ".mp4");
        Uri imgUri = Uri.fromFile(file);
        String fileAbsolutePath = file.getAbsolutePath();
        return fileAbsolutePath;
    }

    public static File getAndroidMoviesFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
    }

    private static void exportPngToGallery(Context context, String filePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(filePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);

    }

    public static String getImageFilePath() {
        return getAndroidImageFolder().getAbsolutePath() + "/" + new SimpleDateFormat("yyyyMM_dd-HHmmss").format(new Date()) + "GPUCameraRecorder.png";
    }

    public static File getAndroidImageFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 2) {
                musicPath = data.getStringExtra("musicPath");
                if (musicPath == null) {
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  /*  private void loadFFMpegBinary() {
        try {
            if (ffmpeg == null) {
                Log.d(TAG1, "ffmpeg : era nulo");
                ffmpeg = FFmpeg.getInstance(this);
            }
            ffmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {
                @Override
                public void onStart() {
                    Log.d(TAG1,"loadBinaryonStart");
                }

                @Override
                public void onFinish() {
                    Log.d(TAG1,"loadBinaryonFinish");
                }

                @Override
                public void onFailure() {
                    Log.d(TAG1,"loadBinaryonFailure");
                    // showUnsupportedExceptionDialog();
                    Toast.makeText(LibraryUploadVideoActivity.this, "showUnsupportedExceptionDialog", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess() {
                    Log.d(TAG1, "loadBinaryonSuccess");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            //showUnsupportedExceptionDialog();
            Toast.makeText(LibraryUploadVideoActivity.this, "showUnsupportedExceptionDialog", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.d(TAG1, "EXception no controlada : " + e);
        }
    }*/

  /*  private void executeFastMotionVideoCommand(String isMusicSelected) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(null);
        progressDialog.setCancelable(false);

        File srcFile = null;
        File destFile=null;
        *//*File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
        );

        String filePrefix = "speed_video";
        String fileExtn = ".mp4";*//*
        //  String yourRealPath =Uri.parse(videoUri).toString();
        try {
            Uri uri=Uri.parse(filepath);
            // String path = uri.getPath() ;// "/mnt/sdcard/FileName.mp3"
            srcFile = new File(String.valueOf(uri));
            // file = new File(new URI(videoUri));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String destFilePath=getVideoFilePath();

        try {
            Uri uri=Uri.parse(destFilePath);
            // String path = uri.getPath() ;// "/mnt/sdcard/FileName.mp3"
            destFile = new File(String.valueOf(uri));
            // file = new File(new URI(videoUri));
        } catch (Exception e) {
            e.printStackTrace();
        }


        *//*File dest = new File(moviesDir, filePrefix + fileExtn);
        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
        }*//*


        //  Log.d("MergeVideoAudioActivity", "startTrim: src: " + file.getAbsolutePath());
        // Log.d("MergeVideoAudioActivity", "startTrim: dest: " + dest.getAbsolutePath());
        DesfilePathVideoSpeed = destFile.getAbsolutePath();


        if(mVideoSpeed.equalsIgnoreCase("fast"))
        {
            String[] complexCommand = {"-y", "-i", srcFile.getAbsolutePath(), "-filter_complex", "[0:v]setpts=0.5*PTS[v];[0:a]atempo=2.0[a]", "-map", "[v]", "-map", "[a]", "-b:v", "2097k", "-r", "60", "-vcodec", "mpeg4", DesfilePathVideoSpeed};
            execFFmpegBinary(complexCommand,DesfilePathVideoSpeed,isMusicSelected);
        }else if(mVideoSpeed.equalsIgnoreCase("Slow")) {
            String[] complexCommand = {"-y", "-i", srcFile.getAbsolutePath(), "-filter_complex", "[0:v]setpts=2.0*PTS[v];[0:a]atempo=0.5[a]", "-map", "[v]", "-map", "[a]", "-b:v", "2097k", "-r", "60", "-vcodec", "mpeg4", DesfilePathVideoSpeed};
            execFFmpegBinary(complexCommand,DesfilePathVideoSpeed,isMusicSelected);
        }


    }*/

    /*private void execFFmpegBinary(final String[] command,String DesfilePath,String isMusicSelected) {
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    Log.d(TAG1, "FAILED with output : " + s);
                }

                @Override
                public void onSuccess(String s) {
                    progressDialog.dismiss();
                    Log.d(TAG1, "SUCCESS with output : " + s);
                    Log.d(TAG1, "SUCCESS with output : " + DesfilePath);

                }

                @Override
                public void onProgress(String s) {
                    Log.d(TAG1, "Started command : ffmpeg " + command);

                }

                @Override
                public void onStart() {
                    Log.d(TAG1, "Started command : ffmpeg " + command);
                    progressDialog.setMessage("Processing...");
                    progressDialog.show();
                }

                @Override
                public void onFinish() {
                    Log.d(TAG1, "Finished command : ffmpeg " + command);
                    *//*if (choice != 8 && choice != 9 && choice != 10) {
                        progressDialog.dismiss();
                    }*//*
                    progressDialog.dismiss();
                    if(isMusicSelected.equalsIgnoreCase("Yes"))
                    {
                        calculateDuration(DesfilePath);
                        //startCodec(DesfilePath);
                    }else{
                        startActivity(new Intent(LibraryUploadVideoActivity.this,EditVideoActivity.class).putExtra("video_uri",DesfilePath));
                    }



                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }*/

   /* private void setUpMuxCommand(String whichFileDurationGreater) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(null);
        progressDialog.setCancelable(false);

        File srcFile = null;
        File  destFile=null;
        *//*File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
        );

        String filePrefix = "speed_video";
        String fileExtn = ".mp4";*//*
        //  String yourRealPath =Uri.parse(videoUri).toString();
        try {
            Uri uri=Uri.parse(muteVideofilepath);
            // String path = uri.getPath() ;// "/mnt/sdcard/FileName.mp3"
            srcFile = new File(String.valueOf(uri));
            // file = new File(new URI(videoUri));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String destFilePath=getVideoFilePath();

        try {
            Uri uri=Uri.parse(destFilePath);
            // String path = uri.getPath() ;// "/mnt/sdcard/FileName.mp3"
            destFile = new File(String.valueOf(uri));
            // file = new File(new URI(videoUri));
        } catch (Exception e) {
            e.printStackTrace();
        }

       *//* File dest = new File(moviesDir, filePrefix + fileExtn);
        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
        }*//*


        //  Log.d("MergeVideoAudioActivity", "startTrim: src: " + file.getAbsolutePath());
        // Log.d("MergeVideoAudioActivity", "startTrim: dest: " + dest.getAbsolutePath());
        DesfilePathVideoSpeed = destFile.getAbsolutePath();


        Uri uri=Uri.parse(musicPath);
        File MusicFile = new File(String.valueOf(uri));

        //working but takes to much time for procssing
        // String[] complexcommand={ "-i",srcFile.getAbsolutePath(), "-i" ,MusicFile.getAbsolutePath() ,"-t" , String.valueOf(secondsVideo), DesfilePathVideoSpeed};
        //faster

        if(whichFileDurationGreater.equalsIgnoreCase("AudioLengthGreater"))
        {

            //if audio length is max than video .final output is same as video (Woking as per requirenent)
             String[] complexcommand = new String[]{"-i", srcFile.getAbsolutePath(),"-i",MusicFile.getAbsolutePath(),"-map","1:a","-map","0:v","-codec","copy","-shortest",DesfilePathVideoSpeed};
            execFFmpegMergeAuioVideo(complexcommand,DesfilePathVideoSpeed);
        }else if(whichFileDurationGreater.equalsIgnoreCase("VideoLengthGreater")){
            String[] complexcommand={ "-i" ,MusicFile.getAbsolutePath(), "-i" ,srcFile.getAbsolutePath(), "-y", "-acodec", "copy", "-vcodec" ,"copy", DesfilePathVideoSpeed };
            execFFmpegMergeAuioVideo(complexcommand,DesfilePathVideoSpeed);
        }else {
            String[] complexcommand={ "-i" ,MusicFile.getAbsolutePath(), "-i" ,srcFile.getAbsolutePath(), "-y", "-acodec", "copy", "-vcodec" ,"copy", DesfilePathVideoSpeed };
            execFFmpegMergeAuioVideo(complexcommand,DesfilePathVideoSpeed);
        }



    }*/

 /*   private void execFFmpegMergeAuioVideo(final String[] command,String DesfilePath) {
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    Log.d(TAG, "FAILED with output : " + s);
                }

                @Override
                public void onSuccess(String s) {
                    progressDialog.dismiss();
                    Log.d(TAG, "SUCCESS with output : " + s);
                    Log.d(TAG, "SUCCESS with output : " + DesfilePath);

                   // startActivity(new Intent(LibraryUploadVideoActivity.this,EditVideoActivity.class).putExtra("video_uri",DesfilePathVideoSpeed));

                }

                @Override
                public void onProgress(String s) {
                    Log.d(TAG, "Started command : ffmpeg " + command);

                }

                @Override
                public void onStart() {
                    Log.d(TAG, "Started command : ffmpeg " + command);
                    progressDialog.setMessage("Processing...");
                    progressDialog.show();
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "Finished command : ffmpeg " + command);
                    *//*if (choice != 8 && choice != 9 && choice != 10) {
                        progressDialog.dismiss();
                    }*//*
                    // progressDialog.dismiss();
                    Log.d(TAG, "Finished command : ffmpeg " + command);
                    *//*if (choice != 8 && choice != 9 && choice != 10) {
                        progressDialog.dismiss();
                    }*//*
                    //progressDialog.dismiss();
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        startActivity(new Intent(LibraryUploadVideoActivity.this,EditVideoActivity.class).putExtra("video_uri",DesfilePath));
                    }

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }*/

    private boolean checkPermission() {
       // int result13 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);

        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);


        return result == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;

    }

    private void requestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            ActivityCompat.requestPermissions(LibraryUploadVideoActivity.this, new String[]{
                           // Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO
                    },
                    PERMISSION_REQUEST_CODE);
        } /*else {

            Log.d("SetUpUi","requestPermission");
            setUpUi();

        }*/

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean flag = true;

                    /*for (int i = 0; i < grantResults.length; i++) {
                        String permission = permissions[i];

                         if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)){
                         //ignore write permission
                                    flag=true;
                        }else {
                             if (!(grantResults[i] == PackageManager.PERMISSION_GRANTED)) {
                                 Log.d("permissions", String.valueOf(grantResults[i]));
                                 flag = false;
                             }
                         }
                    }*/
                    for (int i = 0; i < grantResults.length; i++) {
                        if (!(grantResults[i] == PackageManager.PERMISSION_GRANTED)) {
                            Log.d("permissions", String.valueOf(grantResults[i]));
                            flag = false;
                        }
                    }
                    if(!flag)
                    {
                        openSettingsDialog();

                    }else {
                        // Log.d("SetUpUi","onRequestPermissionsResult");
                       // setUpCamera();
                    }
                }

                break;
        }
    }
    private void openSettingsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LibraryUploadVideoActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Required Permissions");
        builder.setMessage("We are taking these permission to record and upload video.Grant them in app settings.");
        builder.setPositiveButton("Take Me To SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                settingFlag="Settings";
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 101);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showPermissionInformationMsg();

            }
        });
        builder.show();

    }
    public void showPermissionInformationMsg() {
        String subtitle = "We are taking these permission to record and upload video";
        AlertDialog.Builder builder = new AlertDialog.Builder(LibraryUploadVideoActivity.this);
        builder.setMessage(subtitle)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (!checkPermission()) {
                            requestPermission();
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void selectVideoSpeed(String speed) {
        mVideoSpeed=speed;
    }

  /*  private void startCodec(String sourceVideoPath,String whichFileDurationGreater) {
        muteVideofilepath= getVideoFilePath();
        // final ProgressBar progressBar = findViewById(R.id.progress_bar);
        // progressBar.setMax(100);


        // findViewById(R.id.start_play_movie).setEnabled(false);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        progressDialog.show();

        GPUMp4Composer = null;
        GPUMp4Composer = new GPUMp4Composer(sourceVideoPath, muteVideofilepath)
                .rotation(Rotation.NORMAL)
                .size(720, 720)
                .fillMode(FillMode.PRESERVE_ASPECT_CROP)
                .mute(true)
                // .filter(glFilter)
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
                            //working
                            setUpMuxCommand(whichFileDurationGreater);


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

    }*/

    @Override
    public void onResume() {
        super.onResume();
        if (settingFlag.equals("Settings")){
            settingFlag="";
            if (!checkPermission()) {
                requestPermission();
            }else {
                Log.d("SetUpUi","onResume");
                setUpCamera();
            }
        }else {
            if(checkPermission()){
                setUpCamera();
            }
        }


    }

/*    public void calculateDuration(String finalVideoPath){

        try{
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(finalVideoPath);
            long durationVideo = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            VideoDuration = TimeUnit.MILLISECONDS.toSeconds(durationVideo);
            Log.d("VideoDuration", String.valueOf(VideoDuration));

            //  retriever.release();

            // MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(musicPath);
            long durationMusic = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            audioDuration = TimeUnit.MILLISECONDS.toSeconds(durationMusic);
            Log.d("secondsMusic", String.valueOf(audioDuration));

            retriever.release();
            String whichDurationGreater;
            if(audioDuration>VideoDuration)
            {
                whichDurationGreater="AudioLengthGreater";
            }else if(VideoDuration>audioDuration){
                whichDurationGreater="VideoLengthGreater";
            }else {
                whichDurationGreater="equal";
            }

            startCodec(finalVideoPath,whichDurationGreater);
        }catch (Exception e){
            e.printStackTrace();
        }

    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stop music service
        stopService();
    }

}