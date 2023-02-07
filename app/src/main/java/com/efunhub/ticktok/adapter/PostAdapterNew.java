package com.efunhub.ticktok.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.efunhub.ticktok.R;
import com.efunhub.ticktok.interfaces.delete_Listener;
import com.efunhub.ticktok.interfaces.onItemClick_Listener;
import com.efunhub.ticktok.model.User_Profile_Model.Post;
import com.efunhub.ticktok.model.User_Profile_Model.UserProfile;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostAdapterNew extends RecyclerView.Adapter<PostAdapterNew.ViewHolder> {

   List<Post> videoList = new ArrayList<>();
    Context activity;
    private LayoutInflater mInflater;
    delete_Listener item_click;
    onItemClick_Listener onItemClickListener;

    public PostAdapterNew(Context context, List<Post> videoList,onItemClick_Listener onItemClickListener,delete_Listener delete_clicklistener) {
        this.activity = context;
        this.mInflater = LayoutInflater.from(context);
        this.videoList = videoList;
        this.onItemClickListener = onItemClickListener;
        this.item_click = delete_clicklistener;
    }

    @NonNull
    @Override
    public PostAdapterNew.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.myposts_list_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostAdapterNew.ViewHolder holder, int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity,PlayVideoActivity.class));

            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView videoView;
        ImageView imgPlay,imgPause,imgWishlisted,imgWishlist,imgProfile;
        ImageView img_delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            img_delete = itemView.findViewById(R.id.img_delete);
            img_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item_click != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            item_click.delete_click(position);
                        }
                    }
                }
            });
        }
    }

}

