package com.efunhub.ticktok.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.efunhub.ticktok.R;
import com.efunhub.ticktok.adapter.PostAdapterNew;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    SearchView searchView;
    View view;
    PostAdapterNew videoAdapter;
    ArrayList<Integer> videoList = new ArrayList<>();
    RecyclerView rvPost;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);

        initData();
        setupSearchView();
        setUpRecycler();
        return  view;

    }

    private void setupSearchView() {
        searchView=view.findViewById(R.id.searchView);
        searchView.setQueryHint("Search");
        searchView.setIconified(false);
        searchView.setOnCloseListener(new androidx.appcompat.widget.SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
              //  finish();
                getFragmentManager().popBackStack();
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Intent menuListIntent = new Intent();
                menuListIntent.setAction("com.efunhub.frefgo.searchlist");
                menuListIntent.putExtra("searchText", newText);
               // sendBroadcast(menuListIntent);
                return true;
            }
        });
    }

    private void initData() {
        searchView = view.findViewById(R.id.searchView);
        rvPost = view.findViewById(R.id.rvPost);
       /* searchView.setQueryHint("Search");
        searchView.setClickable(true);
        searchView.setFocusable(true);
        searchView.requestFocus();*/

    }

    private void setUpRecycler() {
        videoList.add(R.drawable.nature1);
//        videoList.add(R.drawable.nature2);
//        videoList.add(R.drawable.nature1);
//        videoList.add(R.drawable.nature2);
//        videoList.add(R.drawable.nature1);
//        videoList.add(R.drawable.nature2);
//        videoList.add(R.drawable.nature1);
//        videoList.add(R.drawable.nature2);
//        videoList.add(R.drawable.nature1);
//        videoList.add(R.drawable.nature2);
//        videoList.add(R.drawable.nature1);
//        videoList.add(R.drawable.nature2);

//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3, RecyclerView.VERTICAL, false);
//        rvPost.setLayoutManager(gridLayoutManager);
//        videoAdapter = new PostAdapterNew(getActivity(), videoList);
//        rvPost.setAdapter(videoAdapter);
    }

}
