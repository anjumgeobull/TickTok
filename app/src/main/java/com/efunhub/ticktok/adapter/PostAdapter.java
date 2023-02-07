package com.efunhub.ticktok.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.efunhub.ticktok.R;
import com.efunhub.ticktok.interfaces.delete_Listener;
import com.efunhub.ticktok.interfaces.onItemClick_Listener;
import com.efunhub.ticktok.model.User_Profile_Model.Post;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Holder> {

    ArrayList<Post> postList= new ArrayList<>();
    Context activity;
    onItemClick_Listener onItemClickListener;
    private LayoutInflater mInflater;


    public PostAdapter(Context context, ArrayList<Post> postList, onItemClick_Listener onItemClickListener) {
        this.activity = context;
        this.mInflater = LayoutInflater.from(context);
        this.postList = postList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.posts_list_adapter, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                System.out.println(" video"+postList.size());
//                //selected_postList.add(postList.get(position));
//                System.out.println("selected video"+postList.get(position).getVideo());
//                activity.startActivity(new Intent(activity,PlayVideoActivity.class).putExtra("video_list", postList.get(position)));
               onItemClickListener.Item_ClickListner(position);
//                System.out.println("Video url"+postList.get(position).getVideo());
            }
        });

//        holder.img_delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                delete_Listener.delete_click(position);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        ImageView videoView;

        ImageView imgPlay,imgPause,imgWishlisted,imgWishlist,imgProfile;

        public Holder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);

        }
    }

}

