package com.efunhub.ticktok.services;

import android.content.Context;

import com.efunhub.ticktok.retrofit.APICallback;
import com.efunhub.ticktok.retrofit.APIServiceFactory;
import com.efunhub.ticktok.retrofit.BaseServiceResponseModel;
import com.efunhub.ticktok.retrofit.ErrorUtils;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileSP {
    private  final UpdateProfileService updateProfileService;

    public UpdateProfileSP(Context context) {
        updateProfileService = APIServiceFactory.createService(UpdateProfileService.class, context);
    }

    public  void CallUpdateProfile(String id, String mobile_number, String name, String mail, final APICallback apiCallback)
    {
        Call<BaseServiceResponseModel> call = null;
        call = updateProfileService.updateProfile(id,mobile_number,name,mail);
        String url = call.request().url().toString();

        call.enqueue(new Callback<BaseServiceResponseModel>() {
            @Override
            public void onResponse(Call<BaseServiceResponseModel> call, Response<BaseServiceResponseModel> response) {
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
            public void onFailure(Call<BaseServiceResponseModel> call, Throwable t) {
                apiCallback.onFailure(null, null);
            }
        });
    }
}
