package com.facelesssinger.shim.homesinger;

import com.facelesssinger.shim.homesinger.Data.User;

import java.util.Comparator;

public class ScoreComparator implements Comparator<User> {


    public int compare(User o1, User o2) {
        return Float.valueOf(o2.getTotalScore()).compareTo(Float.valueOf(o1.getTotalScore()));
    }

}
