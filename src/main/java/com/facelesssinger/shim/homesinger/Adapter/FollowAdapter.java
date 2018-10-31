package com.facelesssinger.shim.homesinger.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facelesssinger.shim.homesinger.CommentActivity;
import com.facelesssinger.shim.homesinger.Data.AudioFile;
import com.facelesssinger.shim.homesinger.Data.Song;
import com.facelesssinger.shim.homesinger.Data.User;
import com.facelesssinger.shim.homesinger.Fragment.HomeFragment;
import com.facelesssinger.shim.homesinger.MainActivity;
import com.facelesssinger.shim.homesinger.PushNotificationHelper;
import com.facelesssinger.shim.homesinger.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FollowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    PushNotificationHelper pushNotificationHelper;

    ArrayList<Song> songList = new ArrayList<>();
    Map<String, Boolean> followingMap = new HashMap<>();
    Map<String, String> nickNameMap = new HashMap<>();
    HomeFragment homeFragment;
    AudioFile item = new AudioFile();
    long minutes = 0;
    long seconds = 0;
    float score = 0;

    Context context;

    public FollowAdapter(final Context context, final HomeFragment homeFragment) {
        this.pushNotificationHelper = new PushNotificationHelper();
        this.context = context;
        this.homeFragment = homeFragment;
        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(MainActivity.currentUser.getId())
                .child("myFollowingList")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            homeFragment.showEmptyMessage(false);
                            songList.clear();
                            followingMap = (Map) dataSnapshot.getValue();
                            for (String userId : followingMap.keySet()) {
                                getSongListMap(userId);
                            }
                        }
                        else {
                            homeFragment.showEmptyMessage(true);
                        }
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    public void getSongListMap(String userId) {
        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId).child("mySongList")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Map<String, Boolean> songListMap = (Map) dataSnapshot.getValue();
                            for (String songId : songListMap.keySet()) {
                                addSongToList(songId);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void addSongToList(String songId) {
        FirebaseDatabase.getInstance()
                .getReference("songs")
                .child(songId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Song song = dataSnapshot.getValue(Song.class);
                            songList.add(song);
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void updateData(){
        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(MainActivity.currentUser.getId())
                .child("myFollowingList")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            songList.clear();
                            followingMap = (Map) dataSnapshot.getValue();
                            for (String userId : followingMap.keySet()) {
                                getSongListMap(userId);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list_item, parent, false);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        final Song song = songList.get(position);
        String key = song.getUserId();
        String nickName = song.getUserNickName();

        item.setName(song.getOriginalSinger() + " - " + song.getOriginalSongTitle());
        item.setDateTime((Long) song.getTimeStamp());
        item.setDurationMills(Long.parseLong(song.getDuration()));

        long itemDuration = item.getDurationMills();
        minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
        seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration) - TimeUnit.MINUTES.toSeconds(minutes);

        ColorFilter filter = new LightingColorFilter
                (((FragmentActivity) context).getResources().getColor(R.color.colorPrimary), ((FragmentActivity) context).getResources().getColor(R.color.colorPrimary));

        ((CustomViewHolder) holder).sb_seekBar.getProgressDrawable().setColorFilter(filter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ((CustomViewHolder) holder).sb_seekBar.getThumb().setColorFilter(filter);
        }

        //별표 이미지 눌렀을 때 평가하기 다이얼로그 띄움
        ((CustomViewHolder) holder).iv_evaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //isVoted 함수에서 투표여부 판단 후 안했다면 showRatingDialog함수 호출함
                isVoted(song.getId());
            }
        });

        //댓글 이미지 눌렀을 때 댓글 액티비티 실행
        ((CustomViewHolder) holder).iv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("song", songList.get(position));
                context.startActivity(intent);
            }
        });

        ((CustomViewHolder) holder).sb_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (((CustomViewHolder) holder).mMediaPlayer != null && fromUser) {
                    ((CustomViewHolder) holder).mMediaPlayer.seekTo(progress);
                    ((CustomViewHolder) holder).mHandler.removeCallbacks(((CustomViewHolder) holder).mRunnable);

                    long minutes = TimeUnit.MILLISECONDS.toMinutes(((CustomViewHolder) holder).mMediaPlayer.getCurrentPosition());
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(((CustomViewHolder) holder).mMediaPlayer.getCurrentPosition())
                            - TimeUnit.MINUTES.toSeconds(minutes);
                    ((CustomViewHolder) holder).tv_currentProgress.setText(String.format("%02d:%02d", minutes, seconds));

                    ((CustomViewHolder) holder).updateSeekBar();

                } else if (((CustomViewHolder) holder).mMediaPlayer == null && fromUser) {
                    ((CustomViewHolder) holder).prepareMediaPlayerFromPoint(progress);
                    ((CustomViewHolder) holder).updateSeekBar();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (((CustomViewHolder) holder).mMediaPlayer != null) {
                    // remove message Handler from updating progress bar
                    ((CustomViewHolder) holder).mHandler.removeCallbacks(((CustomViewHolder) holder).mRunnable);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (((CustomViewHolder) holder).mMediaPlayer != null) {
                    ((CustomViewHolder) holder).mHandler.removeCallbacks(((CustomViewHolder) holder).mRunnable);
                    ((CustomViewHolder) holder).mMediaPlayer.seekTo(seekBar.getProgress());

                    long minutes = TimeUnit.MILLISECONDS.toMinutes(((CustomViewHolder) holder).mMediaPlayer.getCurrentPosition());
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(((CustomViewHolder) holder).mMediaPlayer.getCurrentPosition())
                            - TimeUnit.MINUTES.toSeconds(minutes);
                    ((CustomViewHolder) holder).tv_currentProgress.setText(String.format("%02d:%02d", minutes, seconds));
                    ((CustomViewHolder) holder).updateSeekBar();
                }
            }
        });

        ((CustomViewHolder) holder).btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference storageReferencee;
                storageReferencee = FirebaseStorage.getInstance().getReferenceFromUrl(song.getDownloadUrl());
                storageReferencee.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        AudioFile audioFile = new AudioFile();
                        audioFile.setName(song.getOriginalSinger() + " - " + song.getOriginalSongTitle());
                        audioFile.setPath(uri.toString());
                        audioFile.setDateTime((Long) song.getTimeStamp());
                        audioFile.setDurationMills(Long.parseLong(song.getDuration()));

                        item = audioFile;

                        ((CustomViewHolder) holder).onPlay(((CustomViewHolder) holder).isPlaying);
                        ((CustomViewHolder) holder).isPlaying = !((CustomViewHolder) holder).isPlaying;
                    }
                });

            }
        });

        ((CustomViewHolder) holder).tv_singerTitle.setText(item.getName());
        ((CustomViewHolder) holder).tv_fileLength.setText(String.format("%02d:%02d", minutes, seconds));
        ((CustomViewHolder) holder).tv_nickName.setText(nickName);
        ((CustomViewHolder) holder).tv_averageScore.setText(song.getAverageScore());
        ((CustomViewHolder) holder).tv_viewCount.setText(song.getViewCount());

        int commentCount = song.getCommentOfSong().size();
        ((CustomViewHolder) holder).tv_commentCount.setText(String.valueOf(commentCount));
    }

    public void isVoted(final String songId) {
        FirebaseDatabase.getInstance()
                .getReference("songs")
                .child(songId)
                .child("idOfVoters")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            Map<String, Boolean> idOfVotersMap = (Map) dataSnapshot.getValue();

                            if (idOfVotersMap.containsKey(MainActivity.currentUser.getId())) {
                                Toast.makeText(context, "이미 평가하셨습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                showRatingDialog(songId);
                            }
                        } else {
                            showRatingDialog(songId);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void showRatingDialog(final String songId) {
        View view = LayoutInflater.from(context).inflate(R.layout.rating_dialog, null);

        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingDialog_ratingBar);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view)
                .setMessage("드래그 하여 원하시는 별점을 선택해주세요")
                .setPositiveButton("평가완료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setRating(songId);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog ratingDialog = builder.create();
        ratingDialog.show();

        score = ratingBar.getRating();
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating >= 1.0) {
                    score = rating;
                } else {
                    score = (float) 1.0;
                    ratingBar.setRating((float) 1.0);
                    Toast.makeText(context, "최소 별점은 1점입니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setRating(String songId) {
        FirebaseDatabase.getInstance()
                .getReference("songs")
                .child(songId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Song song = dataSnapshot.getValue(Song.class);
                        final float totalScore = Float.parseFloat(song.getTotalScore()) + score;
                        int numOfVoters = Integer.parseInt(song.getNumOfVoters()) + 1;
                        final float averageScore = totalScore / (float) numOfVoters;
                        final float averageDiff;
                        if (numOfVoters == 1) {
                            averageDiff = averageScore;
                        } else {

                            averageDiff = averageScore - Float.parseFloat(song.getAverageScore());
                        }
                        Map<String, Boolean> idOfVotersMap = song.getIdOfVoters();
                        idOfVotersMap.put(MainActivity.currentUser.getId(), true);
                        song.setTotalScore(String.valueOf(totalScore));
                        song.setNumOfVoters(String.valueOf(numOfVoters));
                        song.setAverageScore(String.valueOf(averageScore));

                        FirebaseDatabase.getInstance().getReference("songs").child(song.getId()).setValue(song);

                        FirebaseDatabase.getInstance()
                                .getReference("users")
                                .child(song.getUserId())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        //평가가 완료되면 해당곡을 부른 유저의 점수를 업데이트하고 평균을 구해서 다시 업데이트.
                                        User user = dataSnapshot.getValue(User.class);
                                        float allSongTotalScore = Float.parseFloat(user.getTotalScore()) + score;
                                        float allSongAverageScore = Float.parseFloat(user.getTotalAverageScore()) + averageDiff;
                                        float averageOfAllSongs = allSongAverageScore / Float.parseFloat(user.getNumOfMySong());

                                        user.setTotalScore(String.valueOf(allSongTotalScore));
                                        user.setTotalAverageScore(String.valueOf(allSongAverageScore));
                                        user.setAverageOfAllSong(String.valueOf(averageOfAllSongs));

                                        FirebaseDatabase.getInstance().getReference("users").child(song.getUserId()).setValue(user);
                                        pushNotificationHelper.sendGcm(user.getToken(), PushNotificationHelper.FLAG_NOTIFICATION_EVALUATE, song);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {
        private static final String LOG_TAG = "PlaybackFragment";

        private Handler mHandler = new Handler();
        private MediaPlayer mMediaPlayer = null;

        //stores whether or not the mediaplayer is currently playing audio
        private boolean isPlaying = false;

        //stores minutes and seconds of the length of the file.
        long minutes = 0;
        long seconds = 0;


        public TextView tv_nickName;
        public ImageView iv_evaluate;
        public ImageView iv_comment;
        public TextView tv_averageScore;
        public TextView tv_viewCount;
        public TextView tv_commentCount;
        public TextView tv_singerTitle;
        public FloatingActionButton btn_play;
        public TextView tv_currentProgress;
        public TextView tv_fileLength;
        public SeekBar sb_seekBar;

        public CustomViewHolder(View view) {
            super(view);

            tv_nickName = (TextView) view.findViewById(R.id.homeListItem_tv_nickName);
            iv_evaluate = (ImageView) view.findViewById(R.id.homeListItem_iv_star);
            iv_comment = (ImageView) view.findViewById(R.id.homeListItem_iv_comment);
            tv_averageScore = (TextView) view.findViewById(R.id.homeListItem_tv_averageScore);
            tv_viewCount = (TextView) view.findViewById(R.id.homeListItem_tv_viewCount);
            tv_commentCount = (TextView) view.findViewById(R.id.homeListItem_tv_commentCount);
            tv_singerTitle = (TextView) view.findViewById(R.id.homeListItem_tv_singerTitle);
            btn_play = (FloatingActionButton) view.findViewById(R.id.homeListItem_fab_play);
            tv_currentProgress = (TextView) view.findViewById(R.id.homeListItem_tv_current_progress);
            tv_fileLength = (TextView) view.findViewById(R.id.homeListItem_tv_file_length);
            sb_seekBar = (SeekBar) view.findViewById(R.id.homeListItem_seekbar);
            btn_play = (FloatingActionButton) view.findViewById(R.id.homeListItem_fab_play);
        }

//        @Override
//        public void onPause() {
//            super.onPause();
//
//            if (mMediaPlayer != null) {
//                stopPlaying();
//            }
//        }
//
//        @Override
//        public void onDestroy() {
//            super.onDestroy();
//
//            if (mMediaPlayer != null) {
//                stopPlaying();
//            }
//        }

        // Play start/stop
        private void onPlay(boolean isPlaying) {
            if (!isPlaying) {
                //currently MediaPlayer is not playing audio
                if (mMediaPlayer == null) {
                    startPlaying(); //start from beginning
                } else {
                    resumePlaying(); //resume the currently paused MediaPlayer
                }

            } else {
                //pause the MediaPlayer
                pausePlaying();
            }
        }

        private void startPlaying() {
            btn_play.setImageResource(R.drawable.ic_media_pause);
            mMediaPlayer = new MediaPlayer();

            try {
                mMediaPlayer.setDataSource(item.getPath());
                mMediaPlayer.prepare();
                sb_seekBar.setMax(mMediaPlayer.getDuration());

                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mMediaPlayer.start();
                    }
                });
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
            }

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlaying();
                }
            });

            updateSeekBar();

            //keep screen on while playing audio
            ((FragmentActivity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        private void prepareMediaPlayerFromPoint(int progress) {
            //set mediaPlayer to start from middle of the audio file

            mMediaPlayer = new MediaPlayer();

            try {
                mMediaPlayer.setDataSource(item.getPath());
                mMediaPlayer.prepare();
                sb_seekBar.setMax(mMediaPlayer.getDuration());
                mMediaPlayer.seekTo(progress);

                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopPlaying();
                    }
                });

            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
            }

            //keep screen on while playing audio
            ((FragmentActivity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        private void pausePlaying() {
            btn_play.setImageResource(R.drawable.play_white);
            mHandler.removeCallbacks(mRunnable);
            mMediaPlayer.pause();
        }

        private void resumePlaying() {
            btn_play.setImageResource(R.drawable.pause_white);
            mHandler.removeCallbacks(mRunnable);
            mMediaPlayer.start();
            updateSeekBar();
        }

        private void stopPlaying() {
            btn_play.setImageResource(R.drawable.play_white);
            mHandler.removeCallbacks(mRunnable);
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;

            sb_seekBar.setProgress(sb_seekBar.getMax());
            isPlaying = !isPlaying;

            tv_currentProgress.setText(tv_fileLength.getText());
            sb_seekBar.setProgress(sb_seekBar.getMax());

            //allow the screen to turn off again once audio is finished playing
            ((FragmentActivity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        }

        //updating mSeekBar
        private Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                if (mMediaPlayer != null) {

                    int mCurrentPosition = mMediaPlayer.getCurrentPosition();
                    sb_seekBar.setProgress(mCurrentPosition);

                    long minutes = TimeUnit.MILLISECONDS.toMinutes(mCurrentPosition);
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(mCurrentPosition)
                            - TimeUnit.MINUTES.toSeconds(minutes);
                    tv_currentProgress.setText(String.format("%02d:%02d", minutes, seconds));

                    updateSeekBar();
                }
            }
        };

        private void updateSeekBar() {
            mHandler.postDelayed(mRunnable, 1000);
        }
    }
}

