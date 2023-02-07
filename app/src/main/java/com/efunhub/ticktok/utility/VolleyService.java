package com.efunhub.ticktok.utility;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.efunhub.ticktok.interfaces.IResult;


import java.util.Map;

public class VolleyService {

    IResult mResultCallback = null;
    Context mContext;

    public VolleyService(IResult mResultCallback, Context mContext) {
        this.mResultCallback = mResultCallback;
        this.mContext = mContext;
    }

    public void postDataVolleyParameters(final int requestId, String url, final Map<String, String> params){
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    if(mResultCallback != null)
                        mResultCallback.notifySuccess(requestId, response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(mResultCallback != null)
                        mResultCallback.notifyError(requestId, error);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };


            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(stringRequest);

        }catch(Exception e){
            Log.v("VolleyServiceException", e.toString());
        }
    }

    public void postDataVolley(final int requestId, String url){
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    if(mResultCallback != null)
                        mResultCallback.notifySuccess(requestId, response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(mResultCallback != null)
                        mResultCallback.notifyError(requestId, error);
                }
            });


            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(stringRequest);

        }catch(Exception e){
            Log.v("VolleyServiceException", e.toString());
        }
    }

    public void getDataVolley(final int requestId, String url){
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    if(mResultCallback != null)
                        mResultCallback.notifySuccess(requestId, response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(mResultCallback != null)
                        mResultCallback.notifyError(requestId, error);
                }
            });


            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(stringRequest);

        }catch(Exception e){
            Log.v("VolleyServiceException", e.toString());
        }
    }
}
