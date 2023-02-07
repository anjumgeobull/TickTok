package com.efunhub.ticktok.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.efunhub.ticktok.R;
import com.efunhub.ticktok.adapter.ListViewChatAdapter;
import com.efunhub.ticktok.model.ChatModel;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    ListViewChatAdapter listViewChatAdapter;
    ArrayList<ChatModel> chatModelList = new ArrayList<>();
    RecyclerView rvChat;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initData();
        setUpToolbar();
    }

    private void setUpToolbar() {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Axonas jean");
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

    private void initData() {
        rvChat = findViewById(R.id.rvChat);

        chatModelList.add(new ChatModel(1, "Hi"));
        chatModelList.add(new ChatModel(2, "Hello"));
        chatModelList.add(new ChatModel(1, "How are you"));
        chatModelList.add(new ChatModel(2, "I am good what about you?"));
        chatModelList.add(new ChatModel(1, "Glad to here that. I am fine"));
        chatModelList.add(new ChatModel(1, "How are you"));
        chatModelList.add(new ChatModel(2, "I am good what about you?"));
        chatModelList.add(new ChatModel(2, "I am good what about you?"));
        chatModelList.add(new ChatModel(1, "Glad to here that. I am fine"));
        chatModelList.add(new ChatModel(1, "How are you"));
        chatModelList.add(new ChatModel(2, "I am good what about you?"));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1, RecyclerView.VERTICAL, false);
        rvChat.setLayoutManager(gridLayoutManager);
        listViewChatAdapter = new ListViewChatAdapter(getApplicationContext(), chatModelList);
        rvChat.setAdapter(listViewChatAdapter);
    }

}
