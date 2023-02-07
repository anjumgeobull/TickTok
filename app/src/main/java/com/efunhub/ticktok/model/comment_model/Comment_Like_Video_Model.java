
package com.efunhub.ticktok.model.comment_model;


import com.efunhub.ticktok.retrofit.BaseServiceResponseModel;

import java.util.ArrayList;


public class Comment_Like_Video_Model extends BaseServiceResponseModel {
    private ArrayList<Comment_Like_Video_Model.Data> data;

    public ArrayList<Data> getData() {
        return data;
    }

    public void setData(ArrayList<Data> data) {
        this.data = data;
    }

    public static class Data {
        private String _id;
        private String user_id;
        private String comment_id;
        private String like;
        private String updated_at;
        private String created_at;

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

        public String getVideo_id() {
            return comment_id;
        }

        public void setVideo_id(String video_id) {
            this.comment_id = video_id;
        }

        public String getLike() {
            return like;
        }

        public void setLike(String like) {
            this.like = like;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
    }
}

/*
public class Like_Video_Model {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("data")
    @Expose
    private Like_Video_Data likeVideoData;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Like_Video_Data getData() {
        return likeVideoData;
    }

    public void setData(Like_Video_Data likeVideoData) {
        this.likeVideoData = likeVideoData;
    }

}
*/
