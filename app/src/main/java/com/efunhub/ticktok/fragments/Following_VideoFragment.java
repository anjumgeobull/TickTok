package com.efunhub.ticktok.fragments;

import static com.efunhub.ticktok.retrofit.Constant.SERVER_URL;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.efunhub.ticktok.R;
import com.efunhub.ticktok.adapter.VideoAdapterNew;
import com.efunhub.ticktok.application.SessionManager;
import com.efunhub.ticktok.interfaces.Like_video_interface;
import com.efunhub.ticktok.interfaces.ShowCommentListener;
import com.efunhub.ticktok.model.AllVideoModel;
import com.efunhub.ticktok.retrofit.APICallback;
import com.efunhub.ticktok.retrofit.AlertDialogs;
import com.efunhub.ticktok.retrofit.PrintUtil;
import com.efunhub.ticktok.services.AllVideoServiceProvider;
import com.efunhub.ticktok.utility.VolleySingleton;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Following_VideoFragment extends Fragment implements ShowCommentListener, Like_video_interface {
    View view;
    private AllVideoServiceProvider allVideoServiceProvider;
    private AlertDialogs mAlert;
    RecyclerView rv_FOllowing_VideoView;
    View root;
    String user_id;
    VideoAdapterNew videoAdapter;
    String VIDEO_LIKE = "video-like";
    ArrayList<AllVideoModel.Data> videoArrayList;

    public Following_VideoFragment() {
        // Required empty public constructor
    }


    public static Following_VideoFragment newInstance(String param1, String param2) {
        Following_VideoFragment fragment = new Following_VideoFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_following__video, container, false);
        init();
        return view;
    }

    private void init() {
        mAlert = AlertDialogs.getInstance();
        allVideoServiceProvider = new AllVideoServiceProvider(getActivity());
        rv_FOllowing_VideoView = view.findViewById(R.id.rv_following_VideoView);
        user_id = SessionManager.onGetAutoCustomerId();
        callGetAllVideoApi(SessionManager.onGetAutoCustomerId());
    }

    private void setAdapter() {

        SnapHelper snapHelper = new PagerSnapHelper();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1, RecyclerView.VERTICAL, false);
        rv_FOllowing_VideoView.setLayoutManager(gridLayoutManager);
        videoAdapter = new VideoAdapterNew(getActivity(), videoArrayList, this, this, "following");

        rv_FOllowing_VideoView.setAdapter(videoAdapter);
        snapHelper.attachToRecyclerView(rv_FOllowing_VideoView);
    }

    private void callGetAllVideoApi(String login_uer_id) {
        // mAlert.onShowProgressDialog(getActivity(), true);
        allVideoServiceProvider.CallGetAllVideo(login_uer_id, new APICallback() {
            @Override
            public <T> void onSuccess(T serviceResponse) {
                int Status = ((AllVideoModel) serviceResponse).getStatus();
                try {
                    videoArrayList = ((AllVideoModel) serviceResponse).getData();
                    String message = ((AllVideoModel) serviceResponse).getMsg();

                    if (Status == 1) {
                        //  mAlert.onShowToastNotification(getActivity(),"Success") ;
                        if (videoArrayList.isEmpty() || videoArrayList == null) {

                        } else {
                            setAdapter();
                        }

                    } else {
                        mAlert.onShowToastNotification(getActivity(), "Fail");
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
    public void onPause() {

        super.onPause();
    }

    @Override
    public void showComments(String video_id, int position) {
        Bundle args = new Bundle();
        args.putString("video_id", video_id);
        args.putString("position", String.valueOf(position));
        CommentBottomsheet commentFragment = new CommentBottomsheet();

        commentFragment.setArguments(args);
        commentFragment.show(getFragmentManager(), commentFragment.getTag());


    }

    @Override
    public void Like_video(String video_id, String like_status, int position) {

    }
}


//
//    @Override
//    public void Like_video(String video_id, String like_status, int position) {
//
//        String Urls = SERVER_URL+VIDEO_LIKE;
//
//        StringRequest postRequest = new StringRequest(Request.Method.POST, Urls,
//                response -> {
//                    // response
//                    Log.d("Response", response);
//                    System.out.println("Like_Video_Data=> responce "+response);
//                    //progressDialog.dismiss();
//                    try {
//                        JSONObject jsonObject=new JSONObject(response);
//                        String msg=jsonObject.getString("msg");
//                        int status = jsonObject.getInt("status");
//
//                        System.out.println("Like_Video_Data=> status "+status);
//
//                        if (status==1) {
//
//                            int like = videoArrayList.get(position).getTotal_likes();
//                            System.out.println("like=>"+like);
//                            AllVideoModel.Data data = videoArrayList.get(position);
//                            data.setTotal_likes(like+1);
//                            videoAdapter.notifyDataSetChanged();
//                            Toast.makeText(getActivity(), "Liked", Toast.LENGTH_SHORT).show();
//
//                        } else if(status==2){
//
//                            int like = videoArrayList.get(position).getTotal_likes();
//                            System.out.println("like=>"+like);
//                            AllVideoModel.Data data = videoArrayList.get(position);
//                            data.setTotal_likes(like-1);
//                            videoAdapter.notifyDataSetChanged();
//                            Toast.makeText(getActivity(), "Remove Like", Toast.LENGTH_SHORT).show();
//                        }else {
//
//                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (Exception e) {
//
//                        e.printStackTrace();
//                        Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_LONG).show();
//                    }
//
//                },
//                error -> {
//                    // error
//                    Log.d("Error.Response", String.valueOf(error));
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams()
//            { Map<String, String>  params = new HashMap<String, String>();
//
//                params.put("user_id",user_id);
//                params.put("video_id", video_id);
//                params.put("like", like_status);
//
//                return params;
//            }
//        };
//        VolleySingleton.getInstance(getActivity()).addToRequestQueue(postRequest);
//    }
