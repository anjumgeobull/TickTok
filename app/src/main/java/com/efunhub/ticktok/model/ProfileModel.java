package com.efunhub.ticktok.model;

import com.efunhub.ticktok.retrofit.BaseServiceResponseModel;

public  class ProfileModel extends BaseServiceResponseModel {

    private User_profile user_profile;

    public User_profile getUser_profile() {
        return user_profile;
    }

    public void setUser_profile(User_profile user_profile) {
        this.user_profile = user_profile;
    }

    public static class User_profile {
        private String created_at;
        private String updated_at;
        private String bio;
        private String dob;
        private String gender;
        private String name;
        private String mobile_number;
        private String _id;

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

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMobile_number() {
            return mobile_number;
        }

        public void setMobile_number(String mobile_number) {
            this.mobile_number = mobile_number;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }
    }
}
