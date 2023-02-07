
package com.efunhub.ticktok.model.User_Follower_Model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class FollowingsDetail {

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
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("following")
    @Expose
    private Integer following;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFollowing() {
        return following;
    }

    public void setFollowing(Integer following) {
        this.following = following;
    }

}
