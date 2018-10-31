package com.facelesssinger.shim.homesinger.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facelesssinger.shim.homesinger.Data.AudioFile;
import com.facelesssinger.shim.homesinger.Data.Song;
import com.facelesssinger.shim.homesinger.EvaluateActivity;
import com.facelesssinger.shim.homesinger.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SongListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Song> songList = new ArrayList<>();
    private ArrayList<Song> songListTemp = new ArrayList<>();
    private Map<String, Boolean> myFollowMap = new HashMap<>();
    private Context context;

    public SongListAdapter(Context context, final String myUid){ //내노래만 로딩
        this.context = context;
        FirebaseDatabase.getInstance()
                .getReference("songs")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                songList.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    Song song = data.getValue(Song.class);
                    if(song.getUserId().equals(myUid)){
                        songList.add(song);
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public SongListAdapter(Context context) { //전체 노래 로딩
        this.context = context;
        FirebaseDatabase.getInstance()
                .getReference("songs")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        songList.clear();
                        songListTemp.clear();
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            Song song = data.getValue(Song.class);
                            songList.add(song);
                        }
                        songListTemp.addAll(songList); //필터기능 위해서 임시 데이터를 만듬
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Song song = songList.get(position);

        String singer = song.getOriginalSinger();
        String title = song.getOriginalSongTitle();
        ((CustomViewHolder)holder).tv_songTitleSinger.setText(singer + " - " + title);
        ((CustomViewHolder)holder).tv_userNickName.setText(song.getUserNickName());
        ((CustomViewHolder)holder).tv_averageScore.setText(String.format("%.1f",Double.valueOf(song.getAverageScore())));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference storageReferencee;
                storageReferencee = FirebaseStorage.getInstance().getReferenceFromUrl(song.getDownloadUrl());
                storageReferencee.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        AudioFile audioFile = new AudioFile();

                        audioFile.setName(song.getOriginalSinger()+ " - " + song.getOriginalSongTitle());
                        audioFile.setPath(uri.toString());
                        audioFile.setDateTime((Long)song.getTimeStamp());
                        audioFile.setDurationMills(Long.parseLong(song.getDuration()));

                        Intent intent = new Intent(context, EvaluateActivity.class);
                        intent.putExtra("song",song);
                        intent.putExtra("audioFile",audioFile);

                        context.startActivity(intent);
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public void filter(String s){
        songList.clear();
        if(s.isEmpty() || s==null || s.equals("")){
            songList.addAll(songListTemp);
        }
        else {
            String text = s.toLowerCase();
            for(Song song : songListTemp){
                //유저닉네임, 가수, 제목으로 필터링
                if(song.getUserNickName().toLowerCase().contains(text) || song.getOriginalSongTitle().toLowerCase().contains(text)){
                    songList.add(song);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filterBySongType(ArrayList<String> songType){
        songList.clear();
        if(songType.size()<1){
            songList.addAll(songListTemp);
        }
        else {
            for(String type : songType){
                for(Song song : songListTemp){
                    if(song.getSongType().equals(type)){
                        songList.add(song);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_songTitleSinger;
        public TextView tv_userNickName;
        public TextView tv_averageScore;
        public CustomViewHolder(View view) {
            super(view);
            tv_songTitleSinger = (TextView)view.findViewById(R.id.songListItem_tv_singerTitle);
            tv_userNickName = (TextView)view.findViewById(R.id.songListItem_tv_userNickName);
            tv_averageScore = (TextView)view.findViewById(R.id.songListItem_tv_averageScore);

        }
    }
}
