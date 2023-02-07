package com.efunhub.ticktok.retrofit;

import com.google.gson.annotations.SerializedName;

/**
 * Created by USER1 on 7/4/2017.
 */

public class BaseServiceResponseModel {
    @SerializedName("status")
    public int status;

    @SerializedName("msg")
    public String msg;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
