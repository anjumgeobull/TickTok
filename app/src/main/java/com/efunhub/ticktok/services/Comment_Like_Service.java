package com.efunhub.ticktok.services;



import com.efunhub.ticktok.model.comment_model.Comment_Like_Video_Model;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Comment_Like_Service {
    @POST("comment-like")
    @FormUrlEncoded
    Call<Comment_Like_Video_Model> comment_like_video_model(@Field("user_id") String login_user_id,
                                                            @Field("video_id") String video_id,
                                                            @Field("like") String like,
                                                            @Field("comment_id") String comment_id);
}
