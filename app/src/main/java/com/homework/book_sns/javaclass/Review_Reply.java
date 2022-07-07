package com.homework.book_sns.javaclass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Review_Reply {

    User_info user_info; // 유저 정보. user_id만 보내주고, 응답 시, 해당 정보를 세팅해주자.

    String review_board_id; // 댓글을 달 리뷰의 id (요청 시 알 수 있음)
    String reply_content; // 댓글의 내용 (요청 시 알 수 있음)
    String reply_class; // 댓글의 계층 - 댓글은 1, 답글은 2 (요청 시 알 수 있음)
    String group_num; // 댓글의 group. 0부터 시작. (답글은 바로 요청 시 알 수 있으나, 댓글은 응답시 알 수 있음. 댓글은 null 로 보내자)
    String tag_user_id; // (요청 시 알 수 있음. 댓글은 null, 답글은 user 의 id)
    String tag_user_nickname;
    

    String reply_id; // 댓글(또는 답글)의 id (응답 시 알 수 있음)
    String recommendation_count; // 댓글 또는 답글의 추천 수. (응답 시 알 수 있음)
    String reply_register_date; // (요청 시에도 알 수 있으나...응답 시 가져오는 걸로 하자.)
    boolean isClient_recommendation; //유저가 추천했는지에 대한 여부 (응답 시 알 수 있음)
    
    boolean isRemoved; // 댓글의 삭제 유무


    public User_info getUser_info() {
        return user_info;
    }

    public void setUser_info(User_info user_info) {
        this.user_info = user_info;
    }

    public String getReview_board_id() {
        return review_board_id;
    }

    public void setReview_board_id(String review_board_id) {
        this.review_board_id = review_board_id;
    }

    public String getReply_content() {
        return reply_content;
    }

    public void setReply_content(String reply_content) {
        this.reply_content = reply_content;
    }

    public String getReply_class() {
        return reply_class;
    }

    public void setReply_class(String reply_class) {
        this.reply_class = reply_class;
    }

    public String getGroup_num() {
        return group_num;
    }

    public void setGroup_num(String group_num) {
        this.group_num = group_num;
    }

    public String getTag_user_id() {
        return tag_user_id;
    }

    public void setTag_user_id(String tag_user_id) {
        this.tag_user_id = tag_user_id;
    }

    public String getTag_user_nickname() {
        return tag_user_nickname;
    }

    public void setTag_user_nickname(String tag_user_nickname) {
        this.tag_user_nickname = tag_user_nickname;
    }

    public String getReply_id() {
        return reply_id;
    }

    public void setReply_id(String reply_id) {
        this.reply_id = reply_id;
    }

    public String getRecommendation_count() {
        return recommendation_count;
    }

    public void setRecommendation_count(String recommendation_count) {
        this.recommendation_count = recommendation_count;
    }

    public String getReply_register_date() {
        return reply_register_date;
    }

    public void setReply_register_date(String reply_register_date) {
        SimpleDateFormat input_format    = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 입력포멧
        SimpleDateFormat update_format = new SimpleDateFormat("yyyy-MM-dd a hh:mm"); // 년월일시분초 14자리 포멧

        try {
            String str_source = reply_register_date;         //입력포멧 문자열
            Date date_parsed = input_format.parse(str_source); // 문자열을 파싱해 Date형으로 저장한다
            this.reply_register_date = update_format.format(date_parsed);
//            System.out.println(update_format.format(date_parsed)); // 14자리 포멧으로 출력한다
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    public boolean isClient_recommendation() {
        return isClient_recommendation;
    }

    public void setClient_recommendation(boolean client_recommendation) {
        isClient_recommendation = client_recommendation;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public void setRemoved(boolean removed) {
        isRemoved = removed;
    }
}
