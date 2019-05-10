package com.example.bookshelf;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by lenovo on 2019/4/16.
 */

public class Book implements Serializable{
    public static int UNREAD = 0;
    public static int READING = 1;
    public static int FINISH = 2;

    private String title="";
    private String author="";
    private String pubCompany="";
    private String pubYear = "";
    private String pubMonth = "";
    private String isbn="";
    private int readStatus = Book.UNREAD;
    private String bookShelfName="";
    private String bookNote="";
    private String tag="";
    private String website="";
    private String uuid="";

    public Book(){
        uuid = UUID.randomUUID().toString().replaceAll("-", "");
    }

    public Book(String title){
        this();
        this.title = title;
    }


    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getPubYear() {
        return pubYear;
    }

    public void setPubYear(String pubYear) {
        this.pubYear = pubYear;
    }

    public String getPubMonth() {
        return pubMonth;
    }

    public void setPubMonth(String pubMonth) {
        this.pubMonth = pubMonth;
    }

    public String getPubCompany() {
        return pubCompany;
    }

    public void setPubCompany(String pubCompany) {
        this.pubCompany = pubCompany;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public int getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(int readStatus) {
        this.readStatus = readStatus;
    }

    public String getBookShelfName() {
        return bookShelfName;
    }

    public void setBookShelfName(String bookShelfName) {
        this.bookShelfName = bookShelfName;
    }

    public String getBookNote() {
        return bookNote;
    }

    public void setBookNote(String bookNote) {
        this.bookNote = bookNote;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}
