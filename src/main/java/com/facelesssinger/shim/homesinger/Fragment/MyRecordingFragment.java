package com.facelesssinger.shim.homesinger.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facelesssinger.shim.homesinger.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyRecordingFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Fragment[] fragments = {new RecordFragment(), new FileListFragment()};
    private MyPagerAdapter pagerAdapter;

    public MyRecordingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_recording, container, false);
        viewPager = view.findViewById(R.id.myRecording_viewPager);
        tabLayout = view.findViewById(R.id.myRecording_tabLayout);
        pagerAdapter = new MyPagerAdapter(getChildFragmentManager(), fragments);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        
        return view;
    }

    public class MyPagerAdapter extends FragmentPagerAdapter{
        private Fragment[] fragments;

        public MyPagerAdapter(FragmentManager fm, Fragment[] fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:{
                    return "녹음하기";
                }
                case 1:{
                    return "녹음목록";
                }
                default:{
                    return "";
                }
            }
        }
    }
}
