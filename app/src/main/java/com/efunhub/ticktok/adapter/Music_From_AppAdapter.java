package com.efunhub.ticktok.adapter;

import static ly.img.android.pesdk.backend.decoder.ImageSource.getResources;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.efunhub.ticktok.R;
import com.efunhub.ticktok.backgroundservice.BackgroundSoundService;
import com.efunhub.ticktok.model.AudioFromAppModel;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Music_From_AppAdapter extends RecyclerView.Adapter<Music_From_AppAdapter.ViewHolder> {

    ArrayList<AudioFromAppModel.AudioList> musicList;
    Activity activity;
    private LayoutInflater mInflater;
    // public static String AudioPath;
    ApplyMusicInterface applyMusicInterface;
    String music;
    int music_position;
    String music_path="https://grobiz.app/tiktokadmin/images/audios/";


    public Music_From_AppAdapter(Activity context, ArrayList<AudioFromAppModel.AudioList> musicList) {
        this.activity = context;
        this.mInflater = LayoutInflater.from(context);
        this.musicList = musicList;
        applyMusicInterface= (ApplyMusicInterface) context;
    }

    @NonNull
    @Override
    public Music_From_AppAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.music_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Music_From_AppAdapter.ViewHolder holder, final int position) {

        holder.tvVideoName.setText(musicList.get(position).getAudiofile());
        Log.d("AudioPath",musicList.get(position).getAudiofile());

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(music_path+musicList.get(position).getAudiofile());
        long durationMusic = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        long Music = TimeUnit.MILLISECONDS.toSeconds(durationMusic);
        Log.d("secondsMusic", String.valueOf(Music) +musicList.get(position).getAudiofile());

        retriever.release();


        holder.rlMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( isMyServiceRunning(BackgroundSoundService.class))
                {
                    // AudioPath= musicList.get(position).getaPath();
                    Log.d("AudioPath",musicList.get(position).getAudiofile());
                    //stop previous running service
                    if(music.equals(musicList.get(position).getAudiofile()))
                    {
                        if(isMyServiceRunning(BackgroundSoundService.class))
                        {
                            holder.img_pause.setVisibility(View.GONE);
                            holder.img_play.setVisibility(View.VISIBLE);
                            holder.music_layout.setBackgroundColor(getResources().getColor(R.color.white));
                            Intent myService = new Intent(activity, BackgroundSoundService.class);
                            activity.stopService(myService);
                        }else {
                            Intent myService = new Intent(activity, BackgroundSoundService.class);
                            activity.stopService(myService);
                            holder.img_pause.setVisibility(View.VISIBLE);
                            holder.img_play.setVisibility(View.GONE);
                            holder.music_layout.setBackgroundColor(getResources().getColor(R.color.lightprimary));
                        }

                    }else {
//                        if(music_position!=position && music_position!=-1){
//                            //updated the elements view to default view. Like made the visibility and other changes here.
//                        }
                        holder.img_pause.setVisibility(View.GONE);
                        holder.img_play.setVisibility(View.VISIBLE);
                        holder.music_layout.setBackgroundColor(getResources().getColor(R.color.white));
                        Intent myService = new Intent(activity, BackgroundSoundService.class);
                        activity.stopService(myService);
                        holder.img_pause.setVisibility(View.VISIBLE);
                        holder.img_play.setVisibility(View.GONE);
                        holder.music_layout.setBackgroundColor(getResources().getColor(R.color.lightprimary));
                        //start new service
                        Intent serviceIntent = new Intent(activity, BackgroundSoundService.class);
                        serviceIntent.putExtra("MusicPath", musicList.get(position).getAudiofile());
                        music=musicList.get(position).getAudiofile();
                        music_position=position;
                        activity.startService(serviceIntent);
                    }

                }else {
                    // AudioPath= musicList.get(position).getaPath();
                    Log.d("AudioPath",musicList.get(position).getAudiofile());
                    holder.img_pause.setVisibility(View.VISIBLE);
                    holder.img_play.setVisibility(View.GONE);
                    holder.music_layout.setBackgroundColor(getResources().getColor(R.color.lightprimary));
                    Intent serviceIntent = new Intent(activity, BackgroundSoundService.class);
                    serviceIntent.putExtra("MusicPath", music_path+musicList.get(position).getAudiofile());
                    music=musicList.get(position).getAudiofile();
                    music_position=position;
                    activity.startService(serviceIntent);
                }
            }
        });

        holder.btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  ((Activity)activity).finish();
                applyMusicInterface.selectAndApplyMusic(musicList.get(position).getAudiofile());
            }
        });


    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVideoName;
        ImageView img_pause,img_play;
        //ImageView imgMusic;
        RelativeLayout rlMusic;
        Button btnApply;
        LinearLayout music_layout;


        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            tvVideoName = itemView.findViewById(R.id.tvVideoName);
            rlMusic=itemView.findViewById(R.id.rlMusic);
            btnApply=itemView.findViewById(R.id.btnApply);
            img_pause=itemView.findViewById(R.id.img_pause);
            img_play=itemView.findViewById(R.id.img_play);
            music_layout=itemView.findViewById(R.id.music_layout);
        }
    }

    public interface ApplyMusicInterface{
        public void selectAndApplyMusic(String musicPath);
    }
}

