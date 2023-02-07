package com.efunhub.ticktok.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.efunhub.ticktok.R;
import com.efunhub.ticktok.adapter.VideoAdapter;
import com.efunhub.ticktok.application.SessionManager;
import com.efunhub.ticktok.interfaces.ShowCommentListener;
import com.efunhub.ticktok.model.AllVideoModel;
import com.efunhub.ticktok.retrofit.APICallback;
import com.efunhub.ticktok.retrofit.AlertDialogs;
import com.efunhub.ticktok.retrofit.PrintUtil;
import com.efunhub.ticktok.services.TrendingVideosSP;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;


public class TrendingFragment extends Fragment implements ShowCommentListener {


    private TrendingVideosSP trendingVideosSP;
    private AlertDialogs mAlert;
    RecyclerView rvVideoView;
    View root;
    VideoAdapter videoAdapter;
    //ArrayList<String> videoList = new ArrayList<>();
    ArrayList<AllVideoModel.Data>  videoArrayList;


    public TrendingFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root= inflater.inflate(R.layout.fragment_trending, container, false);
        init();
        return root;
    }

    private void init() {
        mAlert= AlertDialogs.getInstance();
        trendingVideosSP=new TrendingVideosSP(getActivity());
        rvVideoView=root.findViewById(R.id.rvVideoView);
        callGetAllVideoApi(SessionManager.onGetAutoCustomerId());
    }

       private void setAdapter() {
        /*videoList.add("https://grobiz.app/videos/WhatsApp%20Video%202020-09-16%20at%205.05.33%20PM.mp4");
        videoList.add("https://grobiz.app/videos/WhatsApp%20Video%202020-09-16%20at%205.05.33%20PM.mp4");
        videoList.add("https://grobiz.app/videos/WhatsApp%20Video%202020-09-16%20at%205.05.33%20PM.mp4");
        videoList.add("https://grobiz.app/videos/WhatsApp%20Video%202020-09-16%20at%205.05.33%20PM.mp4");
        videoList.add("https://grobiz.app/videos/WhatsApp%20Video%202020-09-16%20at%205.05.33%20PM.mp4");
        videoList.add("https://grobiz.app/videos/WhatsApp%20Video%202020-09-16%20at%205.05.33%20PM.mp4");*/

        SnapHelper snapHelper = new PagerSnapHelper();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1, RecyclerView.VERTICAL, false);
        rvVideoView.setLayoutManager(gridLayoutManager);
        videoAdapter = new VideoAdapter(getActivity(), videoArrayList,this);
        rvVideoView.setAdapter(videoAdapter);
        snapHelper.attachToRecyclerView(rvVideoView);
    }

    private void callGetAllVideoApi(String login_uer_id) {
       // mAlert.onShowProgressDialog(getActivity(), true);
        trendingVideosSP.CallGetTrendingVideo(login_uer_id,new APICallback() {
            @Override
            public <T> void onSuccess(T serviceResponse) {
                int Status = ((AllVideoModel) serviceResponse).getStatus();
                 videoArrayList = ((AllVideoModel) serviceResponse).getData();
                try {
                    String message = ((AllVideoModel) serviceResponse).getMsg();
                    if (Status == 1) {
                        mAlert.onShowToastNotification(getActivity(),"Success") ;
                        if(videoArrayList.isEmpty() ||videoArrayList==null) {

                        }else {
                            setAdapter();
                        }
                    } else {
                        mAlert.onShowToastNotification(getActivity(),"Fail") ;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mAlert.onShowProgressDialog(getActivity(), false);
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
        CommentBottomsheet bottomSheetFragment = new CommentBottomsheet();
       bottomSheetFragment.show(getFragmentManager(), bottomSheetFragment.getTag());

    }
}