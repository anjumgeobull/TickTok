package com.efunhub.ticktok.services;

import android.content.Context;

import com.efunhub.ticktok.model.LikeVideo_Model.Like_Video_Model;
import com.efunhub.ticktok.model.comment_model.Comment_Like_Video_Model;
import com.efunhub.ticktok.retrofit.APICallback;
import com.efunhub.ticktok.retrofit.APIServiceFactory;
import com.efunhub.ticktok.retrofit.BaseServiceResponseModel;
import com.efunhub.ticktok.retrofit.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Comment_LIke_Service_Provider {
    private  final Comment_Like_Service like_service;

    public Comment_LIke_Service_Provider(Context context) {
        like_service = APIServiceFactory.createService(Comment_Like_Service.class, context);
    }

    public  void CallGetAllCommentLikes(String user_id,String video_id, String like,String comment_id ,final APICallback apiCallback)
    {
        System.out.println("param"+user_id+"  "+video_id);
        Call<Comment_Like_Video_Model> call = null;
        call = like_service.comment_like_video_model(user_id, video_id, like,comment_id);
        String url = call.request().url().toString();

        call.enqueue(new Callback<Comment_Like_Video_Model>() {
            @Override
            public void onResponse(Call<Comment_Like_Video_Model> call, Response<Comment_Like_Video_Model> response) {
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
            public void onFailure(Call<Comment_Like_Video_Model> call, Throwable t) {
                apiCallback.onFailure(null, null);
            }
        });
    }
}
