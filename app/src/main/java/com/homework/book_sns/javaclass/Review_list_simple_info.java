package com.homework.book_sns.javaclass;

import java.util.ArrayList;

public class Review_list_simple_info {
    private User_info writer_info;
    private Book_info book_info;

    private String review_id; // 리뷰의 id
    private String review_user_id; // 리뷰 작성자의 id

    private String review_text;
    private ArrayList<String> review_images = new ArrayList<>();
    private String writeDate;
    private int recommendCount; // 추천 수
    private int replyCount; // 댓글 수
    
    private boolean client_recommendation; // (클라이언트의) 추천 여부
    private boolean following = false; // (클라이언트의) 팔로잉 여부


    public User_info getUser_info() {
        return writer_info;
    }

    public void setUser_info(User_info user_info) {
        this.writer_info = user_info;
    }

    public Book_info getBook_info() {
        return book_info;
    }

    public void setBook_info(Book_info book_info) {
        this.book_info = book_info;
    }

    public String getReview_id() {
        return review_id;
    }

    public void setReview_id(String review_id) {
        this.review_id = review_id;
    }

    public String getReview_user_id() {
        return review_user_id;
    }

    public void setReview_user_id(String review_user_id) {
        this.review_user_id = review_user_id;
    }

    public String getReview_text() {
        return review_text;
    }

    public void setReview_text(String review_text) {
        this.review_text = review_text;
    }

    public ArrayList<String> getReview_images() {
        return review_images;
    }

    public void setReview_images(ArrayList<String> review_images) {
        this.review_images = review_images;
    }

    public String getWriteDate() {
        return writeDate;
    }

    public void setWriteDate(String writeDate) {
        this.writeDate = writeDate;
    }

    public int getRecommendCount() {
        return recommendCount;
    }

    public void setRecommendCount(int recommendCount) {
        this.recommendCount = recommendCount;
    }

    public int getReplyCount() {
        return replyCount;
    }
    public void addRecommendCount() {
        recommendCount++;
    }
    public void minusRecommendCount() {
        recommendCount--;
    }
    public void addReplyCount() {
        replyCount++;
    }
    public void minusReplyCount() {
        replyCount--;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public boolean isClient_recommendation() {
        return client_recommendation;
    }

    public void setClient_recommendation(boolean client_recommendation) {
        this.client_recommendation = client_recommendation;
    }
}
