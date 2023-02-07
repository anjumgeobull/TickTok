package com.efunhub.ticktok.adapter;

import static com.efunhub.ticktok.retrofit.Constant.SERVER_URL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.efunhub.ticktok.R;
import com.efunhub.ticktok.application.SessionManager;
import com.efunhub.ticktok.fragments.CommentBottomsheet;
import com.efunhub.ticktok.interfaces.Like_video_interface;
import com.efunhub.ticktok.interfaces.ShowCommentListener;
import com.efunhub.ticktok.model.User_Profile_Model.Post;
import com.efunhub.ticktok.utility.VolleySingleton;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayVideoActivity extends AppCompatActivity implements ShowCommentListener, Like_video_interface , Serializable {

    RecyclerView rvVideoView;
    String user_id;
    ArrayList<Post> postList = new ArrayList<>();
    Video_Adapter_Post videoAdapter;
    String video_like = "video-like";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        initData();
        setAdapter();
    }

    private void initData() {
        rvVideoView = findViewById(R.id.rvVideoView);
        //postList = (List<Post>) getIntent().getSerializableExtra("video_list");
        postList =  getIntent().getParcelableArrayListExtra("video_list");
        user_id = SessionManager.onGetAutoCustomerId();
    }

    private void setAdapter() {

        SnapHelper snapHelper = new PagerSnapHelper();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(PlayVideoActivity.this, 1, RecyclerView.VERTICAL, false);
        rvVideoView.setLayoutManager(gridLayoutManager);
        videoAdapter = new Video_Adapter_Post(PlayVideoActivity.this, postList, this, this);
        //  videoAdapter = new VideoAdapter(getActivity(), videoArrayList, this);

        rvVideoView.setAdapter(videoAdapter);

        snapHelper.attachToRecyclerView(rvVideoView);
    }

    @Override
    public void showComments(String video_id, int position) {
        Bundle args = new Bundle();
        args.putString("video_id", video_id);
        args.putString("position", String.valueOf(position));
        BottomSheetDialogFragment bottomSheetFragment = new CommentBottomsheet();

        bottomSheetFragment.setArguments(args);
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    @Override
    public void Like_video(String video_id, String like_status, int position) {

        String Urls = SERVER_URL+video_like;

        StringRequest postRequest = new StringRequest(Request.Method.POST, Urls,
                response -> {
                    // response
                    Log.d("Response", response);
                    System.out.println("Like_Video_Data=> responce "+response);
                    //progressDialog.dismiss();
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        String msg=jsonObject.getString("msg");
                        int status = jsonObject.getInt("status");

                        System.out.println("Like_Video_Data=> status "+status);

                        if (status==1) {

                            int like = postList.get(position).getTotalLikes();
                            System.out.println("like=>"+like);
                            Post data = postList.get(position);
                            data.setTotalLikes(like+1);
                            videoAdapter.notifyDataSetChanged();
                            Toast.makeText(PlayVideoActivity.this, "Liked", Toast.LENGTH_SHORT).show();

                        } else if(status==2){

                            int like = postList.get(position).getTotalLikes();
                            System.out.println("like=>"+like);
                            Post data = postList.get(position);
                            data.setTotalLikes(like-1);
                            videoAdapter.notifyDataSetChanged();
                            Toast.makeText(PlayVideoActivity.this, "Remove Like", Toast.LENGTH_SHORT).show();
                        }else {

                            Toast.makeText(PlayVideoActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {

                        e.printStackTrace();
                        Toast.makeText(PlayVideoActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                    }

                },
                error -> {
                    // error
                    Log.d("Error.Response", String.valueOf(error));
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            { Map<String, String>  params = new HashMap<String, String>();

                params.put("user_id",user_id);
                params.put("video_id", video_id);
                params.put("like", like_status);

                return params;
            }
        };
        VolleySingleton.getInstance(PlayVideoActivity.this).addToRequestQueue(postRequest);
    }

}
