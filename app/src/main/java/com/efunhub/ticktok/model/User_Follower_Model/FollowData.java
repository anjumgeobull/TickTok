
package com.efunhub.ticktok.model.User_Follower_Model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class FollowData {

    @SerializedName("followings_details")
    @Expose
    private List<FollowersDetail> followingsDetails = null;
    @SerializedName("followers_details")
    @Expose
    private List<FollowersDetail> followersDetails = null;

    public List<FollowersDetail> getFollowingsDetails() {
        return followingsDetails;
    }

    public void setFollowingsDetails(List<FollowersDetail> followingsDetails) {
        this.followingsDetails = followingsDetails;
    }

    public List<FollowersDetail> getFollowersDetails() {
        return followersDetails;
    }

    public void setFollowersDetails(List<FollowersDetail> followersDetails) {
        this.followersDetails = followersDetails;
    }

}
