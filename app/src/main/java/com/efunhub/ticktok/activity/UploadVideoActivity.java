package com.efunhub.ticktok.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.opengl.GLException;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.CountDownTimer;
import android.os.Environment;

import android.os.Handler;
import android.os.HandlerThread;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.Log;

import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.LogCallback;
import com.arthenica.mobileffmpeg.LogMessage;
import com.arthenica.mobileffmpeg.Statistics;
import com.arthenica.mobileffmpeg.StatisticsCallback;
import com.coremedia.iso.boxes.Container;
import com.daasuu.gpuv.composer.FillMode;
import com.daasuu.gpuv.composer.GPUMp4Composer;
import com.daasuu.gpuv.composer.Rotation;
import com.efunhub.ticktok.R;
import com.efunhub.ticktok.adapter.EffectAdapter;
import com.efunhub.ticktok.adapter.MusicAdapter;
import com.efunhub.ticktok.adapter.VideoSpeedAdapter;
import com.efunhub.ticktok.backgroundservice.BackgroundSoundService;
import com.efunhub.ticktok.effect.FilterType;
import com.efunhub.ticktok.fragments.EffectFragments;


import com.efunhub.ticktok.model.VideoSpeedModel;
import com.efunhub.ticktok.videorcording.AutoFitTextureView;
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
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static android.media.CamcorderProfile.QUALITY_HIGH_SPEED_LOW;


public class UploadVideoActivity extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback , VideoSpeedAdapter.VideoSpeedInterface {

    private String TAG1="UploadVideoActivity";
    private static int VIDEO_REQUEST = 101;
  //  private static int SELECT_VIDEO = 22;
    View root;
    Uri videoUri;
    ImageView imgStartVideo,imgNext,imgback,imgMusic1;
    ImageView imgGallery, imgEffect,img_back;
    LinearLayout llVideoMenu;
    RelativeLayout llMusic,llFlip,rlTimer,rlSpeed;
    BottomSheetDialog bottomSheetDialog;
    TabLayout tbEffect;
    ViewPager vpEffect;

    RecyclerView rvEffect,rcVideoSpeed;
    EffectAdapter effectAdapter;

    //private ListView lv;

    TextView tvTimer;
    String filePathMergeVideoAudio="";

    //mute video
    private GPUMp4Composer GPUMp4Composer;
    ProgressDialog progressDialog;
    private String muteVideofilepath;

    //video recording

    private boolean mIsRecordingVideo;

    private static final String TAG = "Camera2VideoFragment";
    private static final int REQUEST_VIDEO_PERMISSIONS = 1;
    private static final String FRAGMENT_DIALOG = "dialog";

    private Integer mSensorOrientation;
    private String mNextVideoAbsolutePath;
    private CaptureRequest.Builder mPreviewBuilder;
    private Size mPreviewSize;
    private Size mVideoSize;
    private MediaRecorder mMediaRecorder;
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;
    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();


    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    private static final String[] VIDEO_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    // private AutoFitTextureView mTextureView;
    private AutoFitTextureView mTextureView;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mPreviewSession;

    public static final String CAMERA_FRONT = "1";
    public static final String CAMERA_BACK = "0";

    private String cameraId="1";

    String musicPath="";
    //private FFmpeg ffmpeg;
    String DesfilePathVideoSpeed="";
    String mVideoSpeed="";

    private static final int PERMISSION_REQUEST_CODE = 200;
    String settingFlag="";

    String SpeedVideofilePath;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_upload_video);
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

       // imgEffect = findViewById(R.id.imgEffect);
       // imgEffect.setOnClickListener(this);

        llVideoMenu=findViewById(R.id.llVideoMenu);

        tvTimer=findViewById(R.id.tvTimer);

        imgNext=findViewById(R.id.imgNext);
        imgNext.setOnClickListener(this);

        imgback=findViewById(R.id.img_back1);
        imgback.setOnClickListener(this);

        img_back=findViewById(R.id.img_back);
        img_back.setOnClickListener(this);

        rlSpeed=findViewById(R.id.rlSpeed);
        rlSpeed.setOnClickListener(this);

        imgMusic1=findViewById(R.id.imgMusic1);
        imgMusic1.setOnClickListener(this);

        rcVideoSpeed=findViewById(R.id.rcVideoSpeed);

        //video
        mTextureView = (AutoFitTextureView) findViewById(R.id.texture);

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
        VideoSpeedAdapter videoSpeedAdapter = new VideoSpeedAdapter(UploadVideoActivity.this,videoSpeedArray);
        rcVideoSpeed.setAdapter(videoSpeedAdapter);
    }

    private void startTimer(String startMusicFlag){
        tvTimer.setVisibility(View.VISIBLE);
        new CountDownTimer(5000,1000) {
            int counter=5;
            @Override
            public void onTick(long millisUntilFinished) {
                playAlertSound(R.raw.beep);
                tvTimer.setText(String.valueOf(counter));
                counter--;
            }
            @Override
            public void onFinish() {
                tvTimer.setVisibility(View.GONE);
                startRecordingVideo();
                if(startMusicFlag.equalsIgnoreCase("Yes"))
                {
                   //start music
                    if(isMyServiceRunning(BackgroundSoundService.class))
                    {
                        //stop previous running service
                        Intent myService = new Intent(UploadVideoActivity.this, BackgroundSoundService.class);
                        stopService(myService);


                        //start new service
                        Intent serviceIntent = new Intent(UploadVideoActivity.this, BackgroundSoundService.class);
                        serviceIntent.putExtra("MusicPath",musicPath);
                        startService(serviceIntent);
                    }else {
                        //start new service
                        Intent serviceIntent = new Intent(UploadVideoActivity.this, BackgroundSoundService.class);
                        serviceIntent.putExtra("MusicPath",musicPath);
                        startService(serviceIntent);
                    }
                }
            }
        }.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgStartVideo:
               /* if (mIsRecordingVideo) {
                    //stopRecordingVideo();
                    mIsRecordingVideo = false;
                    GPUCameraRecorder.stop();
                    //recordBtn.setText(getString(R.string.app_record));
                    imgStartVideo.setImageResource(R.drawable.ic_video_recording);
                    imgEffect.setVisibility(View.VISIBLE);
                    llVideoMenu.setVisibility(View.VISIBLE);
                } else {
                  //  startRecordingVideo();
                    mIsRecordingVideo = true;
                    filepath = getVideoFilePath();
                    GPUCameraRecorder.start(filepath);
                    imgStartVideo.setImageResource(R.drawable.ic_video_pause);
                   // recordBtn.setText("Stop");
                    imgEffect.setVisibility(View.INVISIBLE);
                    llVideoMenu.setVisibility(View.GONE);
                }*/
                if (mIsRecordingVideo) {
                    stopRecordingVideo();
                } else {
                    if(musicPath.isEmpty()){
                        startRecordingVideo();
                    }else {
                        startTimer("yes");
                    }

                }
                break;
            case R.id.imggallery:
                /*Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,"video/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, VIDEO_REQUEST);
                } else {
                    Toast.makeText(
                            this,
                            "No Gallery APP installed",
                            Toast.LENGTH_LONG
                    ).show();
                }*/
                startActivity(new Intent(UploadVideoActivity.this,DeviceVideoListActivity.class));
                break;

            /*case R.id.imgEffect:
               // OpenBottomSheet();
                break;*/
            case R.id.llFlip:
                switchCamera();
                break;
            case R.id.imgMusic1:
                Intent intentMusic = new Intent(UploadVideoActivity.this, MusicActivity.class).putExtra("MuxAudioVideo","No");
                startActivityForResult(intentMusic, 2);
                break;
            case R.id.imgNext:
                stopService();
                //working

               /* if(mVideoSpeed.isEmpty() || mVideoSpeed.equalsIgnoreCase("Normal")){
                    if(musicPath.isEmpty())
                    {
                        startActivity(new Intent(UploadVideoActivity.this,EditVideoActivity.class).putExtra("video_uri",mNextVideoAbsolutePath));
                    }else {
                        startCodec();
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

                if(mVideoSpeed.isEmpty() || mVideoSpeed.equalsIgnoreCase("Normal")){
                    startActivity(new Intent(UploadVideoActivity.this,KEditVideoActivity.class).putExtra("video_uri",mNextVideoAbsolutePath));
                }else {
                    executeSpeedVideoCommand();
                }

                break;
            case R.id.img_back:
                finish();
                break;
            case R.id.img_back1:
                stopService();
                finish();
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
            case R.id.llMusic:
                Intent intentMusic1 = new Intent(UploadVideoActivity.this, Music_FromApp.class).putExtra("MuxAudioVideo","No");
                startActivityForResult(intentMusic1, 2);
                break;

        }
    }

    private void OpenBottomSheet() {
        bottomSheetDialog = new BottomSheetDialog(UploadVideoActivity.this);
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

    private void setupViewPagerTabs() {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(UploadVideoActivity.this, 2, RecyclerView.HORIZONTAL, false);
        rvEffect.setLayoutManager(gridLayoutManager);
        effectAdapter = new EffectAdapter(UploadVideoActivity.this, filterTypes);
        rvEffect.setAdapter(effectAdapter);

    }

    @Override
    public void selectVideoSpeed(String speed) {
         mVideoSpeed=speed;
       // loadFFMpegBinary(speed);
    }
    public void playAlertSound(int sound) {

        MediaPlayer mp = MediaPlayer.create(getBaseContext(), sound);
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
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

    private boolean shouldShowRequestPermissionRationale(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Requests permissions needed for recording video.
     */
   /* private void requestVideoPermissions() {
        if (shouldShowRequestPermissionRationale(VIDEO_PERMISSIONS)) {
            new ConfirmationDialog().show(this.getSupportFragmentManager(), FRAGMENT_DIALOG);
        } else {
            ActivityCompat.requestPermissions(this, VIDEO_PERMISSIONS, REQUEST_VIDEO_PERMISSIONS);
        }
    }*/

   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");
        if (requestCode == REQUEST_VIDEO_PERMISSIONS) {
            if (grantResults.length == VIDEO_PERMISSIONS.length) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        ErrorDialog.newInstance(getString(R.string.permission_request))
                                .show(this.getSupportFragmentManager(), FRAGMENT_DIALOG);
                        Toast.makeText(UploadVideoActivity.this, getString(R.string.permission_request), Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            } else {
                ErrorDialog.newInstance(getString(R.string.permission_request))
                        .show(this.getSupportFragmentManager(), FRAGMENT_DIALOG);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }*/

   /* private boolean hasPermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(UploadVideoActivity.this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }*/


    public static class ErrorDialog extends DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.finish();
                        }
                    })
                    .create();
        }

    }

    public  class ConfirmationDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
           // final Fragment parent = getParentFragment();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.permission_request)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(UploadVideoActivity.this, VIDEO_PERMISSIONS,
                                    REQUEST_VIDEO_PERMISSIONS);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                    .create();
        }

    }

    private void stopService(){
        if( isMyServiceRunning(BackgroundSoundService.class))
        {
            //stop previous running service
            Intent myService = new Intent(UploadVideoActivity.this, BackgroundSoundService.class);
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

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void exportMp4ToGallery(Context context, String filePath) {
        final ContentValues values = new ContentValues(2);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATA, filePath);
        context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                values);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + filePath)));
    }

    public static String getVideoFilePath() {
        return getAndroidMoviesFolder().getAbsolutePath() + "/" + new SimpleDateFormat("yyyyMM_dd-HHmmss").format(new Date()) + "Recording.mp4";
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

    private void startCodec() {
        muteVideofilepath= getVideoFilePath();
        // final ProgressBar progressBar = findViewById(R.id.progress_bar);
        // progressBar.setMax(100);


        // findViewById(R.id.start_play_movie).setEnabled(false);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        progressDialog.show();

        GPUMp4Composer = null;
        GPUMp4Composer = new GPUMp4Composer(mNextVideoAbsolutePath, muteVideofilepath)
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
                           //merge audio and video
                            /*Boolean result=mux(muteVideofilepath,musicPath);
                            if(result){
                                if (progressDialog != null) {
                                    progressDialog.dismiss();
                                    startActivity(new Intent(UploadVideoActivity.this,EditVideoActivity.class).putExtra("video_uri",filePathMergeVideoAudio));
                                }
                            }*/
                            //merge audio and video
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                           // setUpMuxCommand();

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

    public boolean mux(String videoFile, String audioFile) {
        Movie video;
        try {
            video = new MovieCreator().build(videoFile);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        Movie audio;
        try {
            audio = new MovieCreator().build(audioFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }

        Track audioTrack = audio.getTracks().get(0);
        video.addTrack(audioTrack);

        Container out = new DefaultMp4Builder().build(video);


      /*  File myDirectory = new File(Environment.getExternalStorageDirectory(), "Merge");
        if (!myDirectory.exists()) {
            myDirectory.mkdirs();
        }
        filePathMergeVideoAudio = myDirectory + "/video" + System.currentTimeMillis() + ".mp4";*/
        filePathMergeVideoAudio=getVideoFilePath();
        try {
            RandomAccessFile ram = new RandomAccessFile(String.format(filePathMergeVideoAudio), "rw");
            FileChannel fc = ram.getChannel();
            out.writeContainer(fc);
            ram.close();
        } catch (IOException e) {
            e.printStackTrace();
            // return null;
        }
        Log.d("filePathMergeVideoAudio",filePathMergeVideoAudio);
        // return filePathMergeVideoAudio;
        return true;
    }

    //video

    private TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture,
                                              int width, int height) {
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture,
                                                int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

    };

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            startPreview();
            mCameraOpenCloseLock.release();
            if (null != mTextureView) {
                configureTransform(mTextureView.getWidth(), mTextureView.getHeight());
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            finish();
        }

    };


    private static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;
            }
        }
        Log.e(TAG, "Couldn't find any suitable video size");
        return choices[choices.length - 1];
    }


    private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("called","onResumecalled");
        if (settingFlag.equals("Settings")){
            settingFlag="";
            if (!checkPermission()) {
                requestPermission();
            }else {
                Log.d("SetUpUi","onResume");
                setUpCamera();
            }
        } else {
            if(checkPermission()){
                setUpCamera();
            }
        }
    }
    public void setUpCamera(){
        startBackgroundThread();
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    public void onPause() {
        if (checkPermission()) {
            closeCamera();
            stopBackgroundThread();
        }
        super.onPause();
    }


    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tries to open a {@link CameraDevice}. The result is listened by `mStateCallback`.
     */
    @SuppressWarnings("MissingPermission")
    private void openCamera(int width, int height) {
        /*if (!hasPermissionsGranted(VIDEO_PERMISSIONS)) {
            requestVideoPermissions();
            return;
        }*/

        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            Log.d(TAG, "tryAcquire");
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            // String cameraId = manager.getCameraIdList()[0];

            // cameraId = manager.getCameraIdList()[1];
            Log.d("cameraId",cameraId);

            // Choose the sizes for camera preview and video recording
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics
                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            if (map == null) {
                throw new RuntimeException("Cannot get available preview/video sizes");
            }
            mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
            mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                    width, height, mVideoSize);

            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            } else {
                mTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
            }
            configureTransform(width, height);
            mMediaRecorder = new MediaRecorder();
            manager.openCamera(cameraId, mStateCallback, null);
        } catch (CameraAccessException e) {
            Toast.makeText(UploadVideoActivity.this, "Cannot access the camera.", Toast.LENGTH_SHORT).show();
            finish();
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            Toast.makeText(this, getString(R.string.camera_error), Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.");
        }
    }

    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            closePreviewSession();
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mMediaRecorder) {
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.");
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    /**
     * Start the camera preview.
     */
    private void startPreview() {
        if (null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            return;
        }
        try {
            closePreviewSession();
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            Surface previewSurface = new Surface(texture);
            mPreviewBuilder.addTarget(previewSurface);

            mCameraDevice.createCaptureSession(Collections.singletonList(previewSurface),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            mPreviewSession = session;
                            updatePreview();
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            Toast.makeText(UploadVideoActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update the camera preview. {@link #startPreview()} needs to be called in advance.
     */
    private void updatePreview() {
        if (null == mCameraDevice) {
            return;
        }
        try {
            setUpCaptureRequestBuilder(mPreviewBuilder);
            HandlerThread thread = new HandlerThread("CameraPreview");
            thread.start();
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
    }

    private void configureTransform(int viewWidth, int viewHeight) {
        if (null == mTextureView || null == mPreviewSize) {
            return;
        }
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    private void setUpMediaRecorder() throws IOException {
        // final Activity activity = getActivity();
       /* if (null == activity) {
            return;
        }*/


        //previous

        if(musicPath.isEmpty()){
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            Log.d("musicPathSelected","No");
        }else {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.UNPROCESSED);
            Log.d("musicPathSelected","Yes");
        }


        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        if (mNextVideoAbsolutePath == null || mNextVideoAbsolutePath.isEmpty()) {
            mNextVideoAbsolutePath = getVideoFilePath(UploadVideoActivity.this);
        }
        mMediaRecorder.setOutputFile(mNextVideoAbsolutePath);
        mMediaRecorder.setVideoEncodingBitRate(10000000);
        mMediaRecorder.setVideoFrameRate(30);

        mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

       /* CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH_SPEED_HIGH);
        mMediaRecorder.setCaptureRate(profile.videoFrameRate / 0.25f);
        mMediaRecorder.setVideoEncodingBitRate(profile.videoBitRate);
        mMediaRecorder.setProfile(profile);
*/


        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);


        //restrict recording duration for 1 min
        mMediaRecorder.setMaxDuration(60000);

        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        switch (mSensorOrientation) {
            case SENSOR_ORIENTATION_DEFAULT_DEGREES:
                mMediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));
                break;
            case SENSOR_ORIENTATION_INVERSE_DEGREES:
                mMediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));
                break;
        }
        mMediaRecorder.prepare();
    }

    private String getVideoFilePath(Context context) {
        final File dir = context.getExternalFilesDir(null);
        return (dir == null ? "" : (dir.getAbsolutePath() + "/"))
                + System.currentTimeMillis() + ".mp4";
    }

    private void startRecordingVideo() {
        //once video start invisible other layout and visible next button

        rcVideoSpeed.setVisibility(View.INVISIBLE);
        llVideoMenu.setVisibility(View.GONE);
        imgGallery.setVisibility(View.INVISIBLE);
        imgMusic1.setVisibility(View.INVISIBLE);
        imgNext.setVisibility(View.VISIBLE);
        imgback.setVisibility(View.VISIBLE);

        imgStartVideo.setImageResource(R.drawable.ic_video_pause);
       // imgEffect.setVisibility(View.INVISIBLE);

        if (null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            return;
        }
        try {
            closePreviewSession();
            setUpMediaRecorder();
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            List<Surface> surfaces = new ArrayList<>();

            // Set up Surface for the camera preview
            Surface previewSurface = new Surface(texture);
            surfaces.add(previewSurface);
            mPreviewBuilder.addTarget(previewSurface);

            // Set up Surface for the MediaRecorder
            Surface recorderSurface = mMediaRecorder.getSurface();
            surfaces.add(recorderSurface);
            mPreviewBuilder.addTarget(recorderSurface);

            // Start a capture session
            // Once the session starts, we can update the UI and start recording
            mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    mPreviewSession = cameraCaptureSession;
                    updatePreview();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // UI
                            // mButtonVideo.setText("Stop");
                            imgStartVideo.setImageResource(R.drawable.ic_video_pause);
                            mIsRecordingVideo = true;

                            // Start recording
                            mMediaRecorder.start();
                        }
                    });
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    // Activity activity = this;
                    /*if (null != activity) {
                        Toast.makeText(UploadVideoActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }*/
                    Toast.makeText(UploadVideoActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException | IOException e) {
            e.printStackTrace();
        }

    }

    private void closePreviewSession() {
        if (mPreviewSession != null) {
            mPreviewSession.close();
            mPreviewSession = null;
        }
    }

    private void stopRecordingVideo() {
        llVideoMenu.setVisibility(View.VISIBLE);
        //imgEffect.setVisibility(View.VISIBLE);

        imgGallery.setVisibility(View.INVISIBLE);
        imgMusic1.setVisibility(View.INVISIBLE);
        imgNext.setVisibility(View.VISIBLE);
        imgback.setVisibility(View.VISIBLE);

        // UI
        mIsRecordingVideo = false;
        // mButtonVideo.setText("Record");
        imgStartVideo.setImageResource(R.drawable.ic_video_recording);
        // Stop recording
        try{
            mMediaRecorder.stop();
        }catch (Exception e){
            e.printStackTrace();
        }

        mMediaRecorder.reset();

        Activity activity =this;
        if (null != activity) {
            Toast.makeText(activity, "Video saved: " + mNextVideoAbsolutePath,
                    Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Video saved: " + mNextVideoAbsolutePath);
        }
        //uncomment this when want to remove previous path
        //mNextVideoAbsolutePath = null;
        startPreview();
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }


    public void switchCamera() {
        if (cameraId.equals(CAMERA_FRONT)) {
            cameraId = CAMERA_BACK;
            closeCamera();
            reopenCamera();
            // switchCameraButton.setImageResource(R.drawable.ic_camera_front);

        } else if (cameraId.equals(CAMERA_BACK)) {
            cameraId = CAMERA_FRONT;
            closeCamera();
            reopenCamera();
            // switchCameraButton.setImageResource(R.drawable.ic_camera_back);
        }
    }

    public void reopenCamera() {
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
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
            }/*else if(requestCode==VIDEO_REQUEST){
                Log.d("called","onActivityResultcalled");
                selectedGalleryVideoUri=data.getData();
                if(selectedGalleryVideoUri!=null)
                {
                    Log.d("selectedVideoUri", String.valueOf(selectedGalleryVideoUri));
                    galleryVideoFlag="galleryVideo";
                }

               // startActivity(new Intent(UploadVideoActivity.this,EditVideoActivity.class).putExtra("video_uri",String.valueOf(selectedGalleryVideoUri)));
              // startActivity(new Intent(UploadVideoActivity.this,MainActivity.class));
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* private void loadFFMpegBinary() {
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
                    Toast.makeText(UploadVideoActivity.this, "showUnsupportedExceptionDialog", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess() {
                    Log.d(TAG1, "loadBinaryonSuccess");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            //showUnsupportedExceptionDialog();
            Toast.makeText(UploadVideoActivity.this, "showUnsupportedExceptionDialog", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.d(TAG1, "EXception no controlada : " + e);
        }
    }
*/
   /* private void executeFastMotionVideoCommand(String isMusicSelected) {
               progressDialog = new ProgressDialog(this);
                progressDialog.setTitle(null);
                progressDialog.setCancelable(false);

        File srcFile = null;
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
        );

        String filePrefix = "speed_video";
        String fileExtn = ".mp4";
        //  String yourRealPath =Uri.parse(videoUri).toString();
        try {
            Uri uri=Uri.parse(mNextVideoAbsolutePath);
           // String path = uri.getPath() ;// "/mnt/sdcard/FileName.mp3"
            srcFile = new File(String.valueOf(uri));
            // file = new File(new URI(videoUri));
        } catch (Exception e) {
            e.printStackTrace();
        }


        File dest = new File(moviesDir, filePrefix + fileExtn);
        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
        }


        //  Log.d("MergeVideoAudioActivity", "startTrim: src: " + file.getAbsolutePath());
        // Log.d("MergeVideoAudioActivity", "startTrim: dest: " + dest.getAbsolutePath());
         DesfilePathVideoSpeed = dest.getAbsolutePath();


       if(mVideoSpeed.equalsIgnoreCase("fast"))
       {
           String[] complexCommand = {"-y", "-i", srcFile.getAbsolutePath(), "-filter_complex", "[0:v]setpts=0.5*PTS[v];[0:a]atempo=2.0[a]", "-map", "[v]", "-map", "[a]", "-b:v", "2097k", "-r", "60", "-vcodec", "mpeg4", DesfilePathVideoSpeed};
           execFFmpegBinary(complexCommand,DesfilePathVideoSpeed,isMusicSelected);
       }else if(mVideoSpeed.equalsIgnoreCase("Slow")) {
           String[] complexCommand = {"-y", "-i", srcFile.getAbsolutePath(), "-filter_complex", "[0:v]setpts=2.0*PTS[v];[0:a]atempo=0.5[a]", "-map", "[v]", "-map", "[a]", "-b:v", "2097k", "-r", "60", "-vcodec", "mpeg4", DesfilePathVideoSpeed};
           execFFmpegBinary(complexCommand,DesfilePathVideoSpeed,isMusicSelected);
       }


    }*/

  /*  private void execFFmpegBinary(final String[] command,String DesfilePath,String isMusicSelected) {
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
                       startCodec();
                    }else{
                        startActivity(new Intent(UploadVideoActivity.this,EditVideoActivity.class).putExtra("video_uri",DesfilePathVideoSpeed));
                    }



                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }*/

 /*   private void setUpMuxCommand() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(null);
        progressDialog.setCancelable(false);

        File srcFile = null;
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
        );

        String filePrefix = "speed_video";
        String fileExtn = ".mp4";
        //  String yourRealPath =Uri.parse(videoUri).toString();
        try {
            Uri uri=Uri.parse(muteVideofilepath);
            // String path = uri.getPath() ;// "/mnt/sdcard/FileName.mp3"
            srcFile = new File(String.valueOf(uri));
            // file = new File(new URI(videoUri));
        } catch (Exception e) {
            e.printStackTrace();
        }


        File dest = new File(moviesDir, filePrefix + fileExtn);
        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
        }


        //  Log.d("MergeVideoAudioActivity", "startTrim: src: " + file.getAbsolutePath());
        // Log.d("MergeVideoAudioActivity", "startTrim: dest: " + dest.getAbsolutePath());
        DesfilePathVideoSpeed = dest.getAbsolutePath();


        *//*if(videoSpeed.equalsIgnoreCase("fast"))
        {
            String[] complexCommand = {"-y", "-i", srcFile.getAbsolutePath(), "-filter_complex", "[0:v]setpts=0.5*PTS[v];[0:a]atempo=2.0[a]", "-map", "[v]", "-map", "[a]", "-b:v", "2097k", "-r", "60", "-vcodec", "mpeg4", DesfilePathVideoSpeed};
            execFFmpegBinary(complexCommand,DesfilePathVideoSpeed);
        }else if(videoSpeed.equalsIgnoreCase("Slow")) {
            String[] complexCommand = {"-y", "-i", srcFile.getAbsolutePath(), "-filter_complex", "[0:v]setpts=2.0*PTS[v];[0:a]atempo=0.5[a]", "-map", "[v]", "-map", "[a]", "-b:v", "2097k", "-r", "60", "-vcodec", "mpeg4", DesfilePathVideoSpeed};

        }*//*

        Uri uri=Uri.parse(musicPath);
        File MusicFile = new File(String.valueOf(uri));

        //working but takes to much time for procssing
        // String[] complexcommand={ "-i",srcFile.getAbsolutePath(), "-i" ,MusicFile.getAbsolutePath() ,"-t" , String.valueOf(secondsVideo), DesfilePathVideoSpeed};
        //faster
        String[] complexcommand={ "-i" ,MusicFile.getAbsolutePath(), "-i" ,srcFile.getAbsolutePath(), "-y", "-acodec", "copy", "-vcodec" ,"copy", DesfilePathVideoSpeed };

        execFFmpegMergeAuioVideo(complexcommand,DesfilePathVideoSpeed);

    }*/

  /*  private void execFFmpegMergeAuioVideo(final String[] command,String DesfilePath) {
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

                    startActivity(new Intent(UploadVideoActivity.this,EditVideoActivity.class).putExtra("video_uri",DesfilePathVideoSpeed));

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
                        startActivity(new Intent(UploadVideoActivity.this,EditVideoActivity.class).putExtra("video_uri",DesfilePathVideoSpeed));
                    }

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }*/

    private boolean checkPermission() {
        //int result13 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);

        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);


        return result == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED ;

    }

    private void requestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            ActivityCompat.requestPermissions(UploadVideoActivity.this, new String[]{

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

                    for (int i = 0; i < grantResults.length; i++) {
                        if (!(grantResults[i] == PackageManager.PERMISSION_GRANTED)) {
                            flag = false;
                        }
                    }
                    if(!flag)
                    {
                        openSettingsDialog();

                    }else {
                       // Log.d("SetUpUi","onRequestPermissionsResult");
                    }
                }

                break;
        }
    }
    private void openSettingsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(UploadVideoActivity.this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadVideoActivity.this);
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

    private void executeSpeedVideoCommand() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(null);
        progressDialog.setCancelable(false);

        Uri uri=Uri.parse(mNextVideoAbsolutePath);
        File file = new File(String.valueOf(uri));
        // String yourRealPath = getPath(MergeVideoAudioActivity.this, uri);


       // Uri urimusicPath=Uri.parse(musicPath);
       // File fileurimusicPath = new File(String.valueOf(urimusicPath));

        Log.d(TAG, "startTrim: src: " + file);

        SpeedVideofilePath = getVideoWithSubtitlesFile().getAbsolutePath();

        if(mVideoSpeed.equalsIgnoreCase("fast"))
        {
            String[] complexCommand = {"-y", "-i", file.getAbsolutePath(), "-filter_complex", "[0:v]setpts=0.5*PTS[v];[0:a]atempo=2.0[a]", "-map", "[v]", "-map", "[a]", "-b:v", "2097k", "-r", "60", "-vcodec", "mpeg4", SpeedVideofilePath};
            execFFmpegVideoSpeed(complexCommand);
        }else if(mVideoSpeed.equalsIgnoreCase("Slow")) {
            String[] complexCommand = {"-y", "-i", file.getAbsolutePath(), "-filter_complex", "[0:v]setpts=2.0*PTS[v];[0:a]atempo=0.5[a]", "-map", "[v]", "-map", "[a]", "-b:v", "2097k", "-r", "60", "-vcodec", "mpeg4", SpeedVideofilePath};
            execFFmpegVideoSpeed(complexCommand);
        }

      //  String[] complexcommand = new String[]{"-i",file.getAbsolutePath(),"-i",fileurimusicPath.getAbsolutePath(),"-map","1:a","-map","0:v","-codec","copy","-shortest",SpeedVideofilePath};


    }

    private void execFFmpegVideoSpeed(final String[] command) {

        Config.enableLogCallback(new LogCallback() {
            @Override
            public void apply(LogMessage message) {
                Log.e(Config.TAG, message.getText());
            }
        });
        Config.enableStatisticsCallback(new StatisticsCallback() {
            @Override
            public void apply(Statistics newStatistics) {
                Log.e(Config.TAG, String.format("frame: %d, time: %d", newStatistics.getVideoFrameNumber(), newStatistics.getTime()));
                Log.d(TAG, "Started command : ffmpeg " + Arrays.toString(command));
               /* if (choice == 8)
                    progressDialog.setMessage("progress : splitting video " + newStatistics.toString());
                else if (choice == 9)
                    progressDialog.setMessage("progress : reversing splitted videos " + newStatistics.toString());
                else if (choice == 10)
                    progressDialog.setMessage("progress : concatenating reversed videos " + newStatistics.toString());
                else
                    progressDialog.setMessage("progress : " + newStatistics.toString());
                Log.d(TAG, "progress : " + newStatistics.toString());*/
            }
        });
        Log.d(TAG, "Started command : ffmpeg " + Arrays.toString(command));
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        long executionId = com.arthenica.mobileffmpeg.FFmpeg.executeAsync(command, (executionId1, returnCode) -> {
            if (returnCode == Config.RETURN_CODE_SUCCESS) {
                Log.d(TAG, "Finished command : ffmpeg " + Arrays.toString(command));
                if (progressDialog != null)
                    progressDialog.dismiss();
                startActivity(new Intent(UploadVideoActivity.this,KEditVideoActivity.class).putExtra("video_uri",SpeedVideofilePath));
            } else if (returnCode == Config.RETURN_CODE_CANCEL) {
                Log.e(TAG, "Async command execution cancelled by user.");
                if (progressDialog != null)
                    progressDialog.dismiss();
            } else {
                Log.e(TAG, String.format("Async command execution failed with returnCode=%d.", returnCode));
                if (progressDialog != null)
                    progressDialog.dismiss();
            }
        });
        Log.e(TAG, "execFFmpegMergeVideo executionId-" + executionId);
    }

    public File getVideoWithSubtitlesFile() {
        return new File(this.getFilesDir(), "video-with-speed.mp4");
    }

}
