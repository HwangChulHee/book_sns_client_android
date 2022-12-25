package com.homework.book_sns.javaclass;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Noti_info implements Parcelable {
    int user_id;
    String user_name;
    String user_profile;

    int target_user_id;
    String noti_content;
    String noti_date;
    String noti_type;

    int noti_page_id;
    int noti_reply_id;

    boolean target_noti_active;
    boolean read_status;

    public Noti_info(int user_id, String user_name, String user_profile, int target_user_id, String noti_type, int noti_page_id, int noti_reply_id) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_profile = user_profile;
        this.target_user_id = target_user_id;
        this.noti_type = noti_type;
        this.noti_page_id = noti_page_id;
        this.noti_reply_id = noti_reply_id;


        setNoti_content();
        setNoti_date();
        this.target_noti_active = true;
        this.read_status = false;
    }

    public Noti_info(Parcel parcel) {
        this.user_id = parcel.readInt();
        this.user_name = parcel.readString();
        this.user_profile = parcel.readString();

        this.target_user_id = parcel.readInt();
        this.noti_content = parcel.readString();
        this.noti_date = parcel.readString();
        this.noti_type = parcel.readString();

        this.noti_page_id = parcel.readInt();
        this.noti_reply_id = parcel.readInt();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.target_noti_active = parcel.readBoolean();
            this.read_status = parcel.readBoolean();
        }

    }

    private void setNoti_content() {
        if(noti_type.equals("추천")) {
            this.noti_content = user_name+"님이 회원님의 게시글을 추천하였습니다.";
        } else if(noti_type.equals("댓글")) {
            this.noti_content = user_name+"님이 회원님의 게시글에 댓글을 남겼습니다.";
        } else if(noti_type.equals("답글")) {
            this.noti_content = user_name+"님이 회원님의 게시글에 답글을 남겼습니다.";
        } else if(noti_type.equals("팔로우")) {
            this.noti_content = user_name+"님이 회원님을 팔로우하였습니다.";
        } else if(noti_type.equals("모임신청")) {

        } else if(noti_type.equals("모임수락")) {

        } else if(noti_type.equals("모임거절")) {

        }
    }

    private void setNoti_date() {
        Date nowDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM월 dd일 HH시 mm분");

        this.noti_date = sdf.format(nowDate);
    }

    public String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getNoti_type() {
        return noti_type;
    }

    public String getNoti_content() {
        return noti_content;
    }

    public boolean isTarget_noti_active() {
        return target_noti_active;
    }

    public int getNoti_page_id() {
        return noti_page_id;
    }

    public String getUser_profile() {
        return user_profile;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getNoti_date() {
        return noti_date;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getTarget_user_id() {
        return target_user_id;
    }

    public int getNoti_reply_id() {
        return noti_reply_id;
    }

    public boolean isRead_status() {
        return read_status;
    }

    public void setRead_status(boolean read_status) {
        this.read_status = read_status;
    }

    public boolean isSame(Noti_info ni) {

        if(ni.getUser_id() == this.user_id
                && ni.getNoti_type().equals(this.noti_type)
                && ni.getNoti_page_id() == this.noti_page_id
                && ni.getNoti_reply_id() == this.noti_reply_id
                && ni.getTarget_user_id() == this.target_user_id
            ){
           return true;
        } else {
            return false;
        }

    }

    public void setNoti_date(String noti_date) {
        this.noti_date = noti_date;
    }


    public static final Parcelable.Creator<Noti_info> CREATOR = new Parcelable.Creator<Noti_info>() {
        @Override
        public Noti_info createFromParcel(Parcel parcel) {
            return new Noti_info(parcel);
        }

        @Override
        public Noti_info[] newArray(int i) {
            return new Noti_info[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeInt(this.user_id);
        parcel.writeString(this.user_name);
        parcel.writeString(this.user_profile);

        parcel.writeInt(this.target_user_id);
        parcel.writeString(this.noti_content);
        parcel.writeString(this.noti_date);
        parcel.writeString(this.noti_type);

        parcel.writeInt(this.noti_page_id);
        parcel.writeInt(this.noti_reply_id);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcel.writeBoolean(this.target_noti_active);
            parcel.writeBoolean(this.read_status);
        }
    }



}
