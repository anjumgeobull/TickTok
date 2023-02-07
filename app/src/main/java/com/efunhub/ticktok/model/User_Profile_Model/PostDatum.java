
package com.efunhub.ticktok.model.User_Profile_Model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostDatum {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("video")
    @Expose
    private String video;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("visibility")
    @Expose
    private String visibility;
    @SerializedName("video_caption")
    @Expose
    private String videoCaption;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("follow_user")
    @Expose
    private Integer followUser;
    @SerializedName("total_likes")
    @Expose
    private Integer totalLikes;
    @SerializedName("total_views")
    @Expose
    private Integer totalViews;
    @SerializedName("total_shares")
    @Expose
    private Integer totalShares;
    @SerializedName("total_comments")
    @Expose
    private Integer totalComments;
    @SerializedName("self_like")
    @Expose
    private Integer selfLike;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getVideoCaption() {
        return videoCaption;
    }

    public void setVideoCaption(String videoCaption) {
        this.videoCaption = videoCaption;
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

    public Integer getFollowUser() {
        return followUser;
    }

    public void setFollowUser(Integer followUser) {
        this.followUser = followUser;
    }

    public Integer getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(Integer totalLikes) {
        this.totalLikes = totalLikes;
    }

    public Integer getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(Integer totalViews) {
        this.totalViews = totalViews;
    }

    public Integer getTotalShares() {
        return totalShares;
    }

    public void setTotalShares(Integer totalShares) {
        this.totalShares = totalShares;
    }

    public Integer getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(Integer totalComments) {
        this.totalComments = totalComments;
    }

    public Integer getSelfLike() {
        return selfLike;
    }

    public void setSelfLike(Integer selfLike) {
        this.selfLike = selfLike;
    }



}
