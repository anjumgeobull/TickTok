package com.efunhub.ticktok.services;

import android.content.Context;

import com.efunhub.ticktok.model.ProfileModel;
import com.efunhub.ticktok.model.RegistrationModel;
import com.efunhub.ticktok.retrofit.APICallback;
import com.efunhub.ticktok.retrofit.APIServiceFactory;
import com.efunhub.ticktok.retrofit.BaseServiceResponseModel;
import com.efunhub.ticktok.retrofit.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetProfileSP {
    private  final GetProfileService getProfileService;

    public GetProfileSP(Context context) {
        getProfileService = APIServiceFactory.createService(GetProfileService.class, context);
    }

    public  void CallGetProfile(String id,final APICallback apiCallback)
    {
        System.out.println("userid"+id);
        Call<ProfileModel> call = null;
        call = getProfileService.userProfile(id);
        String url = call.request().url().toString();

        call.enqueue(new Callback<ProfileModel>() {
            @Override
            public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {
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
            public void onFailure(Call<ProfileModel> call, Throwable t) {
                apiCallback.onFailure(null, null);
            }
        });
    }
}
