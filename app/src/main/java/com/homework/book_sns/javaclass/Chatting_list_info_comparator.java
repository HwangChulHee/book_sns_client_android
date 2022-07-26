package com.homework.book_sns.javaclass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class Chatting_list_info_comparator implements Comparator<Chatting_list_info> {
    @Override
    public int compare(Chatting_list_info chatting_list_info, Chatting_list_info t1) {
        SimpleDateFormat input_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 입력포멧

        try {
            Date date1 = input_format.parse(chatting_list_info.getLast_msg_time());
            Date date2 = input_format.parse(t1.getLast_msg_time());

            int result = date1.compareTo(date2);

            if(result > 0) { // date1이 date2보다 이후일때
                return -1;
            } else if(result < 0) { // date1이 date2보다 이전일때
                return 1;
            } else {
                return 0;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }

    }
}
