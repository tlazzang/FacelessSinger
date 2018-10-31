package com.facelesssinger.shim.homesinger;


import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facelesssinger.shim.homesinger.Adapter.CommentAdapter;
import com.facelesssinger.shim.homesinger.Data.AudioFile;
import com.facelesssinger.shim.homesinger.Data.Comment;
import com.facelesssinger.shim.homesinger.Data.NotificationModel;
import com.facelesssinger.shim.homesinger.Data.Song;
import com.facelesssinger.shim.homesinger.Data.User;
import com.facelesssinger.shim.homesinger.Fragment.PlayFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EvaluateActivity extends AppCompatActivity {
    private PushNotificationHelper pushNotificationHelper;
    private User user;
    private Song song;
    private AudioFile audioFile;

    private EditText et_comment;
    private LinearLayout ll_evaluate; //버튼에 이미지,글자 넣은것처럼 보이려고 레이아웃사용
    private LinearLayout ll_evaluateCompleted; //평가 완료했을때 하단에 보여질 레이아웃
    private ImageView iv_sendComment;
    private TextView tv_nickName;
    private TextView tv_singerTitle;
    private TextView tv_averageScore;
    private TextView tv_viewCount;
    private Button btn_follow;
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private FloatingActionButton btn_play;
    private ImageView iv_delete;
    private ProgressBar pb_progressBar;

    private float score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(com.facelesssinger.shim.homesinger.R.layout.activity_evaluate);

        et_comment = (EditText) findViewById(com.facelesssinger.shim.homesinger.R.id.evaluate_et_comments);
        ll_evaluate = (LinearLayout) findViewById(com.facelesssinger.shim.homesinger.R.id.evaluate_layout_evaluate);
        ll_evaluateCompleted = (LinearLayout) findViewById(com.facelesssinger.shim.homesinger.R.id.evaluate_layout_evaluateCompleted);
        iv_sendComment = (ImageView) findViewById(com.facelesssinger.shim.homesinger.R.id.evaluate_iv_commentSend);
        tv_nickName = (TextView) findViewById(com.facelesssinger.shim.homesinger.R.id.evaluate_tv_nickName) ;
        tv_singerTitle = (TextView) findViewById(com.facelesssinger.shim.homesinger.R.id.evaluate_tv_singerTitle);
        tv_averageScore = (TextView) findViewById(com.facelesssinger.shim.homesinger.R.id.evaluate_tv_averageScore);
        tv_viewCount = (TextView) findViewById(R.id.evaluate_tv_viewCount);
        recyclerView = (RecyclerView) findViewById(com.facelesssinger.shim.homesinger.R.id.evaluate_recyclerView_comments);
        btn_follow = (Button) findViewById(com.facelesssinger.shim.homesinger.R.id.evaluate_btn_follow);
        btn_play = (FloatingActionButton) findViewById(com.facelesssinger.shim.homesinger.R.id.evaluate_btn_play);
        iv_delete = (ImageView) findViewById(com.facelesssinger.shim.homesinger.R.id.evaluate_iv_delete);
        pb_progressBar = (ProgressBar) findViewById(com.facelesssinger.shim.homesinger.R.id.evaluate_progressBar);

        pushNotificationHelper = new PushNotificationHelper();
        song = (Song)getIntent().getParcelableExtra("song");
        audioFile = (AudioFile)getIntent().getParcelableExtra("audioFile");

        commentAdapter = new CommentAdapter(song.getId());
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(commentAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("평가하기");
        setViewCount();
        showAndHideEvaluateBtn();
        showDeleteView();
        changeFollowBtnText();
        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(song.getUserId())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                tv_nickName.setText(user.getNickName());
                tv_singerTitle.setText(song.getOriginalSinger() + " - " + song.getOriginalSongTitle());
                tv_averageScore.setText(song.getAverageScore());
                tv_viewCount.setText(song.getViewCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        iv_sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentStr = et_comment.getText().toString();
                if(commentStr!=null && !commentStr.equals("") && commentStr.length() < 300){
                    Comment comment = new Comment();
                    comment.setComment(commentStr);
                    comment.setNickName(MainActivity.currentUser.getNickName());

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                            .getReference("songs").child(song.getId()).child("commentOfSong").push();
                    String commentId = databaseReference.getKey();
                    comment.setId(commentId);
                    comment.setTimeStamp(ServerValue.TIMESTAMP);

                    databaseReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FirebaseDatabase.getInstance()
                                    .getReference("users")
                                    .child(song.getUserId())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class); //푸시알람 받을 유저
                                    pushNotificationHelper.sendGcm(user.getToken(), pushNotificationHelper.FLAG_NOTIFICATION_COMMENT, song);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                    et_comment.setText("");
                }
                else{
                    Toast.makeText(getApplicationContext(), "1자 이상 300자 미만으로 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ll_evaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(getApplicationContext()).inflate(com.facelesssinger.shim.homesinger.R.layout.rating_dialog, null);

                RatingBar ratingBar = (RatingBar)view.findViewById(com.facelesssinger.shim.homesinger.R.id.ratingDialog_ratingBar);

                AlertDialog.Builder builder = new AlertDialog.Builder(EvaluateActivity.this);
                builder.setView(view)
                        .setMessage("드래그 하여 원하시는 별점을 선택해주세요")
                        .setPositiveButton("평가완료", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setRating();
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
                        if(rating >= 1.0){
                            score = rating;
                        }
                        else {
                            score = (float)1.0;
                            ratingBar.setRating((float)1.0);
                            Toast.makeText(getApplicationContext(),"최소 별점은 1점입니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User currentUser = MainActivity.currentUser;
                boolean isFollow = false;
                if(currentUser.getMyFollowingList().containsKey(song.getUserId())){
                    currentUser.getMyFollowingList().remove(song.getUserId());
                    Toast.makeText(getApplicationContext(), "팔로우 취소 완료" , Toast.LENGTH_SHORT).show();
                    btn_follow.setText("팔로우");
                    isFollow = false;
                }
                else {
                    currentUser.getMyFollowingList().put(song.getUserId(), true);
                    Toast.makeText(getApplicationContext(), "팔로우 완료" , Toast.LENGTH_SHORT).show();
                    btn_follow.setText("팔로우 취소");
                    isFollow = true;
                }
                final boolean finalIsFollow = isFollow;
                FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(currentUser.getId())
                        .setValue(currentUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if(finalIsFollow) {
                            pushNotificationHelper.sendGcm(user.getToken(), PushNotificationHelper.FLAG_NOTIFICATION_FOLLOWER, song);
                        }
                    }
                });
            }
        });

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlayDialog();
            }
        });

        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSong();
            }
        });

        showPlayDialog();
    }

    public void showPlayDialog(){
        PlayFragment playbackFragment =
                new PlayFragment().newInstance(audioFile);

        FragmentTransaction transaction = ((FragmentActivity) EvaluateActivity.this)
                .getSupportFragmentManager()
                .beginTransaction();

        playbackFragment.show(transaction, "dialog_playback");
    }

    public void setRating(){
        FirebaseDatabase.getInstance()
                .getReference("songs")
                .child(song.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Song song = dataSnapshot.getValue(Song.class);
                final float totalScore = Float.parseFloat(song.getTotalScore()) + score;
                int numOfVoters = Integer.parseInt(song.getNumOfVoters()) + 1;
                final float averageScore = totalScore / (float)numOfVoters;
                final float averageDiff;
                if(numOfVoters == 1) {
                    averageDiff = averageScore;
                }
                else {

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
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                pushNotificationHelper.sendGcm(user.getToken(), PushNotificationHelper.FLAG_NOTIFICATION_EVALUATE, song);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void setViewCount(){
        FirebaseDatabase.getInstance()
                .getReference("songs")
                .child(song.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Song song = dataSnapshot.getValue(Song.class);
                int viewCount = Integer.valueOf(song.getViewCount()) + 1;
                song.setViewCount(String.valueOf(viewCount));
                FirebaseDatabase.getInstance().getReference("songs").child(song.getId()).setValue(song);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showDeleteView(){
        //내가 올린 노래일 경우
        if(song.getUserId().equals(MainActivity.currentUser.getId())){
            iv_delete.setVisibility(View.VISIBLE);
            btn_follow.setVisibility(View.INVISIBLE);
        }
        else {
            iv_delete.setVisibility(View.INVISIBLE);
            btn_follow.setVisibility(View.VISIBLE);
        }
    }

    public void deleteSong(){
        StorageReference storageReference =
                FirebaseStorage.getInstance().getReferenceFromUrl(song.getDownloadUrl());

        pb_progressBar.setVisibility(View.VISIBLE);

        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //스토리지 데이터 삭제 완료 후 데이터베이스에 있는 song 데이터도 삭제
                FirebaseDatabase.getInstance()
                        .getReference("songs")
                        .child(song.getId())
                        .removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pb_progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(),"삭제 완료!",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

                //User의 MySongList에서도 노래를 삭제하고 numOfSong을 1감소시킴
                FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(MainActivity.currentUser.getId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        user.getMySongList().remove(song.getId());
                        int numOfMySong = Integer.parseInt(user.getNumOfMySong()) - 1;
                        user.setNumOfMySong(String.valueOf(numOfMySong));
                        FirebaseDatabase.getInstance().getReference("users").child(MainActivity.currentUser.getId()).setValue(user);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pb_progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(),"삭제 실패 다시 시도해주세요.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showAndHideEvaluateBtn(){

        FirebaseDatabase.getInstance()
                .getReference("songs")
                .child(song.getId())
                .child("idOfVoters")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    Map<String, Boolean> idOfVotersMap = (Map) dataSnapshot.getValue();

                    if (idOfVotersMap.containsKey(MainActivity.currentUser.getId())) {
                        ll_evaluate.setVisibility(View.INVISIBLE);
                        ll_evaluateCompleted.setVisibility(View.VISIBLE);
                    } else {
                        ll_evaluate.setVisibility(View.VISIBLE);
                        ll_evaluateCompleted.setVisibility(View.INVISIBLE);
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void changeFollowBtnText(){
        User currentUser = MainActivity.currentUser;
        if(currentUser.getMyFollowingList().containsKey(song.getUserId())){
            btn_follow.setText("팔로우 취소");
        }
        else {
            btn_follow.setText("팔로우");
        }
    }

//    public void sendGcm(String token, int flag)
//    {
//        final String AUTH_KEY_FCM = "AIzaSyDDh8StR66p4rdROGKMVKYxh-FUxlhYs4w";
//        final String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
//
//
//        Gson gson = new Gson();
//        NotificationModel notificationModel = new NotificationModel();
//        notificationModel.setTo(token);
//        String notiTitle = null;
//        String notiText = null;
//        switch (flag){
//            case FLAG_NOTIFICATION_COMMENT : {
//                notiTitle = "새로운 댓글";
//                notiText = MainActivity.currentUser.getNickName()+"님이 댓글을 남겼습니다.("+song.getUserNickName()+" - "+song.getOriginalSongTitle()+")";
//                break;
//            }
//            case FLAG_NOTIFICATION_FOLLOWER : {
//                notiTitle = "새로운 팔로워";
//                notiText = MainActivity.currentUser.getNickName()+"님이 회원님을 팔로우합니다.";
//                break;
//            }
//            case FLAG_NOTIFICATION_EVALUATE : {
//                notiTitle = "새로운 평가";
//                notiText = "회원님의 노래가 평가되었습니다." + "(" + song.getUserNickName()+ " - " + song.getOriginalSongTitle() + ")";
//                break;
//            }
//        }
//        notificationModel.getNotification().setTitle(notiTitle);
//        notificationModel.getNotification().setText(notiText);
//        notificationModel.getData().setTitle("data title");
//        notificationModel.getData().setText("data text");
//
//        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;  charset=utf-8"),gson.toJson(notificationModel));
//
//        Request request = new Request.Builder()
//                .header("Content-Type","application/json")
//                .addHeader("Authorization","key=" + AUTH_KEY_FCM)
//                .url(API_URL_FCM)
//                .post(requestBody)
//                .build();
//
//        OkHttpClient okHttpClient = new OkHttpClient();
//
//        okHttpClient.newCall(request).enqueue(new Callback() {
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if(response.code() == 200){
//
//                }
//                else{
//
//                }
//            }
//        });
//
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        if(menuId == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
