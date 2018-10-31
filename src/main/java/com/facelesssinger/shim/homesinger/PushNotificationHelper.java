package com.facelesssinger.shim.homesinger;

import com.facelesssinger.shim.homesinger.Data.NotificationModel;
import com.facelesssinger.shim.homesinger.Data.Song;
import com.google.gson.Gson;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PushNotificationHelper {
    public static final int FLAG_NOTIFICATION_COMMENT = 100;
    public static final int FLAG_NOTIFICATION_FOLLOWER = 101;
    public static final int FLAG_NOTIFICATION_EVALUATE = 102;

    public static final String AUTH_KEY_FCM = "AIzaSyDDh8StR66p4rdROGKMVKYxh-FUxlhYs4w";
    public static final String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

    public void sendGcm(String token, int flag, Song song)
    {
        Gson gson = new Gson();
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.setTo(token);
        String notiTitle = null;
        String notiText = null;
        switch (flag){
            case FLAG_NOTIFICATION_COMMENT : {
                notiTitle = "새로운 댓글";
                notiText = MainActivity.currentUser.getNickName()+"님이 댓글을 남겼습니다.("+song.getUserNickName()+" - "+song.getOriginalSongTitle()+")";
                break;
            }
            case FLAG_NOTIFICATION_FOLLOWER : {
                notiTitle = "새로운 팔로워";
                notiText = MainActivity.currentUser.getNickName()+"님이 회원님을 팔로우합니다.";
                break;
            }
            case FLAG_NOTIFICATION_EVALUATE : {
                notiTitle = "새로운 평가";
                notiText = "회원님의 노래가 평가되었습니다." + "(" + song.getUserNickName()+ " - " + song.getOriginalSongTitle() + ")";
                break;
            }
        }
        notificationModel.getNotification().setTitle(notiTitle);
        notificationModel.getNotification().setText(notiText);
//        notificationModel.getData().setTitle("data title");
//        notificationModel.getData().setText("data text");

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
}