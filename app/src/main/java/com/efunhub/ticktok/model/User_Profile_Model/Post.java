
package com.efunhub.ticktok.model.User_Profile_Model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Post implements Parcelable {

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SerializedName("name")
    @Expose
    private String name;
   /* @SerializedName("user")
    @Expose
    private User user;*/
    private Post mInfo;

    public Post(Parcel in) {
        id = in.readString();
        video = in.readString();
        userId = in.readString();
        visibility = in.readString();
        videoCaption = in.readString();
        updatedAt = in.readString();
        createdAt = in.readString();
        followUser = in.readInt();
        totalLikes = in.readInt();
        totalViews = in.readInt();
        totalShares = in.readInt();
        totalComments = in.readInt();
        selfLike = in.readInt();
        name = in.readString();

        // String name = in.readString();
    }

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

   /* public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
*/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(video);
        dest.writeString(userId);
        dest.writeString(visibility);
        dest.writeString(videoCaption);
        dest.writeString(updatedAt);
        dest.writeString(createdAt);
        dest.writeInt(followUser);
        dest.writeInt(totalLikes);
        dest.writeInt(totalViews);
        dest.writeInt(totalShares);
        dest.writeInt(totalComments);
        dest.writeInt(selfLike);
        dest.writeString(name);

        // dest.writeString(user.getName());

    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        public Post[] newArray(int size) {
            return new Post[size];

        }
    };
}
