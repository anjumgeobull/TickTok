package com.efunhub.ticktok.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.efunhub.ticktok.R;
import com.efunhub.ticktok.application.SessionManager;
import com.efunhub.ticktok.model.RegistrationModel;
import com.efunhub.ticktok.retrofit.APICallback;
import com.efunhub.ticktok.retrofit.AlertDialogs;
import com.efunhub.ticktok.retrofit.BaseServiceResponseModel;
import com.efunhub.ticktok.retrofit.PrintUtil;
import com.efunhub.ticktok.services.RegistrationServiceProvider;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mainmobile, mainpassword;
    Button btnSend;
    private AlertDialogs mAlert;
    TextView tv_register,tv_forgot_pass;
    private RegistrationServiceProvider registrationServiceProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initData();

    }

    private void initData() {
        registrationServiceProvider=new RegistrationServiceProvider(this);
        mAlert=AlertDialogs.getInstance();
        tv_register = findViewById(R.id.tv_register);
        tv_forgot_pass=findViewById(R.id.tv_forgot_pasword);
        tv_register.setOnClickListener(this);
        tv_forgot_pass.setOnClickListener(this);
        mainmobile=findViewById(R.id.mainmobile);
        mainpassword = findViewById(R.id.mainpassword);
        btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSend: //startActivity(new Intent(LoginActivity.this,OtpScreenActivity.class));
                //finish();
                if (mainmobile.getText().toString().length() == 0) {
                    Toast.makeText(this, "Please enter mobile no", Toast.LENGTH_SHORT).show();
                } else if (mainmobile.getText().toString().length() < 10) {
                    Toast.makeText(this, "Please enter valid mobile no", Toast.LENGTH_SHORT).show();
                } else if (mainpassword.getText().toString().length() == 0) {
                    Toast.makeText(this, "Please enter valid password", Toast.LENGTH_SHORT).show();
                } else {
                    callRegistrationApi(mainmobile.getText().toString(), mainpassword.getText().toString());
                }
                break;
            case R.id.tv_forgot_pasword:
                Intent intent1 = new Intent(LoginActivity.this, ForgetPass.class);
                startActivity(intent1);
                break;
            case R.id.tv_register:
                startActivity(new Intent(LoginActivity.this, Registration.class));
                break;
        }
    }

    private void callRegistrationApi(String lMobileNumber, String password) {

        mAlert.onShowProgressDialog(LoginActivity.this, true);
        registrationServiceProvider.CallRegistration(lMobileNumber,password,new APICallback() {
            @Override
            public <T> void onSuccess(T serviceResponse) {
                int Status = ((RegistrationModel) serviceResponse).getStatus();
                ArrayList<RegistrationModel.Data>  mRegData = ((RegistrationModel) serviceResponse).getData();
                try {
                    String message = ((RegistrationModel) serviceResponse).getMsg();
                    if (Status == 1) {
                        //save details to sharedpreference
                        SessionManager.onSaveLoginDetails(mRegData.get(0).get_id(),mRegData.get(0).getName(), mRegData.get(0).getMobile(), mRegData.get(0).getEmail(), mRegData.get(0).getCountry(), mRegData.get(0).getOtp_status(), mRegData.get(0).getStatus(), mRegData.get(0).getToken());
                        mAlert.onShowToastNotification(LoginActivity.this,"Login Successfully") ;
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    } else if(Status == 0){

                        mAlert.onShowToastNotification(LoginActivity.this,"Invalid login credentials ") ;

                    }
                    else if(Status == 4){

                        mAlert.onShowToastNotification(LoginActivity.this,"Sorry, an account not exist with this contact") ;

                    }
                    else {

                        mAlert.onShowToastNotification(LoginActivity.this,"Something, went wrong") ;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mAlert.onShowProgressDialog(LoginActivity.this, false);
                }
            }


            @Override
            public <T> void onFailure(T apiErrorModel, T extras) {
                try {

                   // PrintUtil.showNetworkAvailableToast(LoginActivity.this);
                    if (apiErrorModel != null) {
                        PrintUtil.showToast(LoginActivity.this, ((BaseServiceResponseModel) apiErrorModel).getMsg());
                    } else {
                        PrintUtil.showNetworkAvailableToast(LoginActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    PrintUtil.showNetworkAvailableToast(LoginActivity.this);
                } finally {
                    mAlert.onShowProgressDialog(LoginActivity.this, false);
                }
            }
        });
    }
}
