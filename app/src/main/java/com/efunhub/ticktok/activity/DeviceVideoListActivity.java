package com.efunhub.ticktok.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.efunhub.ticktok.R;
import com.efunhub.ticktok.adapter.DeviceVideoListAdapter;
import com.efunhub.ticktok.model.DeviceVideoModel;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class DeviceVideoListActivity extends AppCompatActivity implements View.OnClickListener,DeviceVideoListAdapter.SelectVideoInterface {

    public ArrayList<DeviceVideoModel> videoArrayList;
    RecyclerView rcDeviceVideolist;
    Button btnNext;
    private String selectedVideoUri="";
    PlayerView videoFullScreenPlayer;
    SimpleExoPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_video_list);
        init();
    }

    private void init() {
        videoArrayList=new ArrayList<>();
        rcDeviceVideolist=findViewById(R.id.rcDeviceVideolist);
        btnNext=findViewById(R.id.btnNext);
        btnNext.setOnClickListener(this);
        setRecyclerView();
    }

    private void setRecyclerView() {
       GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4, RecyclerView.VERTICAL, false);
        rcDeviceVideolist.setLayoutManager(gridLayoutManager);
      //  rcDeviceVideolist.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
       // rcDeviceVideolist.setItemAnimator(new DefaultItemAnimator());

        getVideos();
    }

    public void getVideos() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        //looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {

                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                String data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                DeviceVideoModel videoModel  = new DeviceVideoModel ();
                videoModel .setVideoTitle(title);
                videoModel .setVideoUri(Uri.parse(data));
                Log.d("videoURL",Uri.parse(data).toString());
                try{
                    videoModel .setVideoDuration(timeConversion(Long.parseLong(duration)));
                }catch (Exception e){
                    e.printStackTrace();
                }
                videoArrayList.add(videoModel);

            } while (cursor.moveToNext());
        }

        DeviceVideoListAdapter adapter = new DeviceVideoListAdapter(this, videoArrayList);
        rcDeviceVideolist.setAdapter(adapter);

    }
    public String timeConversion(long value) {
        long millis = value;
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
       Log.d("duration", String.valueOf(millis));
        return hms;
    }

    @Override
    public void selectVideo(String videoUri) {
        selectedVideoUri=videoUri;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnNext:
                if(selectedVideoUri.isEmpty()){
                    Toast.makeText(this, "Please select video", Toast.LENGTH_SHORT).show();
                }else {
                    startActivity(new Intent(DeviceVideoListActivity.this,KEditVideoActivity.class).putExtra("video_uri",selectedVideoUri));
                }
                break;
        }
    }
}