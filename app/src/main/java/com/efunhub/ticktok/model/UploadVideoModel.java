package com.efunhub.ticktok.model;

import com.efunhub.ticktok.retrofit.BaseServiceResponseModel;

public class UploadVideoModel extends BaseServiceResponseModel {
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private String _id;
        private String created_at;
        private String updated_at;
        private String video_caption;
        private String visibility;
        private String user_id;
        private String video;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
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

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getVideo() {
            return video;
        }

        public void setVideo(String video) {
            this.video = video;
        }
    }
}
