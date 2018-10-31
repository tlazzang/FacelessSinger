package com.facelesssinger.shim.homesinger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.facelesssinger.shim.homesinger.Adapter.SongListAdapter;
import com.google.firebase.auth.FirebaseAuth;

public class MySongActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SongListAdapter songListAdapter;
    private String myUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_song);
        recyclerView = (RecyclerView) findViewById(R.id.mySong_recyclerView);
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        songListAdapter = new SongListAdapter(this, myUid);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(songListAdapter);
    }
}
