package com.facelesssinger.shim.homesinger.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable{
    String id;
    String nickName;
    String comment;
    Object timeStamp; //(몇분전 기능, 몇일전 기능, 1주일 최대범위로)

    public Comment() {
    }


    public Comment(Parcel in){
        this.id = in.readString();
        this.nickName = in.readString();
        this.comment = in.readString();
        this.timeStamp = (Object)in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.nickName);
        dest.writeString(this.comment);
        dest.writeLong((Long)this.timeStamp);

    }

    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }
}
