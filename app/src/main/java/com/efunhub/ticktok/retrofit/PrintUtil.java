package com.efunhub.ticktok.retrofit;

import android.content.Context;

import com.efunhub.ticktok.R;


public class PrintUtil {

    public static void showToast(Context context, String msg) {
        BaseActivity.showToast(context, msg);
    }

    public static String showNetworkAvailableToast(Context context) {
        try {
            if (BaseActivity.isNetworkAvailable(context)) {
                BaseActivity.showToast(context, context.getString(R.string.error_server));
                return context.getString(R.string.error_server);
            } else {
               // ComanAPIActivity.showToast(context, context.getString(R.string.error_internet));
               // return context.getString(R.string.error_internet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}