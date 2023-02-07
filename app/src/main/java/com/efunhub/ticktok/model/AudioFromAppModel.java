package com.efunhub.ticktok.model;

import com.efunhub.ticktok.retrofit.BaseServiceResponseModel;

import java.util.ArrayList;


public  class AudioFromAppModel extends BaseServiceResponseModel {

    private ArrayList<AudioList> data;

    public ArrayList<AudioList> getData() {
        return data;
    }

    public void setData(ArrayList<AudioList> data) {
        this.data = data;
    }

    public class AudioList {

        private String audiofile;
        private String _id;
        private String updated_at;
        private String created_at;

        public String getAudiofile() {
            return audiofile;
        }

        public void setAudiofile(String audiofile) {
            this.audiofile = audiofile;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
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
