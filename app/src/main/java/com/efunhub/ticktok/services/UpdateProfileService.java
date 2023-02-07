package com.efunhub.ticktok.services;



import com.efunhub.ticktok.retrofit.BaseServiceResponseModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UpdateProfileService {
    //////////////////////////////Update-Profile-User Api Call////////////////////////
    @POST("Update-Profile-User")
    @FormUrlEncoded
    Call<BaseServiceResponseModel> updateProfile(@Field("user_id") String id,
                                                 @Field("mobile") String mobile_number,
                                                 @Field("name") String name,
                                                 @Field("email") String email);
}
//@Part
//MultipartBody.Part video);