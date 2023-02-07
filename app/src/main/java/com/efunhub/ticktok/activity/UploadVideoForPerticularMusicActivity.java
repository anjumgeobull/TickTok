package com.efunhub.ticktok.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.efunhub.ticktok.R;
import com.efunhub.ticktok.adapter.MusicAdapter;
import com.efunhub.ticktok.adapter.PostAdapterNew;

import java.util.ArrayList;

public class UploadVideoForPerticularMusicActivity extends AppCompatActivity {

    RecyclerView rvMusicVideo;
    PostAdapterNew videoAdapter;
    ArrayList<Integer> videoList = new ArrayList<>();
    RelativeLayout relativeUploadVideo;
    Toolbar toolbar;
    Button btn_browse,btn_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video_for_perticular_music);
        setUpToolbar();
        init();
        relativeUploadVideo = findViewById(R.id.relativeUploadVideo);

        relativeUploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // startActivity(new Intent(UploadVideoForPerticularMusicActivity.this, UploadVideoActivity.class));
            }
        });

    }
    private void init() {
        btn_browse=findViewById(R.id.btn_browse);
        btn_search=findViewById(R.id.btn_search);

        btn_browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UploadVideoForPerticularMusicActivity.this, MusicActivity.class));
            }
        });

        //set recycler view
        setUpRecycler();

    }
    private void setUpToolbar() {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Music");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void setUpRecycler() {
        rvMusicVideo = findViewById(R.id.rcMusic);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(UploadVideoForPerticularMusicActivity.this, 1, RecyclerView.VERTICAL, false);
        rvMusicVideo.setLayoutManager(gridLayoutManager);
//        videoAdapter = new MusicAdapter(UploadVideoForPerticularMusicActivity.this, getAllAudioFromDevice(this));
//        rvMusicVideo.setAdapter(videoAdapter);
    }


}
