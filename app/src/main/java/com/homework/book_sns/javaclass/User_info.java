package com.homework.book_sns.javaclass;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

public class User_info implements Parcelable {
    private String user_id;
    private String user_nickname;
    private String user_profile;
    private boolean following;

    public User_info() {
    }

    public User_info(String user_id, String user_nickname, String user_profile) {
        this.user_id = user_id;
        this.user_nickname = user_nickname;
        this.user_profile = user_profile;
    }

    public User_info(Parcel parcel) {
        this.user_id = parcel.readString();
        this.user_nickname = parcel.readString();
        this.user_profile = parcel.readString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.following = parcel.readBoolean();
        }
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getUser_profile() {
        return user_profile;
    }

    public void setUser_profile(String user_profile) {
        this.user_profile = user_profile;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public static final Parcelable.Creator<User_info> CREATOR = new Parcelable.Creator<User_info>() {
        @Override
        public User_info createFromParcel(Parcel parcel) {
            return new User_info(parcel);
        }

        @Override
        public User_info[] newArray(int i) {
            return new User_info[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(user_id);
        parcel.writeString(user_nickname);
        parcel.writeString(user_profile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcel.writeBoolean(following);
        }
    }
}
