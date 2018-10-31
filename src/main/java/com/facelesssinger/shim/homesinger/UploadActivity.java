package com.facelesssinger.shim.homesinger;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.facelesssinger.shim.homesinger.Data.AudioFile;
import com.facelesssinger.shim.homesinger.Data.Song;
import com.facelesssinger.shim.homesinger.Data.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class UploadActivity extends AppCompatActivity {

    private Song song;
    private AudioFile audioFile;
    private ImageView iv_upload;
    private ProgressBar progressBar;
    private EditText et_originalSongTitle;
    private EditText et_originalSinger;
    private Spinner songTypeSpinner;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(com.facelesssinger.shim.homesinger.R.layout.activity_upload);

        audioFile = getIntent().getExtras().getParcelable("audioFile"); //PlayFragment로 부터 받아온 AudioFile 객체
        iv_upload = (ImageView) findViewById(com.facelesssinger.shim.homesinger.R.id.upload_iv_upload);
        progressBar = (ProgressBar) findViewById(com.facelesssinger.shim.homesinger.R.id.upload_progressBar);
        et_originalSongTitle = (EditText) findViewById(com.facelesssinger.shim.homesinger.R.id.upload_et_originalSongTitle);
        et_originalSinger = (EditText) findViewById(com.facelesssinger.shim.homesinger.R.id.upload_et_originalSinger);
        songTypeSpinner = (Spinner) findViewById(com.facelesssinger.shim.homesinger.R.id.upload_spinner_songType);

        iv_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(audioFile.getPath());
                //스토리지에 중복되지 않게 [userID]TimeStamp 형식으로 파일명지정
                String fileName = "["+MainActivity.currentUser.getId()+"]"+audioFile.getDateTime();
                Uri fileUri = Uri.fromFile(file);

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final StorageMetadata storageMetadata = new StorageMetadata.Builder().setContentType("audio/mpeg").build();
                UploadTask uploadTask = storageReference.child("audio/"+fileName).putFile(fileUri, storageMetadata);

                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) { //업로드 진행중
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) { //업로드 완료
                        progressBar.setVisibility(View.INVISIBLE);
                        songInit(taskSnapshot);
                        Toast.makeText(getApplicationContext(),"업로드 완료!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) { //업로드 실패

                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(),"업로드 실패 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void songInit(final UploadTask.TaskSnapshot taskSnapshot){
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                song = new Song();
                song.setOriginalSinger(et_originalSinger.getText().toString());
                song.setOriginalSongTitle(et_originalSongTitle.getText().toString());
                song.setSongType(songTypeSpinner.getSelectedItem().toString());
                song.setTimeStamp(ServerValue.TIMESTAMP);
                song.setDuration(String.valueOf(audioFile.getDurationMills()));
                song.setDownloadUrl(taskSnapshot.getDownloadUrl().toString());
                song.setUserId(uid);
                song.setUserNickName(MainActivity.currentUser.getNickName());
                databaseReference = FirebaseDatabase.getInstance().getReference().child("songs").push();

                final String songId = databaseReference.getKey();
                song.setId(songId);
                databaseReference.setValue(song).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        user.getMySongList().put(songId, true);
                        user.setNumOfMySong(String.valueOf(user.getMySongList().size())); //내가 부른 곡 수를 MySongList를 String으로 변환후 저장
                        FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(user);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
