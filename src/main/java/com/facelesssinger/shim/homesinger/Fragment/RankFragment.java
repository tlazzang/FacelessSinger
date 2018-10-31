package com.facelesssinger.shim.homesinger.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.facelesssinger.shim.homesinger.Adapter.RankAdapter;
import com.facelesssinger.shim.homesinger.Data.User;
import com.facelesssinger.shim.homesinger.R;

import java.util.ArrayList;

public class RankFragment extends Fragment {

    private Spinner spinner;
    private RecyclerView recyclerView;
    private RankAdapter rankAdapter;

    public RankFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rank, container, false);
        spinner = (Spinner)view.findViewById(R.id.rank_spinner);
        recyclerView = (RecyclerView)view.findViewById(R.id.rank_recyclerView);
        if(rankAdapter == null) {
            rankAdapter = new RankAdapter();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(rankAdapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).toString().equals("누적 점수")){
                    rankAdapter.sortByTotal();
                }
                else if(parent.getItemAtPosition(position).toString().equals("평균 점수")){
                    rankAdapter.sortByAverage();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && spinner != null) {
            if (spinner.getSelectedItem().toString().equals("누적 점수")) {
                rankAdapter.sortByTotal();
            } else if (spinner.getSelectedItem().toString().equals("평균 점수")) {
                rankAdapter.sortByAverage();
            }
        }
    }
}
