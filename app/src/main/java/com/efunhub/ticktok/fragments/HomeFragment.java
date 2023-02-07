package com.efunhub.ticktok.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.viewpager.widget.ViewPager;

import com.efunhub.ticktok.R;
import com.efunhub.ticktok.activity.MainActivity;
import com.efunhub.ticktok.model.HomeViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    TabLayout tbVideo;
    ViewPager vpVideo;

    private HomeViewModel homeViewModel;
    RecyclerView rvVideoView;
    View root;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);
        initData();
        setupViewPagerTabs();
        //setAdapter();
        return root;
    }

    private void initData() {
      //  rvVideoView = root.findViewById(R.id.rvVideoView);
        tbVideo=root.findViewById(R.id.tbVideo);
        vpVideo=root.findViewById(R.id.vpVideo);
    }

 /*   private void setAdapter() {
        videoList.add("https://grobiz.app/videos/WhatsApp%20Video%202020-09-16%20at%205.05.33%20PM.mp4");
        videoList.add("https://grobiz.app/videos/WhatsApp%20Video%202020-09-16%20at%205.05.33%20PM.mp4");
        videoList.add("https://grobiz.app/videos/WhatsApp%20Video%202020-09-16%20at%205.05.33%20PM.mp4");
        videoList.add("https://grobiz.app/videos/WhatsApp%20Video%202020-09-16%20at%205.05.33%20PM.mp4");
        videoList.add("https://grobiz.app/videos/WhatsApp%20Video%202020-09-16%20at%205.05.33%20PM.mp4");
        videoList.add("https://grobiz.app/videos/WhatsApp%20Video%202020-09-16%20at%205.05.33%20PM.mp4");

        SnapHelper snapHelper = new PagerSnapHelper();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1, RecyclerView.VERTICAL, false);
        rvVideoView.setLayoutManager(gridLayoutManager);
        videoAdapter = new VideoAdapter(getActivity(), videoList);
        rvVideoView.setAdapter(videoAdapter);
        snapHelper.attachToRecyclerView(rvVideoView);
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        //((MainActivity)getActivity()).clearBackStackInclusive("tag"); // tag (addToBackStack tag) should be the same which was used while transacting the F2 fragment
    }


    private void setupViewPagerTabs() {

        HomeFragment.ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());


        AllVideoFragment newFragment = new AllVideoFragment();

        adapter.addFragment(newFragment, "Videos");

        TrendingFragment fragment = new TrendingFragment();

        adapter.addFragment(fragment, "Trending");


        vpVideo.setAdapter(adapter);
        tbVideo.setupWithViewPager(vpVideo);


    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mTitles = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return mFragments.get(i);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mTitles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }

    }

}
