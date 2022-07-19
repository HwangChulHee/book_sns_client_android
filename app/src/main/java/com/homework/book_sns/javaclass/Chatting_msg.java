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
    int read_count = 0;
    int room_numOfPeople; // 채팅방 인원
    String msg_type; // 메시지 타입 - 진짜 메시지냐, 아니면 읽음처리를 위한 메시지인가 구별..

    public Chatting_msg() {
    }

    public Chatting_msg(User_info user_info, int room_id, String msg, String time, int read_count, int room_numOfPeople) {
        this.user_info = user_info;
        this.room_id = room_id;
        this.msg = msg;
        this.time = setTime(time);
        this.read_count = read_count;
        this.room_numOfPeople = room_numOfPeople;
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


    public String getRead_status() {

        int read_status = room_numOfPeople - read_count;

        if(read_status == 0) {
            return null;
        } else {
            return Integer.toString(read_status);
        }

    }

    public void remove_time() {
        time = "";
    }

    public String convert_json() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public void setRead_count(int read_count) {
        this.read_count = read_count;
    }

    public void plusRead_count() {
        this.read_count++;
        if(this.read_count > this.room_numOfPeople) {
            this.read_count = this.room_numOfPeople;
        }
    }

    public String getMsg_type() {
        return msg_type;
    }
}
