package com.efunhub.ticktok.services;

import android.content.Context;


import com.efunhub.ticktok.model.RegistrationModel;
import com.efunhub.ticktok.retrofit.APICallback;
import com.efunhub.ticktok.retrofit.APIServiceFactory;
import com.efunhub.ticktok.retrofit.BaseServiceResponseModel;
import com.efunhub.ticktok.retrofit.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationServiceProvider {
    private  final RegistrationService registrationService;

    public RegistrationServiceProvider(Context context) {
        registrationService = APIServiceFactory.createService(RegistrationService.class, context);
    }

    public  void CallRegistration(String mobileNo, String password,final APICallback apiCallback)
    {
        Call<RegistrationModel> call = null;
        call = registrationService.userRegistration(mobileNo, password);
        String url = call.request().url().toString();
        System.out.println("url=>"+url);
        call.enqueue(new Callback<RegistrationModel>() {
            @Override
            public void onResponse(Call<RegistrationModel> call, Response<RegistrationModel> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 1) {
                    apiCallback.onSuccess(response.body());
                }
                else  if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 0) {
                    apiCallback.onSuccess(response.body());
                }
                else  if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 4) {
                    apiCallback.onSuccess(response.body());
                }else {
                    BaseServiceResponseModel model = ErrorUtils.parseError(response);
                    apiCallback.onFailure(model, response.errorBody());
                    // apiCallback.onFailure(response.body(), response.errorBody());
                }
            }
            @Override
            public void onFailure(Call<RegistrationModel> call, Throwable t) {
                apiCallback.onFailure(null, null);
            }
        });
    }
}
