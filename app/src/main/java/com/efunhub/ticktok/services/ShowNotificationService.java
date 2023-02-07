package com.efunhub.ticktok.services;



import com.efunhub.ticktok.model.NotificationModel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ShowNotificationService {
    //////////////////////////////Show-Notifications Api Call////////////////////////
    @POST("Show-Notifications")
    @FormUrlEncoded
    Call<NotificationModel> getNotifications(@SafeParcelable.Field(id = 1, type = "customer_auto_id") String customer_auto_id);
}
