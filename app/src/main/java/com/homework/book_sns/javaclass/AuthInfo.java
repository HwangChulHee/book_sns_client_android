package com.homework.book_sns.javaclass;

public class AuthInfo {
    String email;
    String signTime;

    public  AuthInfo() {
        email = null;
        signTime = null;
    }
    public AuthInfo(String email, String signTime) {
        this.email = email;
        this.signTime = signTime;
    }

    public void set_info(String email, String signTime) {
        this.email = email;
        this.signTime = signTime;
    }

    public String getEmail() {
        return email;
    }

    public String getSignTime() {
        return signTime;
    }
}
