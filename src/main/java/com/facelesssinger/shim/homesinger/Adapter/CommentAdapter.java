package com.facelesssinger.shim.homesinger.Adapter;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facelesssinger.shim.homesinger.Data.Comment;
import com.facelesssinger.shim.homesinger.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    ArrayList<Comment> commentList = new ArrayList<>();

    public CommentAdapter(String songId) {
        FirebaseDatabase.getInstance()
                .getReference("songs")
                .child(songId)
                .child("commentOfSong").limitToLast(50)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                commentList.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    Comment comment = data.getValue(Comment.class);
                    commentList.add(comment);
                }
                Collections.reverse(commentList); //정렬이 오래된 것 기준으로 되기때문에 최신순으로 역정렬해줌.
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
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.comment_list_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        ((CustomViewHolder) holder).tv_nickName.setText(comment.getNickName());
        ((CustomViewHolder) holder).tv_comment.setText(comment.getComment());

        //timeStamp이용해서 tv_time에 몇분전 형식으로 표시하기
        Map<String, Object> currentTimeMap = new HashMap<>();
        currentTimeMap.put("timeStamp", ServerValue.TIMESTAMP);

        Long currentTime = System.currentTimeMillis();
        Long createTime = (Long)comment.getTimeStamp();

        String timeAgo = getTimeAgo(currentTime, createTime);

        ((CustomViewHolder) holder).tv_time.setText(timeAgo);
    }

    public String getTimeAgo(Long currentTime, Long createTime){
        Date date = new Date(currentTime - createTime);

        final long diff = currentTime - createTime;
        if (diff < MINUTE_MILLIS) {
            return "방금 전";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "1분 전";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + "분 전";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "1시간 전";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + "시간 전";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "어제";
        } else {
            return diff / DAY_MILLIS + "일 전";
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_nickName;
        public TextView tv_comment;
        public TextView tv_time;

        public CustomViewHolder(View view) {
            super(view);
            tv_nickName = (TextView) view.findViewById(R.id.commentListItem_tv_nickName);
            tv_comment = (TextView) view.findViewById(R.id.commentListItem_tv_comment);
            tv_time = (TextView) view.findViewById(R.id.commentListItem_tv_time);
        }
    }
}
