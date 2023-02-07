package com.efunhub.ticktok.adapter;

import static com.efunhub.ticktok.retrofit.Constant.VIDEO_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.efunhub.ticktok.BuildConfig;
import com.efunhub.ticktok.R;
import com.efunhub.ticktok.activity.LoginActivity;
import com.efunhub.ticktok.activity.MainActivity;
import com.efunhub.ticktok.activity.MyProfile_Activity;
import com.efunhub.ticktok.activity.ProfileActivity;
import com.efunhub.ticktok.activity.Search_activity;
import com.efunhub.ticktok.activity.UploadVideoActivity;
import com.efunhub.ticktok.activity.UploadVideoForPerticularMusicActivity;
import com.efunhub.ticktok.application.SessionManager;
import com.efunhub.ticktok.fragments.NotificationsFragment;
import com.efunhub.ticktok.interfaces.Like_video_interface;
import com.efunhub.ticktok.interfaces.ShowCommentListener;
import com.efunhub.ticktok.model.AllVideoModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

public class VideoAdapterNew extends RecyclerView.Adapter<VideoAdapterNew.ViewHolder> {

    ArrayList<AllVideoModel.Data> allvideoList;
    Activity activity;
    Fragment fragment;
    private LayoutInflater mInflater;
    BottomSheetDialog bottomSheetDialog;
    RecyclerView rvComments;
    CommentsAdapter commentsAdapter;
    ShowCommentListener showCommentListener;
    Like_video_interface like_video_interface;

    ArrayList<String> videoList = new ArrayList<>();
    String type;
    private final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds

    long lastClickTime = 0;


    public VideoAdapterNew(Activity context, ArrayList<AllVideoModel.Data> allvideoList, ShowCommentListener showCommentListener, Like_video_interface like_video_interface, String type) {
        this.activity = context;
        this.mInflater = LayoutInflater.from(context);
        this.allvideoList = allvideoList;
        this.showCommentListener = showCommentListener;
        this.like_video_interface = like_video_interface;
        this.type = type;

    }

    @NonNull
    @Override
    public VideoAdapterNew.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.new_video_list_adapter, parent, false);
        return new ViewHolder(view);
    }

    /*@Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.videoView.pause();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.videoView.start();
    }*/

    @Override
    public void onBindViewHolder(@NonNull final VideoAdapterNew.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        // holder.tvShare.setText(""+allvideoList.get(position).getTotal_shares());
      /*  holder.videoView.setVideoPath(VIDEO_URL + allvideoList.get(position).getVideo());
        holder.videoView.start();*/

        String path = "";
        //if (type.equals("for_you")) {
            holder.videoView.setVideoPath(VIDEO_URL + allvideoList.get(position).getVideo());
            holder.videoView.start();
           /* if (position == 0) {
                path = "android.resource://" + activity.getPackageName() + "/" + R.raw.video_1;
                holder.videoView.setVideoURI(Uri.parse(path));
                holder.videoView.start();
            } else if (position == 1) {
                path = "android.resource://" + activity.getPackageName() + "/" + R.raw.video_2;
                holder.videoView.setVideoURI(Uri.parse(path));
                holder.videoView.start();
            } else if (position == 2) {
                path = "android.resource://" + activity.getPackageName() + "/" + R.raw.video_3;
                holder.videoView.setVideoURI(Uri.parse(path));
                holder.videoView.start();
            } else if (position == 3) {
                path = "android.resource://" + activity.getPackageName() + "/" + R.raw.video_4;
                holder.videoView.setVideoURI(Uri.parse(path));
                holder.videoView.start();
            }*/
//        }
//        else if (type.equals("following")) {
//            holder.videoView.setVideoPath(VIDEO_URL + allvideoList.get(position).getVideo());
//            holder.videoView.start();
//        }

        holder.progressBar.setVisibility(View.VISIBLE);

        if(allvideoList.get(position).getSelf_like()==1)
        {
            holder.btn_Wishlist.setChecked(true);
        }

        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);

               /* mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        if(percent == 100){

                        }
                    }
                });*/
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int arg1, int arg2) {
                        holder.progressBar.setVisibility(View.GONE);
                        mp.start();
                        mp.setLooping(true);
                    }
                });

            }
        });


//        System.out.println("video_links=>" + allvideoList.get(position).getTotal_comments());


        RotateAnimation rotateAnimation = new RotateAnimation(0, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);

        //holder.imgMusic.startAnimation(rotateAnimation);

       /* holder.scroll.post(new Runnable() {
            @Override
            public void run() {
                holder.scroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });*/

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //holder.imgPlay.setVisibility(View.VISIBLE);
//
////                holder.videoView.canPause();
////                holder.videoView.pause();
//            }
//        });

        holder.itemView.setOnClickListener( new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {

                if (holder.llTopMenu.getVisibility() == View.VISIBLE) {
                    holder.llTopMenu.setVisibility(View.GONE);
                    holder.llSideMenu.setVisibility(View.GONE);
                    holder.lldetailsMenu.setVisibility(View.GONE);
                    holder.img_pause.setVisibility(View.GONE);
                    //holder.llFirstMenu.setVisibility(View.VISIBLE);
                } else {
                    holder.videoView.pause();
                    holder.llTopMenu.setVisibility(View.VISIBLE);
                    holder.llSideMenu.setVisibility(View.VISIBLE);
                    holder.lldetailsMenu.setVisibility(View.VISIBLE);
                    holder.img_pause.setVisibility(View.VISIBLE);
                    // holder.llFirstMenu.setVisibility(View.GONE);
                }
            }

            @Override
            public void onDoubleClick(View view) {
                new CountDownTimer( 3000, 3000 ) {
                    public void onTick(long millisUntilFinished) {
                        if(holder.btn_Wishlist.isChecked()==false){
                            holder.heart_gif.setVisibility(View.VISIBLE);
                        }
                    }
                    public void onFinish() {
                        if (SessionManager.onGetAutoCustomerId().isEmpty()) {
                            activity.startActivity(new Intent(activity, LoginActivity.class));
                        } else {
                            if (holder.btn_Wishlist.isChecked()) {
                                holder.btn_Wishlist.setChecked(false);
                                like_video_interface.Like_video(allvideoList.get(position).get_id(), "0", position);
                            } else if(holder.btn_Wishlist.isChecked()==false){
                                holder.heart_gif.setVisibility(View.VISIBLE);
                                holder.btn_Wishlist.setChecked(true);
                                like_video_interface.Like_video(allvideoList.get(position).get_id(), "1", position);
                            }
                        }
                        holder.heart_gif.setVisibility(View.GONE);

                        //onFinish();
                    }
                }.start();
                // Double tap here.
            }
        }));

        holder.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SessionManager.onGetAutoCustomerId().isEmpty()) {
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                } else {
                    activity.startActivity(new Intent(activity, ProfileActivity.class)
                            .putExtra("user_id", allvideoList.get(position).getUser_id()));
                }
            }
        });

        holder.imgCreateVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, UploadVideoActivity.class));
            }
        });
        holder.imgMusic.setOnClickListener(view -> activity.startActivity(new Intent(activity, UploadVideoForPerticularMusicActivity.class)));

        holder.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                sendIntent.setType("text/plain");
                activity.setTheme(R.style.CustomBottomSheetDialogTheme);
                activity.startActivity(sendIntent);
            }
        });

       /* holder.imgComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomSheet();
            }
        });*/
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
        holder.imgComment.setOnClickListener(view -> {
            if (SessionManager.onGetAutoCustomerId().isEmpty()) {
                activity.startActivity(new Intent(activity, LoginActivity.class));
            } else {
                showCommentListener.showComments(allvideoList.get(position).get_id(), position);

            }
        });
        holder.tvCommentCount.setText(String.valueOf(allvideoList.get(position).getTotal_comments()));
        holder.img_myHome.setOnClickListener(v -> {
//            if (SessionManager.onGetAutoCustomerId().isEmpty()) {
//                activity.startActivity(new Intent(activity, LoginActivity.class));
//            } else {
//                activity.startActivity(new Intent(activity, MyProfile_Activity.class));
//
//            }
            activity.startActivity(new Intent(activity, MainActivity.class));

        });
        holder.tv_username.setText("@"+allvideoList.get(position).getName());
        holder.tv_video_caption.setText("#"+allvideoList.get(position).getVideo_caption()+" #"+allvideoList.get(position).getVisibility());

        holder.btn_Wishlist.setOnClickListener(
                v -> {
                    if (SessionManager.onGetAutoCustomerId().isEmpty()) {
                        activity.startActivity(new Intent(activity, LoginActivity.class));
                    } else {
                        if (holder.btn_Wishlist.isChecked()) {

                            like_video_interface.Like_video(allvideoList.get(position).get_id(), "1", position);
                        } else {
                            holder.btn_Wishlist.setChecked(false);
                            like_video_interface.Like_video(allvideoList.get(position).get_id(), "0", position);
                        }
                    }

                }
        );
        holder.tvWishlistCount.setText(String.valueOf(allvideoList.get(position).getTotal_likes()));

        holder.imgSearch.setOnClickListener(v -> {
            if (holder.Search_layout.getVisibility() == View.VISIBLE) {
                holder.Search_layout.setVisibility(View.GONE);

                //holder.llFirstMenu.setVisibility(View.VISIBLE);
            } else {
                holder.Search_layout.setVisibility(View.VISIBLE);

                // holder.llFirstMenu.setVisibility(View.GONE);
            }

        });
        holder.et_serarch_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.Search_layout.setVisibility(View.GONE);
                activity.startActivity(new Intent(activity, Search_activity.class));
            }
        });

        holder.img_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.videoView.isPlaying()==true)
                {
                    holder.videoView.pause();
                    holder.img_pause.setVisibility(View.VISIBLE);
                }else
                {
                    holder.videoView.start();
                    holder.llTopMenu.setVisibility(View.GONE);
                    holder.llSideMenu.setVisibility(View.GONE);
                    holder.lldetailsMenu.setVisibility(View.GONE);
                    holder.img_pause.setVisibility(View.GONE);
                }
            }
        });
        holder.img_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new NotificationsFragment();
                FragmentManager manager = ((FragmentActivity) view.getContext()).getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (type.equals("for_you")) {
            return 4;
        } else if (type.equals("following")) {
            return 3;
        }
        return allvideoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;
        ImageView imgCreateVideo, imgPause, imgShare, imgDownload, imgSearch, imgMusic, imgProfile, imgComment,img_pause,img_Profile,img_notification;
        //  HorizontalScrollView scroll;
        TextView tvWishlistCount, tvCommentCount;
        EditText et_serarch_view;
        ProgressBar progressBar = null;
        LinearLayout llTopMenu, llSideMenu, lldetailsMenu, Search_layout, img_myHome;
        ToggleButton btn_Wishlist;
        TextView tv_username, tv_video_caption;
        ImageView heart_gif;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            imgPause = itemView.findViewById(R.id.imgPause);
            imgCreateVideo = itemView.findViewById(R.id.imgCreateVideo);
            imgSearch = itemView.findViewById(R.id.imgSearch);
            img_pause = itemView.findViewById(R.id.img_pause);
            et_serarch_view = itemView.findViewById(R.id.et_serarch_view);
            tvCommentCount = itemView.findViewById(R.id.tvCommentCount);
            img_Profile = itemView.findViewById(R.id.profile_image_icon);
            btn_Wishlist = itemView.findViewById(R.id.btn_Wishlist);
            img_myHome = itemView.findViewById(R.id.img_myHome);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            imgMusic = itemView.findViewById(R.id.imgMusic);
            Search_layout = itemView.findViewById(R.id.Search_layout);
            // scroll = itemView.findViewById(R.id.scroll);
            imgComment = itemView.findViewById(R.id.imgComment);
            imgShare = itemView.findViewById(R.id.imgShare);
            imgDownload = itemView.findViewById(R.id.imgDownload);
            tvWishlistCount = itemView.findViewById(R.id.tvWishlistCount);
            //tvShare=itemView.findViewById(R.id.tvShare);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressbar);
            llTopMenu = itemView.findViewById(R.id.llTopMenu);
            llSideMenu = itemView.findViewById(R.id.llSideMenu);
            tv_username = itemView.findViewById(R.id.tv_username);
            tv_video_caption = itemView.findViewById(R.id.tv_video_caption);
            //   llFirstMenu=itemView.findViewById(R.id.llFirstMenu);
            lldetailsMenu = itemView.findViewById(R.id.llDetailsMenu);
            heart_gif = itemView.findViewById(R.id.img_heart);
            img_notification = itemView.findViewById(R.id.imgNotification);
        }


    }


}
