package com.facelesssinger.shim.homesinger;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.facelesssinger.shim.homesinger.Data.User;
import com.facelesssinger.shim.homesinger.Fragment.HomeFragment;
import com.facelesssinger.shim.homesinger.Fragment.ListenFragment;
import com.facelesssinger.shim.homesinger.Fragment.MyRecordingFragment;
import com.facelesssinger.shim.homesinger.Fragment.RankFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static User currentUser;
    private Fragment[] fragments =
            {new HomeFragment(), new ListenFragment(), new RankFragment(), new MyRecordingFragment()};

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private MyPagerAdapter pagerAdapter;

    private final String[] permissionArray= {Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(com.facelesssinger.shim.homesinger.R.layout.activity_main);

        tabLayout = (TabLayout)findViewById(com.facelesssinger.shim.homesinger.R.id.main_tabLayout);
        viewPager = (ViewPager)findViewById(com.facelesssinger.shim.homesinger.R.id.main_viewPager);

        getPermission(permissionArray);
        initCurrentUser();
        }


    public class MyPagerAdapter extends FragmentPagerAdapter{
        private Fragment[] fragments;

        public MyPagerAdapter(FragmentManager fm, Fragment[] fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:{
                    return "홈";
                }
                case 1:{
                    return "듣기";
                }
                case 2:{
                    return "랭킹";
                }
                case 3:{
                    return "녹음실";
                }
                default:{
                    return "";
                }
            }
        }
    }

    public void setTapIcon(){
        tabLayout.getTabAt(0).setIcon(com.facelesssinger.shim.homesinger.R.drawable.home);
        tabLayout.getTabAt(1).setIcon(com.facelesssinger.shim.homesinger.R.drawable.listen);
        tabLayout.getTabAt(2).setIcon(com.facelesssinger.shim.homesinger.R.drawable.rank);
        tabLayout.getTabAt(3).setIcon(com.facelesssinger.shim.homesinger.R.drawable.record);
    }

    public void getPermission(String[] permissionArray){
        for(String permission : permissionArray){
            if(ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{permission}, 100);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_PERMISSIONS_REQUEST_RECORD_AUDIO){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //오디오 퍼미션 획득시 실행될 내용
            }
        }
    }

    public void initCurrentUser(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance()
                .getReference("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        currentUser = dataSnapshot.getValue(User.class);
                        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragments);
                        viewPager.setAdapter(pagerAdapter);
                        tabLayout.setupWithViewPager(viewPager);
                        setTapIcon();
                        saveTokenToDB();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void saveTokenToDB(){
        String token = FirebaseInstanceId.getInstance().getToken();
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        FirebaseDatabase.getInstance().getReference("users").child(currentUser.getId()).updateChildren(map);
    }
}
