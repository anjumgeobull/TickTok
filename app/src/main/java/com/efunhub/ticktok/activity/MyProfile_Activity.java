package com.efunhub.ticktok.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.efunhub.ticktok.R;
import com.efunhub.ticktok.fragments.AllVideoFragment;
import com.efunhub.ticktok.fragments.ProfileFragment;

public class MyProfile_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, new ProfileFragment());
        fragmentTransaction.commit();
    }
}