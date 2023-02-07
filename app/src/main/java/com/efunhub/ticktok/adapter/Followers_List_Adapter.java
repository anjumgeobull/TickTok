package com.efunhub.ticktok.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.efunhub.ticktok.R;
import com.efunhub.ticktok.interfaces.onItemClick_Listener;
import com.efunhub.ticktok.model.User_Follower_Model.FollowData;
import com.efunhub.ticktok.model.User_Follower_Model.FollowersDetail;
import com.efunhub.ticktok.model.User_Profile_Model.Follow_List;

import java.util.List;

public class Followers_List_Adapter extends RecyclerView.Adapter<Followers_List_Adapter.Holder>{
    LayoutInflater inflater;
    List<FollowersDetail> follow_list;
    onItemClick_Listener onItemClick_listener;


    public Followers_List_Adapter(Context context, List<FollowersDetail> follow_list, onItemClick_Listener onItemClick_listener) {
        this.inflater = LayoutInflater.from(context);
        this.follow_list = follow_list;
        this.onItemClick_listener = onItemClick_listener;
    }

    @NonNull
    @Override
    public Followers_List_Adapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.followlist_layout, parent, false);
        return new Holder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Followers_List_Adapter.Holder holder, int position) {
            holder.tv_follower_name.setText(follow_list.get(position).getName());
            holder.tv_follower_country.setText("@"+follow_list.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return follow_list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView tv_follower_name, tv_follower_country;
        Button btn_follow;
        public Holder(@NonNull View itemView) {
            super(itemView);
            tv_follower_name = itemView.findViewById(R.id.tv_follower_name);
            tv_follower_country = itemView.findViewById(R.id.tv_follower_country);
            btn_follow = itemView.findViewById(R.id.btn_follow);
            btn_follow.setVisibility(View.GONE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClick_listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClick_listener.Item_ClickListner(position);
                        }
                    }
                }
            });

        }
    }
}
