package com.facelesssinger.shim.homesinger.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facelesssinger.shim.homesinger.Data.AudioFile;
import com.facelesssinger.shim.homesinger.Fragment.PlayFragment;
import com.facelesssinger.shim.homesinger.R;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class FileListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<AudioFile> fileList;
    private Context context;

    public FileListAdapter(ArrayList<AudioFile> fileList, Context context) {
        this.fileList = fileList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_item, parent, false);

        CustomViewHolder customViewHolder = new CustomViewHolder(view);
        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ((CustomViewHolder)holder).tv_name.setText(fileList.get(position).getName());

        long itemDuration = fileList.get(position).getDurationMills();

        long minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration)
                - TimeUnit.MINUTES.toSeconds(minutes);

        ((CustomViewHolder)holder).tv_dateTime.setText(
                DateUtils.formatDateTime(
                        context,
                        fileList.get(position).getDateTime(),
                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR
                )
        );

        ((CustomViewHolder)holder).tv_duration.setText(String.format("%02d:%02d", minutes, seconds));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    PlayFragment playbackFragment =
                            new PlayFragment().newInstance(fileList.get(position));

                    FragmentTransaction transaction = ((FragmentActivity) context)
                            .getSupportFragmentManager()
                            .beginTransaction();

                    playbackFragment.show(transaction, "dialog_playback");

                } catch (Exception e) {

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public void updateData(ArrayList<AudioFile> fileList){
        this.fileList = fileList;
        notifyDataSetChanged();
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name;
        public TextView tv_duration;
        public TextView tv_dateTime;

        public CustomViewHolder(View view) {
            super(view);
            tv_name = (TextView) view.findViewById(R.id.fileListItem_tv_fileName);
            tv_duration = (TextView) view.findViewById(R.id.fileListItem_tv_duration);
            tv_dateTime = (TextView) view.findViewById(R.id.fileListItem_tv_dateTime);
        }
    }
}
