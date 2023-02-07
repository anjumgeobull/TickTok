package com.efunhub.ticktok.activity;

import static com.efunhub.ticktok.retrofit.Constant.SERVER_URL;
import static com.efunhub.ticktok.utility.ConstantVariables.USER_FORGOT_PASSWORD;
import static com.efunhub.ticktok.utility.ConstantVariables.USER_PROFILE;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.efunhub.ticktok.R;
import com.efunhub.ticktok.application.SessionManager;
import com.efunhub.ticktok.interfaces.IResult;
import com.efunhub.ticktok.utility.VolleyService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgetPass extends AppCompatActivity implements View.OnClickListener{

    EditText edtEmail;
    Button btnSend;
    private SessionManager sessionManager;
   // private ToastClass toastClass;
    // private CustomProgressDialog customProgressDialog;
//    private CheckConnectivity checkConnectivity;
   private VolleyService mVolleyService;
    private IResult mResultCallBack;
    private String userForgotURL = "Forgot-Password";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);
        initdata();
        addEvent();
    }

    private void addEvent() {
        btnSend.setOnClickListener(this);

    }

    private void initdata() {
        sessionManager = new SessionManager();
        edtEmail = findViewById(R.id.mailid);
        btnSend = findViewById(R.id.btnSend);
//        customProgressDialog = new CustomProgressDialog();
//        checkConnectivity = new CheckConnectivity();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnSend:
                if (checkValid()) {
                    //customProgressDialog.showProgress(this, "Please wait...", true);
                    sendMailId();
                }

                break;
        }

    }

    private void sendMailId() {
        initVolleyCallback();
        mVolleyService = new VolleyService(mResultCallBack, this);

        Map<String, String> params = new HashMap<>();
        params.put("email", edtEmail.getText().toString());

        mVolleyService.postDataVolleyParameters(USER_FORGOT_PASSWORD, SERVER_URL + userForgotURL, params);


    }

    private void initVolleyCallback() {
        mResultCallBack = new IResult() {

            @Override
            public void notifySuccess(int requestId, String response) {
                switch (requestId) {
                    case USER_FORGOT_PASSWORD:
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            System.out.println("status=>"+response);
                            String status = jsonObject.getString("status");
                            String msg = jsonObject.getString("msg");
                            if (Integer.parseInt(status) == 1) {
                                Toast.makeText(ForgetPass.this, "New password is send to your registered email id", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(ForgetPass.this,MainActivity.class));
                                Intent intent1 = new Intent(ForgetPass.this, LoginActivity.class);
                                startActivity(intent1);
                                finish();

                            } else if (Integer.parseInt(status) == 2) {
                                Toast.makeText(ForgetPass.this, msg, Toast.LENGTH_SHORT).show();

                            }else if (Integer.parseInt(status) == 0){
                                Toast.makeText(ForgetPass.this, "Sorry, an account not exist with this email", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(ForgetPass.this, "Something went wrong. Please try again later!!!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {

                            e.printStackTrace();

                        }


                        break;
                }
            }

            @Override
            public void notifyError(int requestId, VolleyError error) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPass.this);
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

    private boolean checkValid() {
        String emailidPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        if (edtEmail.getText().toString().equalsIgnoreCase("")) {
            edtEmail.setError("Please enter Email Id");
            return false;
        }  else if (!edtEmail.getText().toString().matches(emailidPattern)) {
            edtEmail.setError("Please enter valid Email Id");
            Toast.makeText(this, "Please enter valid password", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }
}