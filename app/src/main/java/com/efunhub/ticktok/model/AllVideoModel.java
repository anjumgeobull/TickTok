package com.efunhub.ticktok.model;

import com.efunhub.ticktok.retrofit.BaseServiceResponseModel;

import java.util.ArrayList;
import java.util.List;

public  class AllVideoModel extends BaseServiceResponseModel {

    private ArrayList<Data> data;

    public ArrayList<Data> getData() {
        return data;
    }

    public void setData(ArrayList<Data> data) {
        this.data = data;
    }

    public static class Data {
        private int self_like;
        private int total_shares;
        private int total_views;
        private int total_likes;
        private int total_comments;


        public int getTotal_comments() {
            return total_comments;
        }

        public void setTotal_comments(int total_comments) {
            this.total_comments = total_comments;
        }

        private String created_at;
        private String updated_at;
        private String video_caption;
        private String visibility;
        private String video;
        private String _id;
        private String user_id;
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getSelf_like() {
            return self_like;
        }

        public void setSelf_like(int self_like) {
            this.self_like = self_like;
        }

        public int getTotal_shares() {
            return total_shares;
        }

        public void setTotal_shares(int total_shares) {
            this.total_shares = total_shares;
        }

        public int getTotal_views() {
            return total_views;
        }

        public void setTotal_views(int total_views) {
            this.total_views = total_views;
        }

        public int getTotal_likes() {
            return total_likes;
        }

        public void setTotal_likes(int total_likes) {
            this.total_likes = total_likes;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getVideo_caption() {
            return video_caption;
        }

        public void setVideo_caption(String video_caption) {
            this.video_caption = video_caption;
        }

        public String getVisibility() {
            return visibility;
        }

        public void setVisibility(String visibility) {
            this.visibility = visibility;
        }

        public String getVideo() {
            return video;
        }

        public void setVideo(String video) {
            this.video = video;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }
    }

}
