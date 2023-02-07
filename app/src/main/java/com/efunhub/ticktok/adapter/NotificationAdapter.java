package com.efunhub.ticktok.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.efunhub.ticktok.R;
import com.efunhub.ticktok.model.Notification_Model;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    ArrayList<Notification_Model.NotificationList> notification_list = new ArrayList<>();
    Context activity;
    private LayoutInflater mInflater;

    public NotificationAdapter(Context context, ArrayList<Notification_Model.NotificationList> notification_list) {
        this.activity = context;
        this.mInflater = LayoutInflater.from(context);
        this.notification_list = notification_list;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.notification_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationAdapter.ViewHolder holder, int position) {

        holder.txtNameNotification.setText(notification_list.get(position).title);
        holder.txt_descriptionName.setText(notification_list.get(position).message);
        holder.txt_time.setText((notification_list.get(position).created_at));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //  activity.startActivity(new Intent(activity, HomeFragment.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return notification_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtNameNotification,txt_descriptionName,txt_time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNameNotification = itemView.findViewById(R.id.txtNameNotification);
            txt_descriptionName = itemView.findViewById(R.id.txtdescriptionName);
            txt_time = itemView.findViewById(R.id.txtTime);

        }
    }
}
