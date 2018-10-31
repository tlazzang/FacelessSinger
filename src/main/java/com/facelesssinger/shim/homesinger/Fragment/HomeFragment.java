package com.facelesssinger.shim.homesinger.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facelesssinger.shim.homesinger.Adapter.FollowAdapter;
import com.facelesssinger.shim.homesinger.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private FollowAdapter followAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tv_empty;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.home_recyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.home_layout_swipeRefresh);
        followAdapter = new FollowAdapter(getActivity(),this);
        tv_empty = (TextView) view.findViewById(R.id.home_tv_emptyText);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(followAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                followAdapter.updateData();
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "새로고침 완료",Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public void showEmptyMessage(boolean isEmpty){
        if(isEmpty){
            tv_empty.setVisibility(View.VISIBLE);
        }
        else {
            tv_empty.setVisibility(View.GONE);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        //포즈 되었을 때 FollowAdapter내에 재생중인 오디오 끄기
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //포즈 되었을 때 FollowAdapter내에 재생중인 오디오 끄기
    }
}
