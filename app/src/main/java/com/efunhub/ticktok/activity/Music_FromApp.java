package com.efunhub.ticktok.activity;

import static com.efunhub.ticktok.retrofit.Constant.SERVER_URL;
import static com.efunhub.ticktok.utility.ConstantVariables.DELETE_VIDEO;
import static com.efunhub.ticktok.utility.ConstantVariables.FOLLOW_USER;
import static com.efunhub.ticktok.utility.ConstantVariables.GET_AUDIO;
import static com.efunhub.ticktok.utility.ConstantVariables.USER_PROFILE;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.LogCallback;
import com.arthenica.mobileffmpeg.LogMessage;
import com.arthenica.mobileffmpeg.Statistics;
import com.arthenica.mobileffmpeg.StatisticsCallback;
import com.daasuu.gpuv.composer.FillMode;
import com.daasuu.gpuv.composer.GPUMp4Composer;
import com.daasuu.gpuv.composer.Rotation;
import com.efunhub.ticktok.R;
import com.efunhub.ticktok.adapter.MusicAdapter;
import com.efunhub.ticktok.adapter.Music_From_AppAdapter;
import com.efunhub.ticktok.backgroundservice.BackgroundSoundService;
import com.efunhub.ticktok.interfaces.IResult;
import com.efunhub.ticktok.model.AudioFromAppModel;
import com.efunhub.ticktok.model.User_Profile_Model.Post;
import com.efunhub.ticktok.model.User_Profile_Model.UserProfile;
import com.efunhub.ticktok.utility.VolleyService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Music_FromApp extends AppCompatActivity implements Music_From_AppAdapter.ApplyMusicInterface{
    private String TAG="Music_Activity";
    RecyclerView rcMusic;
    ImageView imgClose;
    String muxAudioVideoFlag;
    private ProgressDialog progressDialog;
    private String muteVideofilepath;
    private String mMusicPath;
    private com.daasuu.gpuv.composer.GPUMp4Composer GPUMp4Composer;
    private String mergeAudioVideoPath;
    private String video_uri;
    String Get_audios = "audios";
    //ArrayList<AudioFromAppModel> audio_Model = new ArrayList<>();
    ArrayList<AudioFromAppModel.AudioList> audioList = new ArrayList<>();
    private VolleyService mVolleyService;
    private IResult mResultCallBack = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setContentView(R.layout.activity_music_from_app);
        init();
        getaudios();

    }

    private void init() {
        rcMusic=findViewById(R.id.rcMusic_inapp);
        imgClose=findViewById(R.id.imgClose);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stop service
                stopService();
                finish();
            }
        });

        if (getIntent().hasExtra("MuxAudioVideo")) {
            muxAudioVideoFlag = getIntent().getStringExtra("MuxAudioVideo");
            video_uri=getIntent().getStringExtra("video_uri");
        }

    }
    private void getaudios() {
        audioList.clear();
        progressDialog=new ProgressDialog(this);
        progressDialog.show();
        //shimmer_view_container.startShimmerAnimation();
        initVolleyCallback();
        mVolleyService = new VolleyService(mResultCallBack, this);

        mVolleyService.getDataVolley(GET_AUDIO, SERVER_URL + Get_audios);

    }

    private void initVolleyCallback() {
        mResultCallBack = new IResult() {

            @Override
            public void notifySuccess(int requestId, String response) {
                JSONObject jsonObject = null;
                switch (requestId) {
                    case GET_AUDIO:
                        try {
                            jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            String status = jsonObject.getString("status");

                            if (Integer.parseInt(status) == 0) {
                                progressDialog.dismiss();
                                Toast.makeText(Music_FromApp.this, msg, Toast.LENGTH_SHORT).show();
                            } else if (Integer.parseInt(status) == 1) {
                                progressDialog.dismiss();
                                String current_data = jsonObject.getString("data");
                                GsonBuilder gsonBuilder = new GsonBuilder();
                                Gson gson = gsonBuilder.create();
                                AudioFromAppModel.AudioList[] audiomodel = gson.fromJson(current_data, AudioFromAppModel.AudioList[].class);
                                audioList = new ArrayList<>(Arrays.asList(audiomodel));
//                                audioList=audio_Model.get(0).getData();
                                if (!audioList.isEmpty()) {
                                    setRecllerView();
                                }
                                else {

                                }
                            }
                        } catch (JSONException e1) {

                            e1.printStackTrace();
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                        }
                        break;

                }
            }

            @Override
            public void notifyError(int requestId, VolleyError error) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Music_FromApp.this);
                //  builder.setIcon(R.drawable.icon_exit);
                builder.setMessage("Server Error")
                        .setCancelable(true)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
                Log.v("Volley requestid ", String.valueOf(requestId));
                Log.v("Volley Error", String.valueOf(error));
            }
        };
    }


    private void setRecllerView() {
        LinearLayoutManager layoutManager= new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcMusic.setLayoutManager(layoutManager);
        Music_From_AppAdapter musicAdapter = new Music_From_AppAdapter(Music_FromApp.this,  audioList);
        rcMusic.setAdapter(musicAdapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //stop service
        stopService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //stop service
        stopService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stop service
        stopService();
    }

    private void stopService(){
        if( isMyServiceRunning(BackgroundSoundService.class))
        {
            //stop previous running service
            Intent myService = new Intent(Music_FromApp.this, BackgroundSoundService.class);
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

   // @Override
    public void selectAndApplyMusic(String musicPath) {
        mMusicPath=musicPath;
        if(muxAudioVideoFlag.equalsIgnoreCase("Yes")) {
            startCodec(video_uri);
        }else {
            Intent intent=new Intent();
            intent.putExtra("musicPath",musicPath);
            setResult(2,intent);
            finish();//finishing activity
        }
    }

    private void startCodec(String sourceVideoPath) {
        muteVideofilepath= getVideoFilePath();

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

                            /*progressDialog = new ProgressDialog(MusicActivity.this);
                            progressDialog.setTitle("Please Wait...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();*/

                            calculateDuration(muteVideofilepath);


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

    public  String getVideoFilePath() {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("videoDir", Context.MODE_PRIVATE);
        File file = new File(directory,System.currentTimeMillis() + ".mp4");
        Uri imgUri = Uri.fromFile(file);
        String fileAbsolutePath = file.getAbsolutePath();
        return fileAbsolutePath;
    }

    private void MergeAudioVideo(String whichFileDurationGreater) {
        Uri uri=Uri.parse(muteVideofilepath);
        File Videofile = new File(String.valueOf(uri));


        Uri MusicUri=Uri.parse(mMusicPath);
        File fileurimusicPath = new File(String.valueOf(MusicUri));


        mergeAudioVideoPath = getMergeFilePath().getAbsolutePath();


        if(whichFileDurationGreater.equalsIgnoreCase("AudioLengthGreater"))
        {
            //if audio length is max than video .final output is same as video (Woking as per requirenent)
            String[] complexcommand = new String[]{"-i", Videofile.getAbsolutePath(),"-i", String.valueOf(fileurimusicPath),"-map","1:a","-map","0:v","-codec","copy","-shortest",mergeAudioVideoPath};
            execFFmpegMergeAuioVideo(complexcommand);
        }else if(whichFileDurationGreater.equalsIgnoreCase("VideoLengthGreater")){
            String[] complexcommand={ "-i" , String.valueOf(fileurimusicPath), "-i" ,Videofile.getAbsolutePath(), "-y", "-acodec", "copy", "-vcodec" ,"copy", mergeAudioVideoPath };
            execFFmpegMergeAuioVideo(complexcommand);
        }else {
            String[] complexcommand={ "-i" , String.valueOf(fileurimusicPath), "-i" ,Videofile.getAbsolutePath(), "-y", "-acodec", "copy", "-vcodec" ,"copy", mergeAudioVideoPath };
            execFFmpegMergeAuioVideo(complexcommand);
        }

    }

    public File getMergeFilePath() {
        return new File(this.getFilesDir(), "merge-audio-video_1.mp4");
    }

    public void calculateDuration(String finalVideoPath){

        long VideoDuration;
        long audioDuration;

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(null);
        progressDialog.setCancelable(false);

        try{
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(finalVideoPath);
            long durationVideo = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            VideoDuration = TimeUnit.MILLISECONDS.toSeconds(durationVideo);
            Log.d("VideoDuration", String.valueOf(VideoDuration));

            //  retriever.release();

            // MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(mMusicPath);
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
            //call merge audio and video
            MergeAudioVideo(whichDurationGreater);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void execFFmpegMergeAuioVideo(final String[] command) {

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

                Intent intent=new Intent();
                intent.putExtra("musicPath",mMusicPath);
                intent.putExtra("mergeAudioVideoPath",mergeAudioVideoPath);
                setResult(2,intent);
                finish();//finishing activity

                // startActivity(new Intent(MusicActivity.this,EditVideoActivity.class).putExtra("video_uri",mergeAudioVideoPath));
                    /*progressDialog = new ProgressDialog(MergeVideoAudioActivity.this);
                            progressDialog.setTitle(null);
                            progressDialog.setCancelable(false);

                            progressDialog.show();

                            result= mux(muteVideofilepath,trimeAudioPath);
                            Log.d("result",""+result);
                            if(result)
                            {
                                progressDialog.dismiss();
                                startActivity(new Intent(MergeVideoAudioActivity.this,EditVideoActivity.class).putExtra("video_uri",filePathMergeVideoAudio));

                            }*/
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
}