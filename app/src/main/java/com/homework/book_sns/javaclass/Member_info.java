package com.homework.book_sns.javaclass;

public class Member_info {
    private int member_id;
    private String member_name;
    private String member_image;

    private int group_id;
    private boolean isApply; // true면 멤버고, false면 지원자..

    public Member_info(int member_id, String member_name, String member_image) {
        this.member_id = member_id;
        this.member_name = member_name;
        this.member_image = member_image;
    }

    public int getMember_id() {
        return member_id;
    }

    public void setMember_id(int member_id) {
        this.member_id = member_id;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public String getMember_image() {
        return member_image;
    }

    public void setMember_image(String member_image) {
        this.member_image = member_image;
    }

    public boolean isApply() {
        return isApply;
    }

    public void setApply(boolean apply) {
        isApply = apply;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }
}
