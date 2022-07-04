package com.homework.book_sns.javaclass;

import android.os.Parcel;
import android.os.Parcelable;

public class Follow implements Parcelable {
    String object_person_id;
    String client_id;
    String user_nickname;
    String follow_type;

    public Follow() {

    }

    public Follow(Parcel parcel) {
        this.object_person_id = parcel.readString();
        this.client_id = parcel.readString();
        this.user_nickname = parcel.readString();
        this.follow_type = parcel.readString();
    }

    public String getObject_person_id() {
        return object_person_id;
    }

    public void setObject_person_id(String object_person_id) {
        this.object_person_id = object_person_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getFollow_type() {
        return follow_type;
    }

    public void setFollow_type(String follow_type) {
        this.follow_type = follow_type;
    }


    public static final  Parcelable.Creator<Follow> CREATOR = new Parcelable.Creator<Follow>() {
        @Override
        public Follow createFromParcel(Parcel parcel) {
            return new Follow(parcel);
        }

        @Override
        public Follow[] newArray(int i) {
            return new Follow[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(object_person_id);
        parcel.writeString(client_id);
        parcel.writeString(user_nickname);
        parcel.writeString(follow_type);
    }
}
