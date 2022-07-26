package com.homework.book_sns.javaclass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Chatting_list_info {
    User_info opponent;

    int room_id;
    int sender_id;
    String last_msg;
    String last_msg_time;
    String change_last_msg_time;
    int remain_msg_count;


    public Chatting_list_info(User_info opponent, int room_id, int sender_id,
                              String last_msg, String last_msg_time,
                              int remain_msg_count) {
        this.opponent = opponent;
        this.room_id = room_id;
        this.sender_id = sender_id;
        this.last_msg = last_msg;
        this.last_msg_time = last_msg_time;
        this.change_last_msg_time = setTime(last_msg_time);
        this.remain_msg_count = remain_msg_count;
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

    public User_info getOpponent() {
        return opponent;
    }

    public int getRoom_id() {
        return room_id;
    }

    public String getChange_last_msg_time() {
        return change_last_msg_time;
    }

    public String getLast_msg() {
        return last_msg;
    }

    public String getLast_msg_time() {
        return last_msg_time;
    }

    public int getRemain_msg_count() {
        return remain_msg_count;
    }

}
