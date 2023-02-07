package com.efunhub.ticktok.adapter;

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
import com.efunhub.ticktok.model.Search_User_Model;

import java.util.ArrayList;
import java.util.List;


public class Search_Adapter extends RecyclerView.Adapter<Search_Adapter.Holder>{

    List<Search_User_Model> search_user_modelList ;
    LayoutInflater inflater;
    onItemClick_Listener onItemClick_listener;


    public Search_Adapter(Context context,List<Search_User_Model> search_user_modelList, onItemClick_Listener onItemClick_listener) {
        this.inflater = LayoutInflater.from(context);
        this.search_user_modelList = search_user_modelList;
        this.onItemClick_listener = onItemClick_listener;

    }

    @NonNull
    @Override
    public Search_Adapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.followlist_layout, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Search_Adapter.Holder holder, int position) {
        holder.tv_follower_name.setText(search_user_modelList.get(position).getName());
        holder.tv_follower_country.setText("@"+search_user_modelList.get(position).getCountry());
    }

    @Override
    public int getItemCount() {
        return search_user_modelList.size();
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
