package com.example.bookshelf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2019/4/16.
 */

public class BookShelf implements Serializable{
    private String bookshelfName;
    private List<Book> bookList;

    public BookShelf(String bookshelfName, List<Book> bookList){
        this.bookshelfName = bookshelfName;
        this.bookList = bookList;
    }

    public String getBookshelfName() {
        return bookshelfName;
    }

    public void setBookshelfName(String bookshelfName) {
        this.bookshelfName = bookshelfName;
    }

    public List<Book> getBookList() {
        return bookList;
    }

    public void setBookList(List<Book> bookList) {
        this.bookList = bookList;
    }
}
