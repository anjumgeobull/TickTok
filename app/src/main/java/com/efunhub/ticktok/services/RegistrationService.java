package com.efunhub.ticktok.services;


import com.efunhub.ticktok.model.RegistrationModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RegistrationService {
    //////////////////////////////Registration Api Call////////////////////////
    @POST("login")
    @FormUrlEncoded
    Call<RegistrationModel> userRegistration(@Field("mobile") String mobile_number, @Field("password") String password);
}
