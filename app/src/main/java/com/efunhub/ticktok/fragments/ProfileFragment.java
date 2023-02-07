package com.efunhub.ticktok.fragments;

import static android.app.Activity.RESULT_OK;
import static com.efunhub.ticktok.retrofit.Constant.SERVER_URL;
import static com.efunhub.ticktok.utility.ConstantVariables.DELETE_VIDEO;
import static com.efunhub.ticktok.utility.ConstantVariables.USER_PROFILE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.efunhub.ticktok.R;
import com.efunhub.ticktok.activity.LoginActivity;
import com.efunhub.ticktok.adapter.PlayVideoActivity;
import com.efunhub.ticktok.adapter.PostAdapterNew;
import com.efunhub.ticktok.application.SessionManager;
import com.efunhub.ticktok.interfaces.IResult;
import com.efunhub.ticktok.interfaces.delete_Listener;
import com.efunhub.ticktok.interfaces.onItemClick_Listener;
import com.efunhub.ticktok.model.ProfileModel;
import com.efunhub.ticktok.model.User_Profile_Model.Post;
import com.efunhub.ticktok.model.User_Profile_Model.UserProfile;
import com.efunhub.ticktok.retrofit.APICallback;
import com.efunhub.ticktok.retrofit.AlertDialogs;
import com.efunhub.ticktok.retrofit.BaseServiceResponseModel;
import com.efunhub.ticktok.retrofit.PrintUtil;
import com.efunhub.ticktok.services.GetProfileSP;
import com.efunhub.ticktok.services.UpdateProfileSP;
import com.efunhub.ticktok.utility.VolleyService;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class ProfileFragment extends Fragment implements View.OnClickListener, onItemClick_Listener, delete_Listener {

    View view;
    RecyclerView rvPosts;
    PostAdapterNew videoAdapter;
    Toolbar toolbar;
    Button btnEditProfile, btnLogout;

    ImageView img_user;
    FloatingActionButton fab;

    BottomSheetDialog bottomSheetDialog;
    private AlertDialogs mAlert;
    private GetProfileSP mGetProfileSP;
    private UpdateProfileSP mUpdateProfileSP;
    ProfileModel.User_profile mProfileData;
    String Profile_User = "get_myprofile";
    String Delete_Video = "video-delete";
    private int mYear, mMonth, mDay;
    EditText edtMobileNo,edtName,edtMail;
    TextView tvGender,tvDateofBirth;
    String mGender="";
    String tempGender="";
    String mDateOfBirth="";
    ProgressDialog progressDialog;
    TextView tv_no_result,tv_post,tv_follow,tv_following,tv_name,tv_user_country,tv_points;
    ArrayList<UserProfile> userProfileModel_List = new ArrayList<>();
    ArrayList<Post> postList = new ArrayList<>();
    private VolleyService mVolleyService;
    private IResult mResultCallBack = null;
    String  id;
    ShimmerFrameLayout shimmer_view_container;
    LinearLayout profile_layout;

    //upload image
    String mflagVariableImageEmpty="";
    private String mVisibility ="";


    public ProfileFragment() {
        // Required empty public constructor
    }
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        initData();
        getUser_Profile();
        setUpToolbar();
        //callGetProfile(SessionManager.onGetAutoCustomerId(),"No");
        //setUpRecycler();
        return view;
    }

    private void getUser_Profile() {
        userProfileModel_List.clear();
//        progressDialog.show();
        shimmer_view_container.startShimmerAnimation();
        initVolleyCallback();
        mVolleyService = new VolleyService(mResultCallBack, getActivity());

        Map<String, String> params = new HashMap<>();
//        params.put("id", following_user_id);
        params.put("user_id", id);
//        params.put("video_user_id", following_user_id);

        System.out.println("params=>" + params.toString());
        mVolleyService.postDataVolleyParameters(USER_PROFILE, SERVER_URL + Profile_User, params);

    }

    private void delete_Video(String videoid,String following_user_id) {
        shimmer_view_container.startShimmerAnimation();
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.show();
        initVolleyCallback();
        mVolleyService = new VolleyService(mResultCallBack, getActivity());

        Map<String, String> params = new HashMap<>();
        params.put("user_auto_id", following_user_id);
        params.put("video_auto_id", videoid);

        System.out.println("params=>" + params.toString());
        mVolleyService.postDataVolleyParameters(DELETE_VIDEO, SERVER_URL + Delete_Video, params);

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
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
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
                                    if (userProfileModel_List.get(0).getTotalFollowers() != null) {

                                        //btnFollow.setText("Follow");
                                        tv_follow.setText(String.valueOf(userProfileModel_List.get(0).getTotalFollowers()));

                                    }
                                   if (userProfileModel_List.get(0).getTotalFollowings()!=null) {
                                       tv_follow.setText(String.valueOf(userProfileModel_List.get(0).getTotalFollowings()));
                                    }
                                    tv_post.setText(String.valueOf(userProfileModel_List.get(0).getTotalPosts()));
                                    tv_name.setText(userProfileModel_List.get(0).getName());
                                    tv_user_country.setText("@"+userProfileModel_List.get(0).getCountry());
                                    if(userProfileModel_List.get(0).getPoint().equals(""))
                                    {
                                        tv_points.setText(String.valueOf("Points : 0"));
                                    }else {
                                        tv_points.setText("Points :"+String.valueOf(userProfileModel_List.get(0).getPoint()));
                                    }
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

                    case DELETE_VIDEO:
                        try {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }

                            jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            String status = jsonObject.getString("status");


                            if (Integer.parseInt(status) == 0) {


                            } else if (Integer.parseInt(status) == 1) {

                                rvPosts.notify();
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
//                shimmer_view_container.stopShimmerAnimation();
//                shimmer_view_container.setVisibility(View.GONE);
//                profile_layout.setVisibility(View.VISIBLE);
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

    private void setUpToolbar() {

        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).setTitle("Profile");
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

    private void setUpRecycler() {
        if (!postList.isEmpty())
        {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3, RecyclerView.VERTICAL, false);
            rvPosts.setLayoutManager(gridLayoutManager);
            videoAdapter = new PostAdapterNew(getActivity(), postList, this,this);
            rvPosts.setAdapter(videoAdapter);
        }
        else {
            rvPosts.setVisibility(View.GONE);
            tv_no_result.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {
        mUpdateProfileSP=new UpdateProfileSP(getActivity());
        mGetProfileSP=new GetProfileSP(getActivity());
        mAlert=AlertDialogs.getInstance();
        id = SessionManager.onGetAutoCustomerId();
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        profile_layout = view.findViewById(R.id.profile_layout);
        btnLogout = view.findViewById(R.id.btnLogout);
        shimmer_view_container = view.findViewById(R.id.shimmer_view_container);
        rvPosts = view.findViewById(R.id.rvPosts);
        tv_no_result = view.findViewById(R.id.tv_no_result);
        tv_name = view.findViewById(R.id.profile_name);
        tv_user_country = view.findViewById(R.id.tv_user_country);
        tv_follow = view.findViewById(R.id.tv_followers);
        tv_following = view.findViewById(R.id.tv_following);
        tv_post = view.findViewById(R.id.tv_posts);
        tv_points = view.findViewById(R.id.tv_bio);
        btnEditProfile.setOnClickListener((View.OnClickListener) this);
        btnLogout.setOnClickListener((View.OnClickListener) this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEditProfile:
                OpenBottomSheet();
                break;
            case R.id.btnLogout:
                showLogoutAlert();
                break;

           }
    }

    @SuppressLint("MissingInflatedId")
    private void OpenBottomSheet() {
        bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        View sheetView = getLayoutInflater().inflate(R.layout.edit_profile_bottomsheet, null);
        bottomSheetDialog.setContentView(sheetView);

        ImageView imgClose = sheetView.findViewById(R.id.imgClose);
//        LinearLayout llSelectGender=sheetView.findViewById(R.id.llSelectGender);
//        LinearLayout llSelectDateOfBirth=sheetView.findViewById(R.id.llSelectDateOfBirth);
        TextView tvSaveProfile=sheetView.findViewById(R.id.tvSaveProfile);
         edtName=sheetView.findViewById(R.id.edtName);
        edtMail=sheetView.findViewById(R.id.edtMailId);
         //edtBio=sheetView.findViewById(R.id.edtBio);
          edtMobileNo=sheetView.findViewById(R.id.edtMobileNo);

          img_user=sheetView.findViewById(R.id.imgUser);
          fab=sheetView.findViewById(R.id.floatingbutton);

          fab.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  ImagePicker.Companion.with(getActivity())
                          .crop()
                          .cropOval()
                          .maxResultSize(512, 512, true)
                          .provider(ImageProvider.BOTH) //Or bothCameraGallery()
                          .createIntentFromDialog((Function1) (new Function1() {
                              public Object invoke(Object var1) {
                                  this.invoke((Intent) var1);
                                  return Unit.INSTANCE;
                              }

                              public final void invoke(@NotNull Intent it) {
                                  Intrinsics.checkNotNullParameter(it, "it");
                                  launcher.launch(it);
                              }
                          }));

              }
          });




        //tvGender=sheetView.findViewById(R.id.tvGender);
//         tvDateofBirth=sheetView.findViewById(R.id.tvDateofBirth);

         setProfileDate();

//        llSelectDateOfBirth.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                datepicker();
//            }
//        });
//        llSelectGender.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectGender();
//            }
//        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        tvSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String namePattern = "^[a-zA-Z -]+$";
               // String mailPattern= "/^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$/";
                if(edtName.length()==0){
                    edtName.setError("Please enter name");
              }else if(!edtName.getText().toString().matches(namePattern)) {
                    edtName.setError("Please enter valid name");
              }
                else if(edtMail.length()==0){
                   edtMail.setError("Please enter Email Id");
               }
//                else if(!edtMail.getText().toString().matches(mailPattern)) {
//                   edtMail.setError("Please enter valid Email Id");
//               }
                else if(edtMobileNo.length()==0){
                    edtMobileNo.setError("Please enter mobile number");
               }else if(edtMobileNo.length()<10){
                    edtMobileNo.setError("Mobile number must be at least 10 digit");
             }
               else {
                    UpdateProfileApi(SessionManager.onGetAutoCustomerId(),edtMobileNo.getText().toString(),edtName.getText().toString(),edtMail.getText().toString(),"","","");

                    System.out.println("INFO");
                    System.out.println("ID=="+SessionManager.onGetAutoCustomerId());
                    System.out.println("Name=="+edtName.getText().toString());
                    System.out.println("Mobile no=="+edtMobileNo.getText().toString());
                   System.out.println("Email id=="+edtMail.getText().toString());


                   mAlert.onShowProgressDialog(getActivity(), false);
                   callGetProfile(SessionManager.onGetAutoCustomerId(),"Yes");

                }
           }
        });

        bottomSheetDialog.show();
    }



    private void setProfileDate() {

        edtMobileNo.setText(userProfileModel_List.get(0).getMobile());

        if (!userProfileModel_List.get(0).getEmail().isEmpty()) {
            edtMail.setText(userProfileModel_List.get(0).getEmail());
        }

        if(!userProfileModel_List.get(0).getName().isEmpty()){
            edtName.setText(userProfileModel_List.get(0).getName());
        }

      //  img_user.setImageURI(userProfileModel_List.get(0).getUri());

       // Toast.makeText(getActivity(),"Profile update succesfully",Toast.LENGTH_SHORT).show();

//        if(!userProfileModel_List.get(0).getisEmpty()){
//            edtBio.setText(mProfileData.getName());
//        }

//        if(!mProfileData.getGender().isEmpty()){
//            tvGender.setText(mProfileData.getGender());
//        }
//
//        if(!mProfileData.getDob().isEmpty()){
//            tvDateofBirth.setText(mProfileData.getDob());
//        }

    }

    private void selectGender(){
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.gender_layout);
        dialog.setCancelable(true);
        // there are a lot of settings, for dialog, check them all out!
        // set up radiobutton
        RadioGroup radioGroupGender = (RadioGroup) dialog.findViewById(R.id.groupradioGender);
        Button btnSubmit=(Button)dialog.findViewById(R.id.btnSubmit);

       // RadioButton rd2 = (RadioButton) dialog.findViewById(R.id.rd_2);

        // now that the dialog is set up, it's time to show it

        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group1, int checkedId1) {
                switch (checkedId1) {
                    case R.id.radio_male://radiobuttonID
                        //do what you want
                        tempGender="Male";
                        break;
                    case R.id.radio_female://radiobuttonID
                        //do what you want
                        tempGender="Female";
                        break;
                }
            }
        });



        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGender=tempGender;
                tvGender.setText(mGender);
                dialog.dismiss();
            }
        });

        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // ((MainActivity)getActivity()).clearBackStackInclusive("tag"); // tag (addToBackStack tag) should be the same which was used while transacting the F2 fragment
    }

    public void showLogoutAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog.setTitle("");

        // Setting Dialog Message
        alertDialog.setMessage("Do you want to logout?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SessionManager.onSaveLoginDetails("", "", "", "", "","","","");

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    private void callGetProfile(String user_auto_id,String flag) {

        mAlert.onShowProgressDialog(getActivity(), true);
        mGetProfileSP.CallGetProfile(user_auto_id,new APICallback() {
            @Override
            public <T> void onSuccess(T serviceResponse) {
                int Status = ((ProfileModel) serviceResponse).getStatus();
                mProfileData = ((ProfileModel) serviceResponse).getUser_profile();
                try {
                    String message = ((ProfileModel) serviceResponse).getMsg();
                    if (Status == 1) {

                       // Toast.makeText(getActivity(), "get profile", Toast.LENGTH_SHORT).show();
                        if(flag.equalsIgnoreCase("Yes"))
                        {
                            setProfileDate();
                        }

                    } else {

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


    private void datepicker(){


            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            tvDateofBirth.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            mDateOfBirth=dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;


                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
    }

    private void UpdateProfileApi(String user_auto_id, String mobile_number, String name, String mail_id, String s, String s1, String s2) {

        mAlert.onShowProgressDialog(getActivity(), true);

        mUpdateProfileSP.CallUpdateProfile(user_auto_id,mobile_number,name,mail_id,new APICallback() {
            @Override
            public <T> void onSuccess(T serviceResponse) {
                int Status = ((BaseServiceResponseModel) serviceResponse).getStatus();
              //  mProfileData = ((ProfileModel) serviceResponse).getUser_profile();
                try {
                    String message = ((BaseServiceResponseModel) serviceResponse).getMsg();
                    if (Status == 1) {

                        System.out.println("ST="+Status);

                        Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();

                        mAlert.onShowProgressDialog(getActivity(), false);
                        callGetProfile(SessionManager.onGetAutoCustomerId(),"Yes");
                    } else {

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
    public void Item_ClickListner(int position) {
        ArrayList<Post> selected_postList = new ArrayList<>();
        selected_postList.add(postList.get(position));
        Intent i = new Intent(getActivity(), PlayVideoActivity.class);
        i.putParcelableArrayListExtra("video_list", selected_postList);
        startActivity(i);
    }

    @Override
    public void delete_click(int position) {
        delete_Video(postList.get(position).getId(),postList.get(position).getUserId());
    }

    ActivityResultLauncher<Intent> launcher=
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),(ActivityResult result)->{
                if(result.getResultCode()==RESULT_OK){
                    Uri uri=result.getData().getData();
                    img_user.setImageURI(uri);
                    // Use the uri to load the image
                }else if(result.getResultCode()==ImagePicker.RESULT_ERROR){
                    // Use ImagePicker.Companion.getError(result.getData()) to show an error

                }
            });



}
