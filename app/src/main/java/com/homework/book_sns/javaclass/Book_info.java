package com.homework.book_sns.javaclass;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class Book_info implements Parcelable{
    private String title; // 상품명
    private String author; // 저자
    private String publisher; // 출판사
    private String pubDate; // 출간일
    private String description; // 상품 설명
    private String cover; // 표지
    private String category; // 카테고리 (추후 추가예정)
    private String isbn13; // ISBN


    public Book_info() {

    }

    public Book_info(String title, String author, String publisher, String pubDate,
                     String description, String cover, String isbn13) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.pubDate = pubDate;
        this.description = description;
        this.cover = cover;
        this.isbn13 = isbn13;
    }

    public Book_info(Parcel parcel) {
        this.title = parcel.readString();
        this.author = parcel.readString();
        this.publisher = parcel.readString();
        this.pubDate = parcel.readString();
        this.description = parcel.readString();
        this.cover = parcel.readString();
        this.category = parcel.readString();
        this.isbn13 = parcel.readString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getJsonString() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("title", this.title);
            jsonObject.put("author", this.author);
            jsonObject.put("publisher", this.publisher);
            jsonObject.put("pubDate", this.pubDate);
            jsonObject.put("description", this.description);
            jsonObject.put("cover", this.cover);
            jsonObject.put("category", this.category);
            jsonObject.put("isbn13", this.isbn13);


        } catch (Exception e) {
            return null;
        }

        return jsonObject.toString();
    }


    public static final Parcelable.Creator<Book_info> CREATOR = new Parcelable.Creator<Book_info>() {
        @Override
        public Book_info createFromParcel(Parcel parcel) {
            return new Book_info(parcel);
        }

        @Override
        public Book_info[] newArray(int i) {
            return new Book_info[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(author);
        parcel.writeString(publisher);
        parcel.writeString(pubDate);
        parcel.writeString(description);
        parcel.writeString(cover);
        parcel.writeString(category);
        parcel.writeString(isbn13);
    }
}
