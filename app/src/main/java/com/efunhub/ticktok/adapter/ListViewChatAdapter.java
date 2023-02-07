package com.efunhub.ticktok.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.efunhub.ticktok.R;
import com.efunhub.ticktok.model.ChatModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListViewChatAdapter extends RecyclerView.Adapter<ListViewChatAdapter.ViewHolder> {

    ArrayList<ChatModel> strings = new ArrayList<>();
    Context activity;
    private LayoutInflater mInflater;

    public ListViewChatAdapter(Context context, ArrayList<ChatModel> videoList) {
        this.activity = context;
        this.mInflater = LayoutInflater.from(context);
        this.strings = videoList;
    }

    @NonNull
    @Override
    public ListViewChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.chat_list_adapter, parent, false);
        return new ListViewChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListViewChatAdapter.ViewHolder holder, int position) {

        if (strings.get(position).getFlag().equals(1)) {
            holder.txtLeft.setVisibility(View.VISIBLE);
            holder.txtLeft.setText(strings.get(position).getMsg());
        }
        if (strings.get(position).getFlag().equals(2)) {
            holder.txtRight.setVisibility(View.VISIBLE);
            holder.txtRight.setText(strings.get(position).getMsg());
        }
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtLeft, txtRight;
        RelativeLayout relativeMy, relativeTheir;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtLeft = itemView.findViewById(R.id.Left);
            txtRight = itemView.findViewById(R.id.Right);
        }
    }
}
