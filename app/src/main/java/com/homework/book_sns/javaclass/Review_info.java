package com.homework.book_sns.javaclass;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.ArrayList;

public class Review_info {
    private Book_info book_info;
    private User_info user_info;
    private String review_text;
    private String review_id;
    private ArrayList<String> review_images = new ArrayList<>();
    private ArrayList<Uri> uriArrayList = new ArrayList<>();
    private ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();

    private String writeDate; // 작성일
    private int recommendCount; // 추천 수
    private int commentCount; // 댓글 수


    public Book_info getBook_info() {
        return book_info;
    }

    public void setBook_info(Book_info book_info) {
        this.book_info = book_info;
    }

    public String getReview_text() {
        return review_text;
    }

    public void setReview_text(String review_text) {
        this.review_text = review_text;
    }

    public ArrayList<Uri> getUriArrayList() {
        return uriArrayList;
    }

    public void setUriArrayList(ArrayList<Uri> uriArrayList) {
        this.uriArrayList = uriArrayList;
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

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public ArrayList<Bitmap> getBitmapArrayList() {
        return bitmapArrayList;
    }

    public void setBitmapArrayList(ArrayList<Bitmap> bitmapArrayList) {
        this.bitmapArrayList = bitmapArrayList;
    }

    public User_info getUser_info() {
        return user_info;
    }

    public void setUser_info(User_info user_info) {
        this.user_info = user_info;
    }

    public String getReview_id() {
        return review_id;
    }

    public void setReview_id(String review_id) {
        this.review_id = review_id;
    }

    public ArrayList<String> getReview_images() {
        return review_images;
    }

    public void setReview_images(ArrayList<String> review_images) {
        this.review_images = review_images;
    }
}
