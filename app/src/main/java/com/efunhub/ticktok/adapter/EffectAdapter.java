package com.efunhub.ticktok.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.efunhub.ticktok.R;
import com.efunhub.ticktok.effect.FilterType;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EffectAdapter extends RecyclerView.Adapter<EffectAdapter.ViewHolder> {

    List<FilterType> objects;
    Activity activity;
    private LayoutInflater mInflater;
    SelectEffectInterface selectEffectInterface;

    public EffectAdapter(Activity activity, List<FilterType> objects) {
        this.activity = activity;
        this.mInflater = LayoutInflater.from(activity);
        this.objects = objects;
        this.selectEffectInterface= (SelectEffectInterface) activity;
    }

    @NonNull
    @Override
    public EffectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.effect_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EffectAdapter.ViewHolder holder, int position) {

    //    holder.videoView.setVideoPath((strings.get(position)));
      //  holder.videoView.pause();

       // holder.videoView.setImageResource(R.drawable.filter);
        holder.txtEffectName.setText(objects.get(position).name());
        holder.imgEffect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEffectInterface.selectEffect(objects.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgEffect;
        TextView txtEffectName;
        ImageView imgPlay,imgPause,imgWishlisted,imgWishlist,imgProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgEffect = itemView.findViewById(R.id.imgEffect);
            txtEffectName = itemView.findViewById(R.id.txtEffectName);

        }
    }

    public interface SelectEffectInterface{
        public void selectEffect(FilterType filterType);
    }

}

