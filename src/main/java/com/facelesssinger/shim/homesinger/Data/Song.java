package com.facelesssinger.shim.homesinger.Data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class Song implements Parcelable{
    String originalSongTitle;
    String originalSinger;
    String userNickName;
    String userId;
    String songType;
    String averageScore = "0";
    String viewCount = "0";
    String totalScore = "0";
    String numOfVoters = "0";
    String downloadUrl;
    String duration="0";
    String id;

    Object timeStamp;
    Map<String, Comment> commentOfSong = new HashMap<String,Comment>();
    Map<String, Boolean> idOfVoters = new HashMap<>();

    public Song() {
    }

    public Song(Parcel in){
        this.originalSongTitle = in.readString();
        this.originalSinger = in.readString();
        this.userNickName = in.readString();
        this.userId = in.readString();
        this.songType = in.readString();
        this.averageScore = in.readString();
        this.viewCount = in.readString();
        this.totalScore = in.readString();
        this.numOfVoters = in.readString();
        this.downloadUrl = in.readString();
        this.duration = in.readString();
        this.id = in.readString();

        this.timeStamp = (Object)in.readLong();
        in.readMap(commentOfSong, Comment.class.getClassLoader());
        in.readMap(idOfVoters, Boolean.class.getClassLoader());
    }

    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.originalSongTitle);
        dest.writeString(this.originalSinger);
        dest.writeString(this.userNickName);
        dest.writeString(this.userId);
        dest.writeString(this.songType);
        dest.writeString(this.averageScore);
        dest.writeString(this.viewCount);
        dest.writeString(this.totalScore);
        dest.writeString(this.numOfVoters);
        dest.writeString(this.downloadUrl);
        dest.writeString(this.duration);
        dest.writeString(this.id);
        dest.writeLong((Long)this.timeStamp);
        dest.writeMap(this.commentOfSong);
        dest.writeMap(this.idOfVoters);
    }

    public String getOriginalSongTitle() {
        return originalSongTitle;
    }

    public void setOriginalSongTitle(String originalSongTitle) {
        this.originalSongTitle = originalSongTitle;
    }

    public String getOriginalSinger() {
        return originalSinger;
    }


    public void setOriginalSinger(String originalSinger) {
        this.originalSinger = originalSinger;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSongType() {
        return songType;
    }

    public void setSongType(String songType) {
        this.songType = songType;
    }

    public String getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(String averageScore) {
        this.averageScore = averageScore;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    public String getNumOfVoters() {
        return numOfVoters;
    }

    public void setNumOfVoters(String numOfVoters) {
        this.numOfVoters = numOfVoters;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getViewCount() {
        return viewCount;
    }

    public String getDuration() {
        return duration;
    }

    public Map<String, Comment> getCommentOfSong() {
        return commentOfSong;
    }

    public void setCommentOfSong(Map<String, Comment> commentOfSong) {
        this.commentOfSong = commentOfSong;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Boolean> getIdOfVoters() {
        return idOfVoters;
    }

    public void setIdOfVoters(Map<String, Boolean> idOfVoters) {
        this.idOfVoters = idOfVoters;
    }
}
