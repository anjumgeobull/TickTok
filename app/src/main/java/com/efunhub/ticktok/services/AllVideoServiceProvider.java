package com.efunhub.ticktok.services;

import android.content.Context;

import com.efunhub.ticktok.model.AllVideoModel;
import com.efunhub.ticktok.model.RegistrationModel;
import com.efunhub.ticktok.retrofit.APICallback;
import com.efunhub.ticktok.retrofit.APIServiceFactory;
import com.efunhub.ticktok.retrofit.BaseServiceResponseModel;
import com.efunhub.ticktok.retrofit.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllVideoServiceProvider {
    private  final AllVideoService videoService;

    public AllVideoServiceProvider(Context context) {
        videoService = APIServiceFactory.createService(AllVideoService.class, context);
    }

    public  void CallGetAllVideo(String login_user_id,final APICallback apiCallback)
    {
        Call<AllVideoModel> call = null;
        call = videoService.allVideos(login_user_id);
        String url = call.request().url().toString();

        call.enqueue(new Callback<AllVideoModel>() {
            @Override
            public void onResponse(Call<AllVideoModel> call, Response<AllVideoModel> response) {
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
            public void onFailure(Call<AllVideoModel> call, Throwable t) {
                apiCallback.onFailure(null, null);
            }
        });
    }
}
