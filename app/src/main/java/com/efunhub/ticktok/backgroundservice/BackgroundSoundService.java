package com.efunhub.ticktok.backgroundservice;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.efunhub.ticktok.R;

import java.io.IOException;

public class BackgroundSoundService extends Service {
    MediaPlayer mediaPlayer;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        String MusicPath = intent.getStringExtra("MusicPath");

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(Uri.parse(MusicPath).toString());
            mediaPlayer.setLooping(true); // Set looping
            mediaPlayer.setVolume(80, 80);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.start();


        return startId;
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
    }
    @Override
    public void onLowMemory() {
    }
}
