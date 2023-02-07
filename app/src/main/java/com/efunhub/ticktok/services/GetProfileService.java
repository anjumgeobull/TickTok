package com.efunhub.ticktok.services;



import com.efunhub.ticktok.model.ProfileModel;
import com.efunhub.ticktok.model.RegistrationModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GetProfileService {
    //////////////////////////////Profile-User Api Call////////////////////////
    @POST("Profile-User")
    @FormUrlEncoded
    Call<ProfileModel> userProfile(@Field("id") String id);
}
