package com.efunhub.ticktok.activity;

import static com.efunhub.ticktok.retrofit.Constant.SERVER_URL;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chaos.view.PinView;
import com.efunhub.ticktok.R;
import com.efunhub.ticktok.utility.DialogsUtils;
import com.efunhub.ticktok.utility.VolleySingleton;
import com.google.firebase.messaging.FirebaseMessaging;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {
    Spinner spi_Country;
    EditText et_username, et_usermobile, et_user_email;
    ShowHidePasswordEditText et_userPassword;
    Button btnSubmit;
    String token = "";
    ProgressDialog progressDialog;
    TextView tv_login_layout;
    String registration = "registration";
    String verify_OTP = "verify-otp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        generateToken();
        initData();

    }

    private void initData() {
        spi_Country = findViewById(R.id.spi_Country);
        et_username = findViewById(R.id.et_username);
        et_userPassword = findViewById(R.id.et_userPassword);
        et_usermobile = findViewById(R.id.et_usermobile);
        et_user_email = findViewById(R.id.et_user_email);
        tv_login_layout = findViewById(R.id.tv_login_layout);
        tv_login_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Registration.this, LoginActivity.class));
            }
        });
        progressDialog = new ProgressDialog(Registration.this);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validation();

            }
        });


    }

    private void Validation() {
        if (et_username.getText().toString().isEmpty()) {
            et_username.setError("Enter the name");
        } else if (et_userPassword.getText().toString().isEmpty()) {
            et_userPassword.setError("Enter the password");
        } else if (et_usermobile.getText().toString().isEmpty() || et_usermobile.getText().toString().length() < 10) {
            et_usermobile.setError("Enter the valid mobile number");
        } else if (et_user_email.getText().toString().isEmpty()) {
            et_user_email.setError("Enter the email");
        } else if (spi_Country.getSelectedItem().toString().equals("Select Country")) {
            Toast.makeText(Registration.this, "Select Country name", Toast.LENGTH_SHORT).show();
        } else {
            Register_User(et_username.getText().toString(), et_userPassword.getText().toString(), et_user_email.getText().toString(), et_usermobile.getText().toString(), spi_Country.getSelectedItem().toString());
        }
    }


    private void Register_User(String name, String password, String email, String mobile, String country) {
        //Toast.makeText(Registration.this, "Like_Video_Data=>"+name+" "+password+" "+email+" "+mobile+" "+counntry, Toast.LENGTH_SHORT).show();
        System.out.println("Data=> params " + name + " " + password + " " + email + " " + mobile + " " + country);
        String Urls = SERVER_URL + registration;
        progressDialog = DialogsUtils.showProgressDialog(Registration.this, "Please Wait");
        progressDialog.setCancelable(false);
        StringRequest postRequest = new StringRequest(Request.Method.POST, Urls, response -> {
                    // response
                    Log.d("Response", response);
                    System.out.println("Data=> responce " + response);
                    //progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg = jsonObject.getString("msg");
                        int status = jsonObject.getInt("status");

                        System.out.println("Data=> status " + status);

                        if (status == 1) {
                            progressDialog.dismiss();
                            Toast.makeText(Registration.this, "User Register Successfully", Toast.LENGTH_SHORT).show();
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            System.out.println("JSonarray =" + jsonArray);
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                String otp = jsonObject1.getString("otp");
                                Show_Otp_Dialouge(et_usermobile.getText().toString(), otp);
                            }

                        } else if (status == 0) {
                            progressDialog.dismiss();
                            Toast.makeText(Registration.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.dismiss();

                            Toast.makeText(Registration.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_LONG).show();
                    }

                },
                error -> {
                    progressDialog.dismiss();
                    // error
                    Log.d("Error.Response", String.valueOf(error));
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("name", name);
                params.put("password", password);
                params.put("email", email);
                params.put("mobile", mobile);
                params.put("country", country);
                params.put("token", token);
                System.out.println("params"+params);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(postRequest);
    }

    private void Show_Otp_Dialouge(String mobile, String otp) {
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.otp_layout);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);

        PinView pin_view = dialog.findViewById(R.id.pin_view);
        pin_view.setText(otp);
        Button btn_submit = dialog.findViewById(R.id.btn_verifyOtp);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Validate_Otp(mobile, pin_view.getText().toString());
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }


    private void Validate_Otp(String mobile, String otp) {
        String Urls = SERVER_URL + verify_OTP;
        progressDialog = DialogsUtils.showProgressDialog(Registration.this, "Please Wait");
        progressDialog.setCancelable(false);
        StringRequest postRequest = new StringRequest(Request.Method.POST, Urls, response -> {
            // response
            Log.d("Response", response);
            //progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                String msg = jsonObject.getString("msg");

                if (jsonObject.getString("status").contains("1")) {
                    progressDialog.dismiss();
                    Toast.makeText(Registration.this, msg, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Registration.this, LoginActivity.class));


                } else if (jsonObject.getString("status").contains("0")) {
                    progressDialog.dismiss();
                    Toast.makeText(Registration.this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();

                    Toast.makeText(Registration.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                progressDialog.dismiss();
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_LONG).show();
            }

        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        // error
                        Log.d("Error.Response", String.valueOf(error));
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("mobile", mobile);
                params.put("otp", otp);

                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(postRequest);
    }

    public void generateToken() {

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                return;
            }
            token = task.getResult();
        });

    }
}