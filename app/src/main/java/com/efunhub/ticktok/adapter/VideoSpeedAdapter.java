package com.efunhub.ticktok.adapter;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.efunhub.ticktok.R;
import com.efunhub.ticktok.backgroundservice.BackgroundSoundService;
import com.efunhub.ticktok.model.AudioModel;
import com.efunhub.ticktok.model.VideoSpeedModel;

import java.util.ArrayList;

public class VideoSpeedAdapter extends RecyclerView.Adapter<VideoSpeedAdapter.ViewHolder> {

    ArrayList<VideoSpeedModel> videoSpeedArray;
    Activity activity;
    private LayoutInflater mInflater;
   // public static String AudioPath;
   VideoSpeedInterface videoSpeedInterface;

    public VideoSpeedAdapter(Activity context, ArrayList<VideoSpeedModel> videoSpeedArray) {
        this.activity = context;
        this.mInflater = LayoutInflater.from(context);
        this.videoSpeedArray = videoSpeedArray;
        videoSpeedInterface= (VideoSpeedInterface) context;
    }

    @NonNull
    @Override
    public VideoSpeedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.video_speed_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoSpeedAdapter.ViewHolder holder, final int position) {

        holder.btnSpeed.setText(videoSpeedArray.get(position).getVideoSpeed());

        holder.btnSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<videoSpeedArray.size();i++)
                {
                    if(i==position)
                    {
                        videoSpeedArray.get(i).setSelectionFlag("1");
                        videoSpeedInterface.selectVideoSpeed(videoSpeedArray.get(position).getVideoSpeed());
                    }else {
                        videoSpeedArray.get(i).setSelectionFlag("0");
                    }
                }
                notifyDataSetChanged();
            }
        });

        if(videoSpeedArray.get(position).getSelectionFlag().equals("1")){
           // holder.btnSpeed.setBackground(activity.getResources(R.drawable.rounded_corners));
            holder.btnSpeed.setBackgroundResource(R.drawable.rounded_corners);
        }else {
            holder.btnSpeed.setBackgroundResource(0);
        }
    }


    @Override
    public int getItemCount() {
        return videoSpeedArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
         Button btnSpeed;



        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            btnSpeed = itemView.findViewById(R.id.btnSpeed);

        }
    }

    public interface VideoSpeedInterface{
        public void selectVideoSpeed(String speed);
    }
}

