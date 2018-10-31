package com.facelesssinger.shim.homesinger.Data;

import java.util.HashMap;

public class User {
    String id;
    String email;
    String password;
    String nickName;
    String numOfFollowers;
    String totalAverageScore = "0";
    String totalScore = "0";
    String numOfMySong = "0";
    String averageOfAllSong = "0";
    String token;

    HashMap<String, Boolean> myFollowingList = new HashMap<>();
    HashMap<String, Boolean> mySongList = new HashMap<>();

    public User() {
    }

    public User(String id, String email, String password, String nickName) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickName = nickName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNumOfFollowers() {
        return numOfFollowers;
    }

    public void setNumOfFollowers(String numOfFollowers) {
        this.numOfFollowers = numOfFollowers;
    }

    public HashMap<String, Boolean> getMyFollowingList() {
        return myFollowingList;
    }

    public void setMyFollowingList(HashMap<String, Boolean> myFollowingList) {
        this.myFollowingList = myFollowingList;
    }

    public HashMap<String, Boolean> getMySongList() {
        return mySongList;
    }

    public void setMySongList(HashMap<String, Boolean> mySongList) {
        this.mySongList = mySongList;
    }

    public String getTotalAverageScore() {
        return totalAverageScore;
    }

    public void setTotalAverageScore(String totalAverageScore) {
        this.totalAverageScore = totalAverageScore;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    public String getNumOfMySong() {
        return numOfMySong;
    }

    public void setNumOfMySong(String numOfMySong) {
        this.numOfMySong = numOfMySong;
    }

    public String getAverageOfAllSong() {
        return averageOfAllSong;
    }

    public void setAverageOfAllSong(String averageOfAllSong) {
        this.averageOfAllSong = averageOfAllSong;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

