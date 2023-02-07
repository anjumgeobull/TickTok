package com.efunhub.ticktok.adapter;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.efunhub.ticktok.BuildConfig;
import com.efunhub.ticktok.R;
import com.efunhub.ticktok.activity.LoginActivity;
import com.efunhub.ticktok.activity.MainActivity;
import com.efunhub.ticktok.activity.MyProfile_Activity;
import com.efunhub.ticktok.activity.ProfileActivity;
import com.efunhub.ticktok.application.SessionManager;
import com.efunhub.ticktok.interfaces.ShowCommentListener;
import com.efunhub.ticktok.model.AllVideoModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder>  {

    ArrayList<AllVideoModel.Data> allvideoList;
    Activity activity;
    private LayoutInflater mInflater;
    BottomSheetDialog bottomSheetDialog;
    RecyclerView rvComments;
    CommentsAdapter commentsAdapter;
    boolean flag;
    ArrayList<String> videoList = new ArrayList<>();

    ShowCommentListener showCommentListener;

    public VideoAdapter(Activity context, ArrayList<AllVideoModel.Data> allvideoList,ShowCommentListener showCommentListener) {
        this.activity = context;
        this.mInflater = LayoutInflater.from(context);
        this.allvideoList = allvideoList;
        this.showCommentListener=showCommentListener;

    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.videoView.pause();

    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.videoView.start();
    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.new_video_list_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoAdapter.ViewHolder holder, int position) {

        holder.tvWishlistCount.setText(""+allvideoList.get(position).getTotal_likes());
       // holder.videoView.setVideoPath(VIDEO_URL+(allvideoList.get(position).getVideo()));
       // holder.videoView.start();

        String path="";

        if(position==0){
            path = "android.resource://" + activity.getPackageName() + "/" + R.raw.video_1;
            holder.videoView.setVideoURI(Uri.parse(path));
            holder.videoView.start();
        }
        else if(position==1){
            path = "android.resource://" + activity.getPackageName() + "/" + R.raw.video_2;
            holder.videoView.setVideoURI(Uri.parse(path));
            holder.videoView.start();
        }
//        else if(position==3){
//            path = "android.resource://" + activity.getPackageName() + "/" + R.raw.video_3;
//            holder.videoView.setVideoURI(Uri.parse(path));
//            holder.videoView.start();
//        }
        else if(position==4){
            path = "android.resource://" + activity.getPackageName() + "/" + R.raw.video_4;
            holder.videoView.setVideoURI(Uri.parse(path));
            holder.videoView.start();
        }
        else if(position==6){
            path = "android.resource://" + activity.getPackageName() + "/" + R.raw.video_5;
            holder.videoView.setVideoURI(Uri.parse(path));
            holder.videoView.start();
        }
        else if(position==7){
            path = "android.resource://" + activity.getPackageName() + "/" + R.raw.video_6;
            holder.videoView.setVideoURI(Uri.parse(path));
            holder.videoView.start();
        }

        else{
            path = "android.resource://" + activity.getPackageName() + "/" + R.raw.video_7;
            holder.videoView.setVideoURI(Uri.parse(path));
            holder.videoView.start();
        }

      /*  if (!flag)
        {
            holder.videoView.pause();
        }
        else {

        }*/

        RotateAnimation rotateAnimation = new RotateAnimation(0, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  holder.videoView.canPause();
              //  holder.videoView.pause();
                if(holder.llTopMenu.getVisibility()==View.VISIBLE){
                    holder.llTopMenu.setVisibility(View.GONE);
                    holder.llSideMenu.setVisibility(View.GONE);
                    holder.lldetailsMenu.setVisibility(View.GONE);
                    //holder.llFirstMenu.setVisibility(View.VISIBLE);
                }
                else {
                    holder.llTopMenu.setVisibility(View.VISIBLE);
                    holder.llSideMenu.setVisibility(View.VISIBLE);
                    holder.lldetailsMenu.setVisibility(View.VISIBLE);
                   // holder.llFirstMenu.setVisibility(View.GONE);
                }

            }
        });
        holder.img_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SessionManager.onGetAutoCustomerId().isEmpty()) {
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                } else {
                    activity.startActivity(new Intent(activity, MyProfile_Activity.class));
                }
            }
        });

        holder.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, ProfileActivity.class));
            }
        });

        holder.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                sendIntent.setType("text/plain");
                activity.startActivity(sendIntent);            }
        });

        holder.imgComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommentListener.showComments(allvideoList.get(position).get_id(), position);
            }
        });

        holder.imgWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // holder.imgWishlisted.setVisibility(View.VISIBLE);
                //holder.imgWishlist.setVisibility(View.GONE);
                holder.imgWishlist.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_heart_color));
            }
        });
        holder.img_myHome.setOnClickListener(v -> {
//            if (SessionManager.onGetAutoCustomerId().isEmpty()) {
//                activity.startActivity(new Intent(activity, LoginActivity.class));
//            } else {
//                activity.startActivity(new Intent(activity, MyProfile_Activity.class));
//            }
            activity.startActivity(new Intent(activity, MainActivity.class));
        });
    }

    @Override
    public int getItemCount() {
        return allvideoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;
        ImageView imgShare,imgDownload, imgWishlisted, imgWishlist, imgMusic, imgProfile, imgComment,img_Profile;
      //  HorizontalScrollView scroll;
        TextView tvWishlistCount,tvShare;
        LinearLayout llTopMenu,llSideMenu,lldetailsMenu,img_myHome;
       // LinearLayout llFirstMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            imgWishlist = itemView.findViewById(R.id.imgWishlist);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            imgComment = itemView.findViewById(R.id.imgComment);
            imgShare = itemView.findViewById(R.id.imgShare);
            imgDownload = itemView.findViewById(R.id.imgDownload);
            tvWishlistCount=itemView.findViewById(R.id.tvWishlistCount);
            llTopMenu=itemView.findViewById(R.id.llTopMenu);
            llSideMenu=itemView.findViewById(R.id.llSideMenu);
         //   llFirstMenu=itemView.findViewById(R.id.llFirstMenu);
            lldetailsMenu=itemView.findViewById(R.id.llDetailsMenu);
            img_myHome = itemView.findViewById(R.id.img_myHome);
            img_Profile = itemView.findViewById(R.id.profile_image_icon);
            //tvShare=itemView.findViewById(R.id.tvShare);


            videoView.setOnCompletionListener ( new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    videoView.start();
                }
            });
        }
    }
}
