package com.efunhub.ticktok.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.efunhub.ticktok.R;
import com.efunhub.ticktok.activity.LoginActivity;
import com.efunhub.ticktok.application.SessionManager;
import com.efunhub.ticktok.interfaces.Comment_Like_video_interface;
import com.efunhub.ticktok.interfaces.Like_video_interface;
import com.efunhub.ticktok.model.comment_model.User_Comment;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    ArrayList<User_Comment> comment_modelList = new ArrayList<>();
    Context activity;
    Comment_Like_video_interface like_video_interface;
    private LayoutInflater mInflater;

    public CommentsAdapter(Context context, ArrayList<User_Comment> comment_modelList, Comment_Like_video_interface like_video_interface) {
        this.activity = context;
        this.mInflater = LayoutInflater.from(context);
        this.comment_modelList = comment_modelList;
        this.like_video_interface = like_video_interface;
    }

    @NonNull
    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.comments_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentsAdapter.ViewHolder holder, int position) {

        holder.txtName.setText(comment_modelList.get(position).getName());
        holder.txtcomment.setText(comment_modelList.get(position).getCommentText());
        if(comment_modelList.get(position).getSelfLike()==1)
        {
            holder.imgWishlisted.setVisibility(View.VISIBLE);
            holder.imgWishlist.setVisibility(View.GONE);
        }
        holder.imgWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.imgWishlisted.setVisibility(View.VISIBLE);
                holder.imgWishlist.setVisibility(View.GONE);
                if (SessionManager.onGetAutoCustomerId().isEmpty()) {
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                } else {
                    like_video_interface.comment_Like_video(comment_modelList.get(position).getVideoId(), "1", position,comment_modelList.get(position).getId());
                }
            }
        });

        holder.imgWishlisted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.imgWishlisted.setVisibility(View.GONE);
                holder.imgWishlist.setVisibility(View.VISIBLE);
                if (SessionManager.onGetAutoCustomerId().isEmpty()) {
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                } else {
                    like_video_interface.comment_Like_video(comment_modelList.get(position).getVideoId(), "0", position,comment_modelList.get(position).getId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return comment_modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPlay, imgPause, imgWishlisted, imgWishlist, imgMusic, imgProfile, imgComment;
        TextView txtName, txtcomment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            imgWishlisted = itemView.findViewById(R.id.imgWishlisted);
            imgWishlist = itemView.findViewById(R.id.imgWishlist);
            txtcomment = itemView.findViewById(R.id.txtcomment);

        }
    }
}
