
package com.efunhub.ticktok.model.comment_model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class User_Comment {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("video_id")
    @Expose
    private String videoId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("comment_text")
    @Expose
    private String commentText;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("total_comment_likes")
    @Expose
    private Integer totalCommentLikes;
    @SerializedName("self_like")
    @Expose
    private Integer selfLike;
    /*@SerializedName("user")
    @Expose
    private User user;
*/
    @SerializedName("name")
    @Expose
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
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

    public Integer getTotalCommentLikes() {
        return totalCommentLikes;
    }

    public void setTotalCommentLikes(Integer totalCommentLikes) {
        this.totalCommentLikes = totalCommentLikes;
    }

    public Integer getSelfLike() {
        return selfLike;
    }

    public void setSelfLike(Integer selfLike) {
        this.selfLike = selfLike;
    }

    /*public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }*/

}
