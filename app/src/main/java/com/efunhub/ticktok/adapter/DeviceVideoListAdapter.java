package com.efunhub.ticktok.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.efunhub.ticktok.BuildConfig;
import com.efunhub.ticktok.R;
import com.efunhub.ticktok.activity.EditVideoActivity;
import com.efunhub.ticktok.activity.PlayEditedVideoActivity;
import com.efunhub.ticktok.activity.ProfileActivity;
import com.efunhub.ticktok.activity.UploadVideoForPerticularMusicActivity;
import com.efunhub.ticktok.model.DeviceVideoModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class DeviceVideoListAdapter extends RecyclerView.Adapter<DeviceVideoListAdapter.ViewHolder> {

    ArrayList<DeviceVideoModel> videoList = new ArrayList<>();
    Activity activity;
    private LayoutInflater mInflater;
    SelectVideoInterface selectVideoInterface;


    public DeviceVideoListAdapter(Activity context, ArrayList<DeviceVideoModel> videoList) {
        this.activity = context;
        this.mInflater = LayoutInflater.from(context);
        this.videoList = videoList;
        this.selectVideoInterface= (SelectVideoInterface) activity;
    }

    @NonNull
    @Override
    public DeviceVideoListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.device_video_list_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DeviceVideoListAdapter.ViewHolder holder, int position) {

        holder.tvDuration.setText(videoList.get(position).getVideoDuration());


       /* Glide
                .with(context)
                .asBitmap()
                .load(Uri.fromFile(new File(filePath)))
                .into(imageViewGifAsBitmap);*/

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.isMemoryCacheable();
        Glide.with(activity).setDefaultRequestOptions(requestOptions).load(videoList.get(position).getVideoUri().toString()).into(holder.videoThumbnail);


        holder.videoThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, PlayEditedVideoActivity.class).putExtra("video_uri",videoList.get(position).getVideoUri().toString()));
            }
        });

       holder.radiobtnSelectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<videoList.size();i++)
                {
                    if(i==position)
                    {
                        selectVideoInterface.selectVideo(videoList.get(position).getVideoUri().toString());
                        videoList.get(i).setSelectionFlag("1");
                    }else {
                        videoList.get(i).setSelectionFlag("0");
                    }
                }
                notifyDataSetChanged();
            }
        });

        if(videoList.get(position).getSelectionFlag().equalsIgnoreCase("1"))
        {
            holder.radiobtnSelectVideo.setChecked(true);
        }else {
            holder.radiobtnSelectVideo.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView videoThumbnail;
        TextView tvDuration;
        RadioButton radiobtnSelectVideo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.videoThumbnail);
            tvDuration=itemView.findViewById(R.id.tvDuration);
            radiobtnSelectVideo=itemView.findViewById(R.id.radiobtnSelectVideo);
        }

    }

    public interface SelectVideoInterface{
        public void selectVideo(String videoUri);
    }

}
