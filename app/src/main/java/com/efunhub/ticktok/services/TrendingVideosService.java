package com.efunhub.ticktok.services;



import com.efunhub.ticktok.model.AllVideoModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TrendingVideosService {
    //////////////////////////////all Videos Api Call////////////////////////
    @POST("trending-videos")
    @FormUrlEncoded
    Call<AllVideoModel> trendingVideos(@Field("login_user_id") String login_user_id);
}
