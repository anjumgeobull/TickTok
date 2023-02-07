
package com.efunhub.ticktok.model.User_Profile_Model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Follow_List implements Parcelable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("following_user_id")
    @Expose
    private String followingUserId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
   /* @SerializedName("user")
    @Expose
    private Follower_User_Info user;
*/
    public static final Creator<Follow_List> CREATOR = new Creator<Follow_List>() {
        @Override
        public Follow_List createFromParcel(Parcel in) {
            return new Follow_List(in);
        }

        @Override
        public Follow_List[] newArray(int size) {
            return new Follow_List[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFollowingUserId() {
        return followingUserId;
    }

    public void setFollowingUserId(String followingUserId) {
        this.followingUserId = followingUserId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /*public Follower_User_Info getUser() {
        return user;
    }

    public void setUser(Follower_User_Info user) {
        this.user = user;
    }*/

    public Follow_List(Parcel in) {
        id = in.readString();
        followingUserId = in.readString();
        userId = in.readString();
        date = in.readString();
        updatedAt = in.readString();
        createdAt = in.readString();


        // String name = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(followingUserId);
        dest.writeString(userId);
        dest.writeString(date);
        dest.writeString(updatedAt);
        dest.writeString(createdAt);
    }
}
