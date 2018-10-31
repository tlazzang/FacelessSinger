package com.facelesssinger.shim.homesinger.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SearchView;

import com.facelesssinger.shim.homesinger.Adapter.SongListAdapter;
import com.facelesssinger.shim.homesinger.Data.Song;
import com.facelesssinger.shim.homesinger.R;

import java.util.ArrayList;


public class ListenFragment extends Fragment {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private ImageView iv_sortBy;
    private SongListAdapter songListAdapter;

    private ArrayList<Song> songList = new ArrayList<>();

    public ListenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listen, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.listen_recyclerView);
        searchView = (SearchView)view.findViewById(R.id.listen_searchView);
        iv_sortBy = (ImageView)view.findViewById(R.id.listen_iv_sort);

        songListAdapter = new SongListAdapter(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(songListAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                songListAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                songListAdapter.filter(newText);
                return true;
            }
        });

        iv_sortBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.songtype_dialog, null);

                final CheckBox cb_domestic = (CheckBox) view.findViewById(R.id.songTypeDialog_cb_domestic);
                final CheckBox cb_pop = (CheckBox) view.findViewById(R.id.songTypeDialog_cb_pop);
                final CheckBox cb_rap = (CheckBox) view.findViewById(R.id.songTypeDialog_cb_rap);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("노래 장르 필터")
                        .setMessage("듣고싶은 노래 장르를 선택하세요")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ArrayList<String> songType = new ArrayList<>();
                                if(cb_domestic.isChecked()){
                                    songType.add("국내가요");
                                }
                                if(cb_pop.isChecked()){
                                    songType.add("팝송");
                                }
                                if(cb_rap.isChecked()){
                                    songType.add("랩");
                                }
                                songListAdapter.filterBySongType(songType);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setView(view);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        return view;
    }
}
