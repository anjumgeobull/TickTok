package com.efunhub.ticktok.services;

import android.content.Context;

import com.efunhub.ticktok.model.AllVideoModel;
import com.efunhub.ticktok.model.LikeVideo_Model.Like_Video_Model;
import com.efunhub.ticktok.retrofit.APICallback;
import com.efunhub.ticktok.retrofit.APIServiceFactory;
import com.efunhub.ticktok.retrofit.BaseServiceResponseModel;
import com.efunhub.ticktok.retrofit.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Video_LIke_Service_Provider {
    private  final Video_Like_Service like_service;

    public Video_LIke_Service_Provider(Context context) {
        like_service = APIServiceFactory.createService(Video_Like_Service.class, context);
    }

    public  void CallGetAllVideoLikes(String user_id,String video_id, String like, final APICallback apiCallback)
    {
        System.out.println("param"+user_id+"  "+video_id);
        Call<Like_Video_Model> call = null;
        call = like_service.like_video_model(user_id, video_id, like);
        String url = call.request().url().toString();

        call.enqueue(new Callback<Like_Video_Model>() {
            @Override
            public void onResponse(Call<Like_Video_Model> call, Response<Like_Video_Model> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 1) {
                    apiCallback.onSuccess(response.body());
                }
                else  if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 0) {
                    apiCallback.onSuccess(response.body());
                }else {
                    BaseServiceResponseModel model = ErrorUtils.parseError(response);
                    apiCallback.onFailure(model, response.errorBody());
                    // apiCallback.onFailure(response.body(), response.errorBody());
                }
            }
            @Override
            public void onFailure(Call<Like_Video_Model> call, Throwable t) {
                apiCallback.onFailure(null, null);
            }
        });
    }
}
