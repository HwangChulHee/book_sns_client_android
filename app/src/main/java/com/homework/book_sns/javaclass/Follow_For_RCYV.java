package com.homework.book_sns.javaclass;

public class Follow_For_RCYV {
    String user_id;
    String user_nickname;
    String profile_photo;
    boolean client_relationship;

    int review_count;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public int getReview_count() {
        return review_count;
    }

    public void setReview_count(int review_count) {
        this.review_count = review_count;
    }

    public boolean isClient_relationship() {
        return client_relationship;
    }

    public void setClient_relationship(boolean client_relationship) {
        this.client_relationship = client_relationship;
    }
}
