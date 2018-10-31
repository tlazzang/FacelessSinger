package com.facelesssinger.shim.homesinger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facelesssinger.shim.homesinger.Adapter.CommentAdapter;
import com.facelesssinger.shim.homesinger.Data.Comment;
import com.facelesssinger.shim.homesinger.Data.NotificationModel;
import com.facelesssinger.shim.homesinger.Data.Song;
import com.facelesssinger.shim.homesinger.Data.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommentActivity extends AppCompatActivity {
    private PushNotificationHelper pushNotificationHelper;
    private Song song;
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private ImageView iv_commentSend;
    private EditText et_comment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(com.facelesssinger.shim.homesinger.R.layout.activity_comment);

        Intent intent = getIntent();
        song = intent.getParcelableExtra("song");
        pushNotificationHelper = new PushNotificationHelper();

        iv_commentSend = (ImageView) findViewById(com.facelesssinger.shim.homesinger.R.id.comment_iv_commentSend);
        et_comment = (EditText) findViewById(com.facelesssinger.shim.homesinger.R.id.comment_et_comments);
        recyclerView = (RecyclerView) findViewById(com.facelesssinger.shim.homesinger.R.id.comment_recyclerView);
        commentAdapter = new CommentAdapter(song.getId());

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(commentAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("댓글");

        //전송 이미지 클릭시 코멘트를 firebase database에 저장
        iv_commentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentStr = et_comment.getText().toString();
                if(commentStr!=null && !commentStr.equals("") && commentStr.length()<300){
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                            .getReference("songs").child(song.getId()).child("commentOfSong").push();
                    String commentId = databaseReference.getKey();

                    Comment comment = new Comment();
                    comment.setComment(commentStr);
                    comment.setNickName(MainActivity.currentUser.getNickName());
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
                                    User user = dataSnapshot.getValue(User.class);
                                    pushNotificationHelper.sendGcm(user.getToken(), PushNotificationHelper.FLAG_NOTIFICATION_COMMENT, song);
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
    }

    public void sendGcm(String token)
    {
        final String AUTH_KEY_FCM = "AIzaSyDDh8StR66p4rdROGKMVKYxh-FUxlhYs4w";
        final String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";


        Gson gson = new Gson();
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.setTo(token);
        notificationModel.getNotification().setTitle("새로운 댓글");
        notificationModel.getNotification().setText(MainActivity.currentUser.getNickName()+"님이 댓글을 남겼습니다.("+song.getUserNickName()+" - "+song.getOriginalSongTitle()+")");
        notificationModel.getData().setTitle("data title");
        notificationModel.getData().setText("data text");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;  charset=utf-8"),gson.toJson(notificationModel));

        Request request = new Request.Builder()
                .header("Content-Type","application/json")
                .addHeader("Authorization","key=" + AUTH_KEY_FCM)
                .url(API_URL_FCM)
                .post(requestBody)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();

        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code() == 200){

                }
                else{

                }
            }
        });

    }

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
