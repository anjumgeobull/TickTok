package com.efunhub.ticktok.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.efunhub.ticktok.R;
import com.efunhub.ticktok.activity.MainActivity;
import com.efunhub.ticktok.adapter.EffectAdapter;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class EffectFragments extends Fragment {
    public EffectFragments() {
        // Required empty public constructor
    }

    public static EffectFragments newInstance(String param1, String param2) {
        EffectFragments fragment = new EffectFragments();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    RecyclerView rvEffect;
    ArrayList<Integer> videoList = new ArrayList<>();
    EffectAdapter effectAdapter;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_effect, container, false);
        initData();

           //setUpRecycler();
        return view;
    }

/*
    private void setUpRecycler() {
        videoList.add(R.drawable.nature1);
        videoList.add(R.drawable.nature2);
        videoList.add(R.drawable.nature1);
        videoList.add(R.drawable.nature2);
        videoList.add(R.drawable.nature1);
        videoList.add(R.drawable.nature2);
        videoList.add(R.drawable.nature1);
        videoList.add(R.drawable.nature2);
        videoList.add(R.drawable.nature1);
        videoList.add(R.drawable.nature2);
        videoList.add(R.drawable.nature1);
        videoList.add(R.drawable.nature2);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 5, RecyclerView.VERTICAL, false);
        rvEffect.setLayoutManager(gridLayoutManager);
        effectAdapter = new EffectAdapter(getActivity(), videoList);
        rvEffect.setAdapter(effectAdapter);
    }
*/

    private void initData() {
        rvEffect = view.findViewById(R.id.rvEffect);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //((MainActivity) getActivity()).clearBackStackInclusive("tag"); // tag (addToBackStack tag) should be the same which was used while transacting the F2 fragment
    }

}
