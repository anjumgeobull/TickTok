package com.efunhub.ticktok.services;

import android.content.Context;


import com.efunhub.ticktok.model.NotificationModel;
import com.efunhub.ticktok.retrofit.APICallback;
import com.efunhub.ticktok.retrofit.APIServiceFactory;
import com.efunhub.ticktok.retrofit.BaseServiceResponseModel;
import com.efunhub.ticktok.retrofit.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowNotificationSP {
    private  final ShowNotificationService showNotificationService;

    public ShowNotificationSP(Context context) {
        showNotificationService = APIServiceFactory.createService(ShowNotificationService.class, context);
    }

    public  void callShowNotification(String customer_auto_id,final APICallback apiCallback)
    {
        Call<NotificationModel> call = null;
        call = showNotificationService.getNotifications(customer_auto_id);
        String url = call.request().url().toString();

        call.enqueue(new Callback<NotificationModel>() {
            @Override
            public void onResponse(Call<NotificationModel> call, Response<NotificationModel> response) {
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
            public void onFailure(Call<NotificationModel> call, Throwable t) {
                apiCallback.onFailure(null, null);
            }
        });
    }
}
