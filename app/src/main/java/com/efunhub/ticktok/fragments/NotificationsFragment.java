package com.efunhub.ticktok.fragments;

import static com.efunhub.ticktok.retrofit.Constant.SERVER_URL;
import static com.efunhub.ticktok.utility.ConstantVariables.NOTIFICATION_LIST;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.efunhub.ticktok.R;
import com.efunhub.ticktok.adapter.NotificationAdapter;
import com.efunhub.ticktok.application.SessionManager;
import com.efunhub.ticktok.interfaces.IResult;
import com.efunhub.ticktok.model.Notification_Model;
import com.efunhub.ticktok.utility.VolleyService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class NotificationsFragment extends Fragment {

    RecyclerView rvNotification;
    View view;
    NotificationAdapter notificationAdapter;
    Toolbar toolbar;
    String Notification_List = "notifications-list";
    private VolleyService mVolleyService;
    private IResult mResultCallBack = null;
    ProgressDialog progressDialog;
    String  id;
    TextView tv_no_result;
    ArrayList<Notification_Model> notification_list = new ArrayList<Notification_Model>();
    ArrayList<Notification_Model.NotificationList> notifications = new ArrayList<Notification_Model.NotificationList>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notifications, container, false);
        initData();
        setUpToolbar();
        getnotifications();
        return view;
    }

    private void initData() {
        id = SessionManager.onGetAutoCustomerId();
        tv_no_result = view.findViewById(R.id.tv_no_result);
        rvNotification = view.findViewById(R.id.rvNotification);

    }

    private void setUpToolbar() {

        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).setTitle("Notification");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                if(fm.getBackStackEntryCount()>0) {
                    fm.popBackStack();
                }
            }
        });
    }

    private void getnotifications() {
        notification_list.clear();
        progressDialog=new ProgressDialog(getContext());
        progressDialog.show();
        initVolleyCallback();
        mVolleyService = new VolleyService(mResultCallBack, getActivity());
        mVolleyService.getDataVolley(NOTIFICATION_LIST, SERVER_URL + Notification_List);

    }

    private void initVolleyCallback() {
        mResultCallBack = new IResult() {

            @Override
            public void notifySuccess(int requestId, String response) {
                JSONObject jsonObject = null;
                switch (requestId) {
                    case NOTIFICATION_LIST:
                        try {
                            jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            String status = jsonObject.getString("status");

                            if (Integer.parseInt(status) == 0) {
                                progressDialog.dismiss();
                                rvNotification.setVisibility(View.VISIBLE);
                                tv_no_result.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                            } else if (Integer.parseInt(status) == 1) {
                                progressDialog.dismiss();
                                rvNotification.setVisibility(View.VISIBLE);
                                tv_no_result.setVisibility(View.GONE);
                                String current_order = jsonObject.getString("data");
                                GsonBuilder gsonBuilder = new GsonBuilder();
                                Gson gson = gsonBuilder.create();
                                Notification_Model.NotificationList[] userProfiles = gson.fromJson(current_order,Notification_Model.NotificationList[].class);
                                notifications = new ArrayList<>(Arrays.asList(userProfiles));
                                if (!notifications.isEmpty()) {
                                    setAdapter();
                                }
                                else {
                                    rvNotification.setVisibility(View.GONE);
                                    tv_no_result.setVisibility(View.VISIBLE);
                                }
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
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
                //  builder.setIcon(R.drawable.icon_exit);
                builder.setMessage("Server Error")
                        .setCancelable(true)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                androidx.appcompat.app.AlertDialog alert = builder.create();
                alert.show();
                Log.v("Volley requestid ", String.valueOf(requestId));
                Log.v("Volley Error", String.valueOf(error));
            }
        };
    }

    private void setAdapter() {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1, RecyclerView.VERTICAL, false);
        rvNotification.setLayoutManager(gridLayoutManager);
        notificationAdapter = new NotificationAdapter(getContext(), notifications);
        rvNotification.setAdapter(notificationAdapter);
    }
}
