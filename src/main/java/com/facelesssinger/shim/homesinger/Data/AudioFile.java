package com.facelesssinger.shim.homesinger.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class AudioFile implements Parcelable{
    String path;
    String name;
    long durationMills;
    long dateTime;

    public AudioFile() {

    }

    public AudioFile(Parcel in) {
        path = in.readString();
        name = in.readString();
        durationMills = in.readLong();
        dateTime = in.readLong();
    }

    public AudioFile(String path, String name, long durationMills, long dateTime) {
        this.path = path;
        this.name = name;
        this.durationMills = durationMills;
        this.dateTime = dateTime;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDurationMills() {
        return durationMills;
    }

    public void setDurationMills(long durationMills) {
        this.durationMills = durationMills;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public static final Parcelable.Creator<AudioFile> CREATOR = new Parcelable.Creator<AudioFile>() {
        public AudioFile createFromParcel(Parcel in) {
            return new AudioFile(in);
        }

        public AudioFile[] newArray(int size) {
            return new AudioFile[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(path);
            dest.writeString(name);
            dest.writeLong(durationMills);
            dest.writeLong(dateTime);
    }
}
