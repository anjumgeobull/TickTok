package com.efunhub.ticktok.fragments;

import static android.content.ContentValues.TAG;
import static com.efunhub.ticktok.retrofit.Constant.SERVER_URL;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.efunhub.ticktok.R;
import com.efunhub.ticktok.adapter.VideoAdapterNew;
import com.efunhub.ticktok.application.SessionManager;

import com.efunhub.ticktok.interfaces.Like_video_interface;
import com.efunhub.ticktok.interfaces.ShowCommentListener;
import com.efunhub.ticktok.model.AllVideoModel;
import com.efunhub.ticktok.model.LikeVideo_Model.Like_Video_Model;
import com.efunhub.ticktok.model.comment_model.User_Comment;
import com.efunhub.ticktok.retrofit.APICallback;
import com.efunhub.ticktok.retrofit.AlertDialogs;
import com.efunhub.ticktok.retrofit.BaseServiceResponseModel;
import com.efunhub.ticktok.retrofit.PrintUtil;
import com.efunhub.ticktok.services.AllVideoServiceProvider;
import com.efunhub.ticktok.services.Video_LIke_Service_Provider;
import com.efunhub.ticktok.utility.DialogsUtils;
import com.efunhub.ticktok.utility.VolleySingleton;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AllVideoFragment extends Fragment implements ShowCommentListener, Like_video_interface {

    private AllVideoServiceProvider allVideoServiceProvider;
    private Video_LIke_Service_Provider video_lIke_service_provider;
    private AlertDialogs mAlert;
    RecyclerView rvVideoView;
    View root;
    String user_id;
    boolean flag;
    Button btn_click;
    VideoAdapterNew videoAdapter;
    String video_like = "video-like";

    Fragment fragment;


    // ArrayList<String> videoList = new ArrayList<>();

    ArrayList<AllVideoModel.Data>  videoArrayList;
    ArrayList<Like_Video_Model.Data> like_video_ArrayList;

    public AllVideoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root=inflater.inflate(R.layout.fragment_all_video, container, false);
        init();

    /*    View decorView = getActivity().getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(uiOptions);*/

        return root;
    }

    private void init() {
        mAlert=AlertDialogs.getInstance();
        allVideoServiceProvider=new AllVideoServiceProvider(getActivity());
        video_lIke_service_provider = new Video_LIke_Service_Provider(getActivity());
        rvVideoView=root.findViewById(R.id.rvVideoView);
       user_id = SessionManager.onGetAutoCustomerId();
        callGetAllVideoApi(SessionManager.onGetAutoCustomerId());
        flag = true;
    }

    private void setAdapter() {

        SnapHelper snapHelper = new PagerSnapHelper();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1, RecyclerView.VERTICAL, false);
        rvVideoView.setLayoutManager(gridLayoutManager);
        videoAdapter = new VideoAdapterNew(getActivity(), videoArrayList, this, this,"for_you");
      //  videoAdapter = new VideoAdapter(getActivity(), videoArrayList, this);

        rvVideoView.setAdapter(videoAdapter);

        snapHelper.attachToRecyclerView(rvVideoView);
    }


    private void callGetAllVideoApi(String login_uer_id) {
       // mAlert.onShowProgressDialog(getActivity(), true);
        allVideoServiceProvider.CallGetAllVideo(login_uer_id,new APICallback() {
            @Override
            public <T> void onSuccess(T serviceResponse) {
                int Status = ((AllVideoModel) serviceResponse).getStatus();
                try {
                    videoArrayList = ((AllVideoModel) serviceResponse).getData();
                    String message = ((AllVideoModel) serviceResponse).getMsg();
                    System.out.println("videoArrayList=>"+videoArrayList.size());
                    if (Status == 1) {
                      //  mAlert.onShowToastNotification(getActivity(),"Success") ;
                        if(videoArrayList.isEmpty())
                        {

                        }else {
                            setAdapter();
                        }

                    } else {
                        mAlert.onShowToastNotification(getActivity(),"Fail") ;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                 //   mAlert.onShowProgressDialog(getActivity(), false);
                }
            }
            @Override
            public <T> void onFailure(T apiErrorModel, T extras) {
                try {

                    PrintUtil.showNetworkAvailableToast(getActivity());
                   /* if (apiErrorModel != null) {
                        PrintUtil.showToast(LoginActivity.this, ((BaseServiceResponseModel) apiErrorModel).getMsg());
                    } else {
                        PrintUtil.showNetworkAvailableToast(LoginActivity.this);
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                    PrintUtil.showNetworkAvailableToast(getActivity());
                } finally {
                    mAlert.onShowProgressDialog(getActivity(), false);
                }
            }
        });
    }


    @Override
    public void showComments(String video_id, int position) {
        Bundle args = new Bundle();
        args.putString("video_id", video_id);
        args.putString("position", String.valueOf(position));
         /*BottomSheetDialogFragment bottomSheetFragment = new CommentBottomsheet();
        bottomSheetFragment.setArguments(args);
        bottomSheetFragment.show(getFragmentManager(), bottomSheetFragment.getTag());*/
        fragment = new CommentFragment();
        fragment.setArguments(args);
        final FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
//        // getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment);

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

                            int like = videoArrayList.get(position).getTotal_likes();
                            System.out.println("like=>"+like);
                           AllVideoModel.Data data = videoArrayList.get(position);
                           data.setTotal_likes(like+1);
                           videoAdapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), "Liked", Toast.LENGTH_SHORT).show();

                        } else if(status==2){

                            int like = videoArrayList.get(position).getTotal_likes();
                            System.out.println("like=>"+like);
                            AllVideoModel.Data data = videoArrayList.get(position);
                            data.setTotal_likes(like-1);
                            videoAdapter.notifyDataSetChanged();
                            //Toast.makeText(getActivity(), "Remove Like", Toast.LENGTH_SHORT).show();
                        }else {

                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {

                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_LONG).show();
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
System.out.println(params.toString());
                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(postRequest);
    }

//    @Override
//    public void onBackPressed() {
//        final Myfragment fragment = (Myfragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT);
//
//        if (fragment.allowBackPressed()) { // and then you define a method allowBackPressed with the logic to allow back pressed or not
//            super.onBackPressed();
//        }
//    }

}