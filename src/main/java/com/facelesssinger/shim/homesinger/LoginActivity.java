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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button btn_login;
    private Button btn_signup;
    private ProgressBar pb_progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private EditText et_email;
    private EditText et_password;

    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(com.facelesssinger.shim.homesinger.R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();

        btn_login = (Button)findViewById(com.facelesssinger.shim.homesinger.R.id.login_btn_login);
        btn_signup = (Button)findViewById(com.facelesssinger.shim.homesinger.R.id.login_btn_signup);

        et_email = (EditText)findViewById(com.facelesssinger.shim.homesinger.R.id.login_et_id);
        et_password = (EditText)findViewById(com.facelesssinger.shim.homesinger.R.id.login_et_password);

        pb_progressBar = (ProgressBar)findViewById(R.id.login_progressBar);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                finish();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = et_email.getText().toString();
                password = et_password.getText().toString();

                if(email.equals("") || password.equals(""))
                {
                    Toast.makeText(LoginActivity.this, "please input all information.", Toast.LENGTH_SHORT).show();
                    return;
                }
                btn_login.setClickable(false);
                pb_progressBar.setVisibility(View.VISIBLE);

                isLogin();

            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null) {
                    //로그인 시
                    Toast.makeText(getApplicationContext(),"Welcome! Login Success",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
                else {
                    //로그아웃 시

                }
            }
        };

    }


    public void isLogin()
    {
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                btn_login.setClickable(true);
                pb_progressBar.setVisibility(View.INVISIBLE);

                if(!task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}
