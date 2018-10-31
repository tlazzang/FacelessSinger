package com.facelesssinger.shim.homesinger.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facelesssinger.shim.homesinger.AverageComparator;
import com.facelesssinger.shim.homesinger.Data.User;
import com.facelesssinger.shim.homesinger.R;
import com.facelesssinger.shim.homesinger.ScoreComparator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class RankAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    ArrayList<User> userList = new ArrayList<>();

    private boolean byTotal = true;
    private boolean byAverage = false;

    public RankAdapter() {

        FirebaseDatabase.getInstance()
                .getReference("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            userList.add(data.getValue(User.class));
                        }
                        Collections.sort(userList, new ScoreComparator());
                        notifyDataSetChanged();

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void sortByTotal(){
            Collections.sort(userList, new ScoreComparator());
            byTotal = true;
            byAverage = false;
            notifyDataSetChanged();
        }

    public void sortByAverage(){
            Collections.sort(userList, new AverageComparator());
            byTotal = false;
            byAverage = true;
            notifyDataSetChanged();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rank_list_item, parent, false);
        CustomViewHolder customViewHolder = new CustomViewHolder(view);
        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(byTotal){
            ((CustomViewHolder) holder).tv_totalScore.setVisibility(View.VISIBLE);
            ((CustomViewHolder) holder).tv_averageScore.setVisibility(View.INVISIBLE);
        }
        else if(byAverage){
            ((CustomViewHolder) holder).tv_totalScore.setVisibility(View.INVISIBLE);
            ((CustomViewHolder) holder).tv_averageScore.setVisibility(View.VISIBLE);
        }

        User user = userList.get(position);
        ((CustomViewHolder) holder).tv_rank.setText(String.valueOf(position+1));
        ((CustomViewHolder) holder).tv_nickName.setText(user.getNickName());
        ((CustomViewHolder) holder).tv_totalScore.setText(user.getTotalScore());
        ((CustomViewHolder) holder).tv_averageScore.setText(user.getAverageOfAllSong());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_rank;
        public TextView tv_nickName;
        public TextView tv_averageScore;
        public TextView tv_totalScore;

        public CustomViewHolder(View view) {
            super(view);
            tv_rank = (TextView) view.findViewById(R.id.rankListItem_tv_rank);
            tv_nickName = (TextView) view.findViewById(R.id.rankListItem_tv_nickName);
            tv_averageScore = (TextView) view.findViewById(R.id.rankListItem_tv_averageScore);
            tv_totalScore = (TextView) view.findViewById(R.id.rankListItem_tv_totalScore);
        }
    }
}
