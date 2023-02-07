package com.efunhub.ticktok.activity;

import static com.efunhub.ticktok.retrofit.Constant.SERVER_URL;
import static com.efunhub.ticktok.utility.ConstantVariables.USER_PROFILE;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.android.volley.VolleyError;
import com.efunhub.ticktok.R;
import com.efunhub.ticktok.adapter.Followers_List_Adapter;
import com.efunhub.ticktok.application.SessionManager;
import com.efunhub.ticktok.interfaces.IResult;
import com.efunhub.ticktok.interfaces.onItemClick_Listener;
import com.efunhub.ticktok.model.User_Follower_Model.FollowData;
import com.efunhub.ticktok.model.User_Follower_Model.FollowersDetail;
import com.efunhub.ticktok.utility.VolleyService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Showing_Followers_List extends AppCompatActivity implements onItemClick_Listener {
    RecyclerView rvfollow_list;
    Toolbar toolbar;
    String following_user_id, id;
    String follow_type;
    private VolleyService mVolleyService;
    private IResult mResultCallBack = null;
    ProgressDialog progressDialog;
    Followers_List_Adapter followers_list_adapter;
    List<FollowData> followData = new ArrayList<>();
    List<FollowersDetail> followersDetailsList = new ArrayList<>();
    String Follow_List = "get_Follow_List";

    // ArrayList<Follow_List> follow_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showing_followers_list);
        initData();
        setUpToolbar();
        getFollow_Profile();
    }

    private void getFollow_Profile() {
        followData.clear();
        progressDialog.show();
        initVolleyCallback();
        mVolleyService = new VolleyService(mResultCallBack, this);
        Map<String, String> params = new HashMap<>();
        params.put("following_user_id", following_user_id);
        params.put("user_id", id);
        System.out.println("params=>" + params.toString());
        mVolleyService.postDataVolleyParameters(USER_PROFILE, SERVER_URL + Follow_List, params);

    }

    private void initVolleyCallback() {
        mResultCallBack = new IResult() {
            @Override
            public void notifySuccess(int requestId, String response) {
                JSONObject jsonObject = null;
                switch (requestId) {
                    case USER_PROFILE:
                        try {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }

                            jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            String status = jsonObject.getString("status");


                            if (Integer.parseInt(status) == 0) {

                                Toast.makeText(Showing_Followers_List.this, msg, Toast.LENGTH_SHORT).show();
                            } else if (Integer.parseInt(status) == 1) {

                                String followList = jsonObject.getString("data");
                                GsonBuilder gsonBuilder = new GsonBuilder();
                                Gson gson = gsonBuilder.create();
                                FollowData[] followdata = gson.fromJson(followList, FollowData[].class);
                                followData = new ArrayList<>(Arrays.asList(followdata));
                                if (!followData.isEmpty()) {
                                    if (follow_type.equals("following"))
                                    {
                                        followersDetailsList = followData.get(0).getFollowingsDetails();
                                    }
                                    else if (follow_type.equals("followers"))
                                    {
                                        followersDetailsList = followData.get(0).getFollowersDetails();

                                    }
                                    setAdapter();

                                }

                                // check_Follow_Status();

                            }


                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                        }
                        break;



                }


            }

            @Override
            public void notifyError(int requestId, VolleyError error) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(Showing_Followers_List.this);
                //  builder.setIcon(R.drawable.icon_exit);
                builder.setMessage("Server Error")
                        .setCancelable(true)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
                Log.v("Volley requestid ", String.valueOf(requestId));
                Log.v("Volley Error", String.valueOf(error));
            }


        };
    }
    private void setAdapter() {
        SnapHelper snapHelper = new PagerSnapHelper();
        rvfollow_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        followers_list_adapter = new Followers_List_Adapter(Showing_Followers_List.this, followersDetailsList, this);

        rvfollow_list.setAdapter(followers_list_adapter);
        rvfollow_list.setHasFixedSize(true);
        snapHelper.attachToRecyclerView(rvfollow_list);
    }

    private void initData() {
        rvfollow_list = findViewById(R.id.rvfollow_list);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            following_user_id = bundle.getString("following_user_id");
            follow_type = getIntent().getStringExtra("follow_type");

        }
        id = SessionManager.onGetAutoCustomerId();
       // follow_list = getIntent().getParcelableArrayListExtra("follow_list");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);

    }

    private void setUpToolbar() {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(follow_type);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
    @Override
    public void Item_ClickListner(int position) {
        if (!followersDetailsList.get(position).getUserId().equals(id)){
            startActivity(new Intent(Showing_Followers_List.this, ProfileActivity.class)
                    .putExtra("user_id", followersDetailsList.get(position).getUserId()));
        }
        else {
            Toast.makeText(Showing_Followers_List.this, "Please Search other profile", Toast.LENGTH_SHORT).show();
        }

    }
}