package com.efunhub.ticktok.services;

import android.content.Context;

import com.efunhub.ticktok.model.UploadVideoModel;
import com.efunhub.ticktok.retrofit.APICallback;
import com.efunhub.ticktok.retrofit.APIServiceFactory;
import com.efunhub.ticktok.retrofit.BaseServiceResponseModel;
import com.efunhub.ticktok.retrofit.ErrorUtils;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadVideoDraftSP {
    private  final UploadVideoDraftService uploadVideoService;

    public UploadVideoDraftSP(Context context) {
        uploadVideoService = APIServiceFactory.createService(UploadVideoDraftService.class, context);
    }

    public  void callUploaddraftVideo(RequestBody user_id, RequestBody visibility, RequestBody video_caption, MultipartBody.Part video, final APICallback apiCallback)
    {
        Call<UploadVideoModel> call = null;
        call = uploadVideoService.DraftuploadVideo(user_id,visibility,video_caption,video);
        String url = call.request().url().toString();
        System.out.println("urls=>"+url);
        call.enqueue(new Callback<UploadVideoModel>() {
            @Override
            public void onResponse(Call<UploadVideoModel> call, Response<UploadVideoModel> response) {
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
            public void onFailure(Call<UploadVideoModel> call, Throwable t) {
                apiCallback.onFailure(null, null);
            }
        });
    }
}
