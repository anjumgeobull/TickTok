package com.efunhub.ticktok.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.efunhub.ticktok.R;
import com.efunhub.ticktok.application.SessionManager;
import com.efunhub.ticktok.model.UploadVideoModel;
import com.efunhub.ticktok.retrofit.APICallback;
import com.efunhub.ticktok.retrofit.AlertDialogs;
import com.efunhub.ticktok.retrofit.BaseServiceResponseModel;
import com.efunhub.ticktok.retrofit.PrintUtil;
import com.efunhub.ticktok.services.UploadVideoDraftSP;
import com.efunhub.ticktok.services.UploadVideoSP;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ShareVideoActivity extends AppCompatActivity implements View.OnClickListener{

    private final String KEY_VIDEO_URI = "video_uri";
    private String videoUri;
    private Button btnShare,btnTag,btnSave,btnBack;
    EditText edtVideoCaption;
    Toolbar toolbar;
    private CardView crdVideo;
    private ImageView imgVideo;
    private CheckBox cbPublic,cbPrivate;
    private String mVisibility ="";
    String mflagVariableImageEmpty="";
    Uri mImageCaptureUri;
    UploadVideoSP uploadVideoSP;
    UploadVideoDraftSP uploaddraftVideoSP;
    private AlertDialogs mAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_video);
        setUpToolbar();
        init();
    }

    private void init() {
        uploadVideoSP=new UploadVideoSP(this);
        uploaddraftVideoSP=new UploadVideoDraftSP(this);
        mAlert=AlertDialogs.getInstance();

        btnShare=findViewById(R.id.btnShare);
        btnBack=findViewById(R.id.btnBack);
        btnSave=findViewById(R.id.btnSave);
        crdVideo=findViewById(R.id.crdVideo);
        imgVideo=findViewById(R.id.imgVideo);
        cbPublic=findViewById(R.id.cbPublic);
        cbPrivate=findViewById(R.id.cbPrivate);
        edtVideoCaption=findViewById(R.id.edtVideoCaption);
        btnTag=findViewById(R.id.btnTag);

        crdVideo.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnTag.setOnClickListener(this);

        if (getIntent().hasExtra(KEY_VIDEO_URI)) {
            videoUri = getIntent().getStringExtra(KEY_VIDEO_URI);
            System.out.println("Video="+videoUri);
        }

        mImageCaptureUri= Uri.parse(videoUri);
        mflagVariableImageEmpty=mImageCaptureUri.getPath();

        cbPublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mVisibility="public";
                    cbPrivate.setChecked(false);
                }
                Log.d("mVisibility",mVisibility);
            }
        });

        cbPrivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                   mVisibility="private";
                    cbPublic.setChecked(false);
                }
                Log.d("mVisibility",mVisibility);
            }
        });

        setVideoThumbnail();
    }

    private void setVideoThumbnail() {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.isMemoryCacheable();
        Glide.with(this).setDefaultRequestOptions(requestOptions).load(videoUri).into(imgVideo);
    }

    private void setUpToolbar() {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Share");
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnShare:
                //call API
                if(mVisibility.isEmpty()){
                    Toast.makeText(ShareVideoActivity.this, "Choose privacy settings", Toast.LENGTH_SHORT).show();
                }else if(edtVideoCaption.getText().toString().length()==0) {
                    Toast.makeText(ShareVideoActivity.this, "Please add caption for video", Toast.LENGTH_SHORT).show();
                }else {
                    uploadVideo();
                }
                break;
            case R.id.btnBack:
                //call API
                finish();
                break;
            case R.id.btnSave:
                //call API
                if(mVisibility.isEmpty()){
                    Toast.makeText(ShareVideoActivity.this, "Choose privacy settings", Toast.LENGTH_SHORT).show();
                }else if(edtVideoCaption.getText().toString().length()==0) {
                    Toast.makeText(ShareVideoActivity.this, "Please add caption for video", Toast.LENGTH_SHORT).show();
                }else {
                    uploadVideotodraft();
                }
                break;
            case R.id.crdVideo:
                System.out.println("video_ur=>"+videoUri);
                startActivity(new Intent(ShareVideoActivity.this,PlayEditedVideoActivity.class).putExtra("video_uri",videoUri));
                break;
            case R.id.btnTag:
                   edtVideoCaption.setText(edtVideoCaption.getText().toString()+"#");
                   edtVideoCaption.setSelection(edtVideoCaption.getText().length());
                break;
        }
    }

    public void uploadVideo() {
        File sourceFile=null;
        try {
            RequestBody requestFile;
            MultipartBody.Part ImageCaptured;
            if (!"".equals(mflagVariableImageEmpty)) {
                sourceFile = new File(mflagVariableImageEmpty);

            }

            RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), SessionManager.onGetAutoCustomerId());
            RequestBody visibility = RequestBody.create(MediaType.parse("text/plain"), mVisibility);
            RequestBody video_caption=RequestBody.create(MediaType.parse("text/plain"),edtVideoCaption.getText().toString());

            requestFile = RequestBody.create(MediaType.parse("***/*//*//**//*"), sourceFile);

            ImageCaptured =
                    MultipartBody.Part.createFormData("video", sourceFile.getName(), requestFile);

            callVideoUploadAPI(user_id,visibility,video_caption,ImageCaptured);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void uploadVideotodraft() {
        File sourceFile=null;
        try {
            RequestBody requestFile;
            MultipartBody.Part ImageCaptured;
            if (!"".equals(mflagVariableImageEmpty)) {
                sourceFile = new File(mflagVariableImageEmpty);
            }

            RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), SessionManager.onGetAutoCustomerId());
            RequestBody visibility = RequestBody.create(MediaType.parse("text/plain"), mVisibility);
            RequestBody video_caption=RequestBody.create(MediaType.parse("text/plain"),edtVideoCaption.getText().toString());

            requestFile = RequestBody.create(MediaType.parse("***/*//*//**//*"), sourceFile);

            ImageCaptured =
                    MultipartBody.Part.createFormData("video", sourceFile.getName(), requestFile);

            callUploaddraftVideo(user_id,visibility,video_caption,ImageCaptured);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void callVideoUploadAPI(RequestBody user_id,RequestBody visibility,RequestBody video_caption, MultipartBody.Part imageCaptured) {

        mAlert.onShowProgressDialog(ShareVideoActivity.this, true);

        uploadVideoSP.callUploadVideo(user_id,visibility,video_caption,imageCaptured,new APICallback() {
                    @Override
                    public <T> void onSuccess(T serviceResponse) {
                        try {
                            int Status = ((UploadVideoModel) serviceResponse).getStatus();

                            String message = ((UploadVideoModel) serviceResponse).getMsg();
                            if (Status == 1) {
                                mAlert.onShowToastNotification(ShareVideoActivity.this, "Video shared successfully");
                                Intent i = new Intent(ShareVideoActivity.this, MainActivity.class);
                                // set the new task and clear flags
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            } else {
                                mAlert.onShowToastNotification(ShareVideoActivity.this, message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            mAlert.onShowProgressDialog(ShareVideoActivity.this, false);
                        }
                    }

                    @Override
                    public <T> void onFailure(T apiErrorModel, T extras) {
                        try {

                           // PrintUtil.showNetworkAvailableToast(ShareVideoActivity.this);
                            if (apiErrorModel != null) {
                                PrintUtil.showToast(ShareVideoActivity.this, ((BaseServiceResponseModel) apiErrorModel).getMsg());
                            } else {
                                PrintUtil.showNetworkAvailableToast(ShareVideoActivity.this);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            PrintUtil.showNetworkAvailableToast(ShareVideoActivity.this);
                        } finally {
                            mAlert.onShowProgressDialog(ShareVideoActivity.this, false);
                        }
                    }
                });
    }

    private void callUploaddraftVideo(RequestBody user_id,RequestBody visibility,RequestBody video_caption, MultipartBody.Part imageCaptured) {

        mAlert.onShowProgressDialog(ShareVideoActivity.this, true);

        uploaddraftVideoSP.callUploaddraftVideo(user_id,visibility,video_caption,imageCaptured,new APICallback() {
            @Override
            public <T> void onSuccess(T serviceResponse) {
                try {
                    int Status = ((UploadVideoModel) serviceResponse).getStatus();

                    String message = ((UploadVideoModel) serviceResponse).getMsg();
                    if (Status == 1) {
                        mAlert.onShowToastNotification(ShareVideoActivity.this, "Video saved successfully");
                        Intent i = new Intent(ShareVideoActivity.this, MainActivity.class);
                        // set the new task and clear flags
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    } else {
                        mAlert.onShowToastNotification(ShareVideoActivity.this, message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mAlert.onShowProgressDialog(ShareVideoActivity.this, false);
                }
            }

            @Override
            public <T> void onFailure(T apiErrorModel, T extras) {
                try {

                    if (apiErrorModel != null) {
                        PrintUtil.showToast(ShareVideoActivity.this, ((BaseServiceResponseModel) apiErrorModel).getMsg());
                    } else {
                        PrintUtil.showNetworkAvailableToast(ShareVideoActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //PrintUtil.showNetworkAvailableToast(ShareVideoActivity.this);
                } finally {
                    mAlert.onShowProgressDialog(ShareVideoActivity.this, false);
                }
            }
        });
    }


}
