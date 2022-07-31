package com.homework.book_sns.javaclass;

public class Group_info {
    private int group_id;
    private String group_name;
    private String group_category;
    private String group_explain;
    private String group_image;

    private boolean isMember;
    private boolean isLeader;
    private boolean isApply;

    public Group_info(int group_id, String group_name, String group_category, String group_explain, String group_image) {
        this.group_id = group_id;
        this.group_name = group_name;
        this.group_category = group_category;
        this.group_explain = group_explain;
        this.group_image = group_image;
    }

    public void setMember(boolean member) {
        isMember = member;
    }

    public void setLeader(boolean leader) {
        isLeader = leader;
    }


    public int getGroup_id() {
        return group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public String getGroup_category() {
        return group_category;
    }

    public String getGroup_explain() {
        return group_explain;
    }

    public String getGroup_image() {
        return group_image;
    }

    public boolean isMember() {
        return isMember;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public boolean isApply() {
        return isApply;
    }

    public void setApply(boolean apply) {
        isApply = apply;
    }
}
