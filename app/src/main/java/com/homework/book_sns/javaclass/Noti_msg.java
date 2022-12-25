package com.homework.book_sns.javaclass;

import com.google.gson.Gson;

public class Noti_msg {
    private String type; // noti(알림), enter(사용자 입장), active(알림 활성화 요청), inactive(알림 비활성화 요청)이 있다.
    private String content; // Noti_info(알림 정보), user_id가 올 수 있다.

    public Noti_msg() {
    }

    public Noti_msg(String type, String content) {
        this.type = type;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
