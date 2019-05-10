package com.example.bookshelf;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;
import java.util.List;

/**
 * Created by lenovo on 2019/4/16.
 */

public class BookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Book> mBookList;
    private MainActivity mMainActivity;

    static class BookHolder extends RecyclerView.ViewHolder{
        ImageView imgBook;
        TextView txtBookTitle;
        TextView txtBookInfo;
        TextView txtBookTime;
        RelativeLayout book_view;
        View bookItemView;

        public BookHolder(View view){
            super(view);
            bookItemView = view;
            book_view = (RelativeLayout)view.findViewById(R.id.book_view);
            imgBook = (ImageView)view.findViewById(R.id.img_book);
            txtBookTitle = (TextView)view.findViewById(R.id.txt_book_title);
            txtBookInfo = (TextView)view.findViewById(R.id.txt_book_info);
            txtBookTime = (TextView)view.findViewById(R.id.txt_book_time);
        }
    }

    public BookAdapter(MainActivity mainActivity ,List<Book> bookList){
        this.mMainActivity = mainActivity;
        this.mBookList = bookList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lv_item_view, parent, false);
        final BookHolder holder = new BookHolder(view);


        holder.book_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Book book = mBookList.get(position);
                // TODO 每一本书的点击
                //Toast.makeText(view.getContext(), "hello "+position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), BookEditActivity.class);
                intent.putExtra("bookSelected", book);
                intent.putExtra("bookSelectedPos", position);
                mMainActivity.startActivityForResult(intent, MainActivity.EDIT_INTENT_CODE);
            }
        });

        holder.book_view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //TODO: 删除书籍
                int position = holder.getAdapterPosition();
                BookShelfManager.removeABook(mMainActivity.selectedBookshelf, position);
                mMainActivity.refreshBookList(MainActivity.bookShelfList.get(mMainActivity.selectedBookshelf));
                return false;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BookHolder bookHolder = (BookHolder)holder;
        Book book = mBookList.get(position);
        //TODO 设置书籍信息
        //bookHolder.txtBookInfo.setText();
        bookHolder.txtBookTitle.setText(book.getTitle());

        //TODO:空的判断有问题
        if(book.getAuthor().length()!=0 && book.getPubCompany().length()!=0)
            bookHolder.txtBookInfo.setText(book.getAuthor()+" 著，"+book.getPubCompany());
        else if(book.getAuthor().length()!=0 && book.getPubCompany().length()==0)
            bookHolder.txtBookInfo.setText(book.getAuthor()+" 著");
        else if(book.getAuthor().length()==0 && book.getPubCompany().length()!=0)
            bookHolder.txtBookInfo.setText(book.getPubCompany());

        if(book.getPubMonth().length()==0 || book.getPubYear().length()==0)
            bookHolder.txtBookTime.setText("未设置");
        else
            bookHolder.txtBookTime.setText(book.getPubYear()+"-"+book.getPubMonth());


        //Bitmap bitmap = BitmapFactory.decodeResource(mMainActivity.getResources(), R.drawable.test);
        Bitmap bitmap = ImageManager.GetLocalBitmap(mMainActivity, "testImg");
        bookHolder.imgBook.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        Log.d("temp", "getItemCount: "+mBookList.size());
       return mBookList.size();
    }
}