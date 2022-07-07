package com.homework.book_sns.javaclass;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Chatting_msg {
    User_info user_info;
    int room_id;
    String msg;
    String time;
    int read_count;

    public Chatting_msg() {
    }

    public Chatting_msg(User_info user_info, int room_id, String msg, String time, int read_count) {
        this.user_info = user_info;
        this.room_id = room_id;
        this.msg = msg;
        this.time = setTime(time);
        this.read_count = read_count;
    }

    private String setTime(String time) {
        SimpleDateFormat input_format    = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 입력포멧
        SimpleDateFormat update_format = new SimpleDateFormat("a hh:mm"); // 년월일시분초 14자리 포멧

        try {
            String str_source = time;         //입력포멧 문자열
            Date date_parsed = input_format.parse(str_source); // 문자열을 파싱해 Date형으로 저장한다
            return update_format.format(date_parsed);
//            System.out.println(update_format.format(date_parsed)); // 14자리 포멧으로 출력한다
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public User_info getUser_info() {
        return user_info;
    }

    public int getRoom_id() {
        return room_id;
    }

    public String getMsg() {
        return msg;
    }

    public String getTime() {
        return time;
    }

    public int getRead_count() {
        return read_count;
    }

    public void remove_time() {
        time = "";
    }

    public String convert_json() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }


}
