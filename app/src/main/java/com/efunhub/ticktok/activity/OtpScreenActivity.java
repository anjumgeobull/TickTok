package com.efunhub.ticktok.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.efunhub.ticktok.R;

public class OtpScreenActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnConfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_screen);

        initData();
    }

    private void initData() {
        btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnConfirm:
                startActivity(new Intent(OtpScreenActivity.this, MainActivity.class));
                finish();
        }
    }
}
