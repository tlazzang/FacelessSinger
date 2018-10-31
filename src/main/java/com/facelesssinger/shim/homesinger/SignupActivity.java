package com.facelesssinger.shim.homesinger;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facelesssinger.shim.homesinger.Data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private EditText et_email;
    private EditText et_name; //닉네임
    private EditText et_password;
    private Button btn_signup;
    private ProgressBar progressBar;

    private String email;
    private String name;
    private String password;
    private String uid;

    private final int PICK_FROM_ALBUM = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(com.facelesssinger.shim.homesinger.R.layout.activity_signup);

        et_email = (EditText)findViewById(R.id.signup_et_email);
        et_name = (EditText)findViewById(R.id.signup_et_nickname);
        et_password = (EditText)findViewById(R.id.signup_et_password);
        btn_signup = (Button)findViewById(R.id.signup_btn_signup);
        progressBar = (ProgressBar)findViewById(R.id.signup_pb_progressBar);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = et_email.getText().toString();
                name = et_name.getText().toString();
                password = et_password.getText().toString();
                if(email.equals("") || name.equals("") || password.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Please input all information.",Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                btn_signup.setClickable(false);
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(et_email.getText().toString(), et_password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                progressBar.setVisibility(View.INVISIBLE);
                                btn_signup.setClickable(true);
                                uid = authResult.getUser().getUid();
                                User user = new User(uid, email, password, name);
                                FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(user);
                                Toast.makeText(getApplicationContext(), "Sign up Complete!",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), e.getMessage() ,Toast.LENGTH_SHORT).show();
                                btn_signup.setClickable(true);
                            }
                        });
            }
        });

    }

}

