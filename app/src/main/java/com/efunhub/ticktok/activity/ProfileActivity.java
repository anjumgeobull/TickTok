package com.efunhub.ticktok.activity;

import static com.efunhub.ticktok.retrofit.Constant.SERVER_URL;
import static com.efunhub.ticktok.utility.ConstantVariables.CHECK_FOLLOW_STATUS;
import static com.efunhub.ticktok.utility.ConstantVariables.FOLLOW_USER;
import static com.efunhub.ticktok.utility.ConstantVariables.USER_PROFILE;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.efunhub.ticktok.R;
import com.efunhub.ticktok.adapter.PlayVideoActivity;
import com.efunhub.ticktok.adapter.PostAdapter;
import com.efunhub.ticktok.application.SessionManager;
import com.efunhub.ticktok.interfaces.IResult;
import com.efunhub.ticktok.interfaces.onItemClick_Listener;
import com.efunhub.ticktok.model.User_Profile_Model.Follow_List;
import com.efunhub.ticktok.model.User_Profile_Model.Post;
import com.efunhub.ticktok.model.User_Profile_Model.UserProfile;
import com.efunhub.ticktok.utility.VolleyService;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, onItemClick_Listener {

    RecyclerView rvPosts;
    ProgressDialog progressDialog;
    PostAdapter videoAdapter;

    Toolbar toolbar;
    TextView tv_user_name, tv_user_country, tv_user_post, tv_user_follower, tv_user_following;
    Button btnShare, btnFollow;
    ShimmerFrameLayout shimmer_view_container;
    TextView tv_no_result;
    BottomSheetDialog bottomSheetDialog;
    Menu mMenu;
    String following_user_id, id;
    LinearLayout follower_layout, user_following_layout;
    ArrayList<UserProfile> userProfileModel_List = new ArrayList<>();
    ArrayList<Post> postList = new ArrayList<>();
    LinearLayout profile_layout;

    private VolleyService mVolleyService;
    private IResult mResultCallBack = null;
    MenuItem item, item_more;
    String Profile_User = "Profile-User";
    String Check_follow_status = "check_follow_status";
    String Follow_Profile = "following-user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initData();
        setUpToolbar();
        getUser_Profile();
        //check_Follow_Status();
        // setUpRecycler();
    }


    private void getUser_Profile() {
        userProfileModel_List.clear();
        //progressDialog.show();
        shimmer_view_container.startShimmerAnimation();
        initVolleyCallback();
        mVolleyService = new VolleyService(mResultCallBack, this);

        Map<String, String> params = new HashMap<>();
        params.put("id", following_user_id);
        params.put("login_user_id", id);
        params.put("video_user_id", following_user_id);

        System.out.println("params=>" + params.toString());
        mVolleyService.postDataVolleyParameters(USER_PROFILE, SERVER_URL + Profile_User, params);
    }

    private void initVolleyCallback() {
        mResultCallBack = new IResult() {

            @Override
            public void notifySuccess(int requestId, String response) {
                JSONObject jsonObject = null;
                switch (requestId) {
                    case USER_PROFILE:
                        try {


                            jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            String status = jsonObject.getString("status");


                            if (Integer.parseInt(status) == 0) {
                                shimmer_view_container.stopShimmerAnimation();
                                shimmer_view_container.setVisibility(View.GONE);
                                profile_layout.setVisibility(View.VISIBLE);
                                rvPosts.setVisibility(View.VISIBLE);
                                tv_no_result.setVisibility(View.GONE);

                                Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } else if (Integer.parseInt(status) == 1) {
                                shimmer_view_container.stopShimmerAnimation();
                                shimmer_view_container.setVisibility(View.GONE);
                                profile_layout.setVisibility(View.VISIBLE);
                                rvPosts.setVisibility(View.VISIBLE);
                                tv_no_result.setVisibility(View.GONE);
                                String current_order = jsonObject.getString("user_profile");
                                GsonBuilder gsonBuilder = new GsonBuilder();
                                Gson gson = gsonBuilder.create();
                                UserProfile userProfiles = gson.fromJson(current_order, UserProfile.class);
                                userProfileModel_List = new ArrayList<>(Arrays.asList(userProfiles));
                                if (!userProfileModel_List.isEmpty()) {
                                    postList = (ArrayList<Post>) userProfileModel_List.get(0).getPosts();

                                    setUpRecycler();
                                    if (userProfileModel_List.get(0).getFollow_status().equals("No")) {

                                        btnFollow.setText("Follow");

                                    } else if (userProfileModel_List.get(0).getFollow_status().equals("Yes")) {

                                        btnFollow.setText("Unfollow");

                                    }
                                    tv_user_post.setText(String.valueOf(userProfileModel_List.get(0).getTotalPosts()));
                                    tv_user_follower.setText(String.valueOf(userProfileModel_List.get(0).getTotalFollowers()));
                                    tv_user_following.setText(String.valueOf(userProfileModel_List.get(0).getTotalFollowings()));
                                    Toast.makeText(ProfileActivity.this, userProfileModel_List.get(0).getName(), Toast.LENGTH_SHORT).show();
                                    tv_user_name.setText(userProfileModel_List.get(0).getName());
                                    tv_user_country.setText("@"+userProfileModel_List.get(0).getCountry());
                                }
                                else {

                                    rvPosts.setVisibility(View.GONE);
                                    tv_no_result.setVisibility(View.VISIBLE);
                                }
                               // check_Follow_Status();

                            }


                        } catch (JSONException e1) {
                            shimmer_view_container.stopShimmerAnimation();
                            shimmer_view_container.setVisibility(View.GONE);
                            profile_layout.setVisibility(View.VISIBLE);
                            e1.printStackTrace();
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                        }
                        break;



                    case FOLLOW_USER:
                        try {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }

                            jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            String status = jsonObject.getString("status");


                            if (Integer.parseInt(status) == 0) {

                               btnFollow.setText("Follow");
                                getUser_Profile();
                               Toast.makeText(ProfileActivity.this, "Unfollow Successfully", Toast.LENGTH_SHORT).show();
                            } else if (Integer.parseInt(status) == 1) {

                                Toast.makeText(ProfileActivity.this, "Follow Successfully", Toast.LENGTH_SHORT).show();
                                getUser_Profile();
                                btnFollow.setText("Unfollow");

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
                /*if (progressDialog != null) {
                    progressDialog.dismiss();
                }*/
                shimmer_view_container.stopShimmerAnimation();
                shimmer_view_container.setVisibility(View.GONE);
                profile_layout.setVisibility(View.VISIBLE);
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
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


    private void setUpToolbar() {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Profile");
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

    private void setUpRecycler() {
        if (!postList.isEmpty())
        {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(ProfileActivity.this, 3, RecyclerView.VERTICAL, false);
            rvPosts.setLayoutManager(gridLayoutManager);
            videoAdapter = new PostAdapter(ProfileActivity.this, postList, this);
            rvPosts.setAdapter(videoAdapter);
        }
        else {
            rvPosts.setVisibility(View.GONE);
            tv_no_result.setVisibility(View.VISIBLE);
        }

    }

    private void initData() {
        btnFollow = findViewById(R.id.btnFollow);
        rvPosts = findViewById(R.id.rvPosts);
        tv_user_name = findViewById(R.id.tv_user_name);
        tv_user_country = findViewById(R.id.tv_user_country);
        tv_user_follower = findViewById(R.id.tv_user_follower);
        tv_user_following = findViewById(R.id.tv_user_following);
        shimmer_view_container = findViewById(R.id.shimmer_view_container);
        tv_user_post = findViewById(R.id.tv_user_post);
        tv_no_result = findViewById(R.id.tv_no_result);
        profile_layout = findViewById(R.id.profile_layout);
        user_following_layout = findViewById(R.id.following_layout);
        follower_layout = findViewById(R.id.follower_layout);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            following_user_id = bundle.getString("user_id");

        }
        id = SessionManager.onGetAutoCustomerId();
        btnFollow.setOnClickListener(this);
        user_following_layout.setOnClickListener(this);
        follower_layout.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnFollow:
                Follow_User(btnFollow.getText().toString());
                break;
            case R.id.following_layout:
                ShowFollowing_List("following");
                break;
            case R.id.follower_layout:
                ShowFollowing_List("followers");
                break;

        }
    }

    private void Follow_User(String follow_status) {
        if (follow_status.equals("Follow"))
        {
            Follow_Profile("1");

        }else if (follow_status.equals("Unfollow"))
        {
            Follow_Profile("0");
        }
    }

    private void Follow_Profile(String status) {
        progressDialog.show();
        initVolleyCallback();
        mVolleyService = new VolleyService(mResultCallBack, this);

        Map<String, String> params = new HashMap<>();
        params.put("following_user_id", following_user_id);
        params.put("user_id", id);
        params.put("following", status);

        System.out.println("params=>" + params.toString());
        mVolleyService.postDataVolleyParameters(FOLLOW_USER, SERVER_URL + Follow_Profile, params);
    }

    private void ShowFollowing_List(String followers) {
        if (followers.equals("following")) {
            Intent i = new Intent(ProfileActivity.this, Showing_Followers_List.class);
            i.putExtra("follow_type", followers);
            i.putExtra("following_user_id", following_user_id);
          //  i.putParcelableArrayListExtra("follow_list", Following_List);
            startActivity(i);
        } else if (followers.equals("followers")) {
            Intent i = new Intent(ProfileActivity.this, Showing_Followers_List.class);
            i.putExtra("follow_type", followers);
            i.putExtra("following_user_id", following_user_id);

            //   i.putParcelableArrayListExtra("follow_list", follower_List);
            startActivity(i);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        this.mMenu = menu;

        //  item = mMenu.findItem(R.id.action_chat);
        item_more = mMenu.findItem(R.id.action_more);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

      /*  if (id == R.id.action_chat) {
            startActivity(new Intent(this, ChatActivity.class));
        }*/
        if (id == R.id.action_more) {
            Toast.makeText(this, "more", Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(this, ChatActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void Item_ClickListner(int position) {
        ArrayList<Post> selected_postList = new ArrayList<>();
        selected_postList.add(postList.get(position));
        Intent i = new Intent(ProfileActivity.this, PlayVideoActivity.class);
        i.putParcelableArrayListExtra("video_list", selected_postList);
        startActivity(i);
    }
}
