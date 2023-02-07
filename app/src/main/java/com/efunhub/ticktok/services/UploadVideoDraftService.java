package com.efunhub.ticktok.services;




import com.efunhub.ticktok.model.UploadVideoModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadVideoDraftService {
    //////////////////////////////Profile-User Api Call////////////////////////
    @Multipart
    @POST("upload-video-draft")
    Call<UploadVideoModel> DraftuploadVideo(@Part("user_id") RequestBody user_id,
                                      @Part("visibility") RequestBody visibility,
                                      @Part("video_caption") RequestBody video_caption,
                                      @Part MultipartBody.Part video);

}
