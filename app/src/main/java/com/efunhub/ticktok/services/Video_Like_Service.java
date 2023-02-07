package com.efunhub.ticktok.services;

import com.efunhub.ticktok.model.AllVideoModel;
import com.efunhub.ticktok.model.LikeVideo_Model.Like_Video_Model;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Video_Like_Service {
    @POST("video-like")
    @FormUrlEncoded
    Call<Like_Video_Model> like_video_model(@Field("user_id") String login_user_id,
                                            @Field("video_id") String video_id,
                                            @Field("like") String like);
}
