package com.example.bookshelf;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookEditActivity extends AppCompatActivity {

    public static final int IMAGE_INTENT  = 1;

    int selectedBookshelf;
    int selectedStatus = 0;
    int selectedBookPos = 0;


    Book book;
    ImageView book_cover_img;
    EditText cover_book_title_edit;
    EditText cover_book_author_edit;
    //EditText cover_book_translator_edit;
    EditText cover_book_publisher_edit;
    EditText cover_book_publ_year_edit;
    EditText cover_book_publ_month_edit;
    EditText cover_book_isbn_edit;

    TextView be_detail_var_text_view;
    Spinner be_reading_status_spinner;
    Spinner be_bookshelf_spinner;
    EditText be_note_edit;
    EditText be_label_edit;
    EditText be_web_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_edit);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_activity_book_edit);
        setSupportActionBar(toolbar);

        book_cover_img = (ImageView)findViewById(R.id.book_cover_img);
        cover_book_title_edit = (EditText)findViewById(R.id.cover_book_title_edit);
        cover_book_author_edit = (EditText)findViewById(R.id.cover_book_author_edit);
        //EditText cover_book_translator_edit = (EditText)findViewById(R.id.cover_book_translator_edit);
        cover_book_publisher_edit = (EditText)findViewById(R.id.cover_book_publisher_edit);
        cover_book_publ_year_edit = (EditText)findViewById(R.id.cover_book_publ_year_edit);
        cover_book_publ_month_edit = (EditText)findViewById(R.id.cover_book_publ_month_edit);
        cover_book_isbn_edit = (EditText)findViewById(R.id.cover_book_isbn_edit);

        be_detail_var_text_view = (TextView)findViewById(R.id.be_detail_var_text_view);  ////详情来源
        be_reading_status_spinner = (Spinner)findViewById(R.id.be_reading_status_spinner);
        be_bookshelf_spinner = (Spinner)findViewById(R.id.be_bookshelf_spinner);
        be_note_edit = (EditText)findViewById(R.id.be_note_edit);
        be_label_edit = (EditText)findViewById(R.id.be_label_edit);  ////弹出标签选择对话框
        be_web_edit = (EditText)findViewById(R.id.be_web_edit);

        Intent intent = getIntent();
        book= (Book)intent.getSerializableExtra("bookSelected");
        selectedBookshelf = MainActivity.selectedBookshelf;
        if(book!=null){
            //TODO: 图片
            Bitmap bitmap = ImageManager.GetLocalBitmap(BookEditActivity.this, book.getUuid());
            if(bitmap != null){
                book_cover_img.setImageBitmap(bitmap);
            }


            cover_book_title_edit.setText(book.getTitle());
            cover_book_author_edit.setText(book.getAuthor());
            cover_book_publisher_edit.setText(book.getPubCompany());
            cover_book_publ_year_edit.setText(book.getPubYear());
            cover_book_publ_month_edit.setText(book.getPubMonth());
            cover_book_isbn_edit.setText(book.getIsbn());
            be_note_edit.setText(book.getBookNote());
            be_label_edit.setText(book.getTag());
            be_web_edit.setText(book.getWebsite());

            selectedBookPos = intent.getIntExtra("bookSelectedPos", 0);
            selectedStatus = book.getReadStatus();
        }

        //点击图片打开本地相册
        book_cover_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_img = new Intent(Intent.ACTION_PICK, null);
                intent_img.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*");
                startActivityForResult(intent_img, BookEditActivity.IMAGE_INTENT);
            }
        });


        List<String> statusNames = new ArrayList<String>();
        statusNames.add("未读");
        statusNames.add("阅读中");
        statusNames.add("已读");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(BookEditActivity.this,
                R.layout.support_simple_spinner_dropdown_item, statusNames);
        be_reading_status_spinner.setAdapter(spinnerAdapter);
        be_reading_status_spinner.setSelection(selectedStatus);
        be_reading_status_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO 选中某项后
                selectedStatus = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        refreshBookShelfSpinner(selectedBookshelf);
    }

    public void refreshBookShelfSpinner(int selected){
        final ArrayList<String> bookshelfNames = new ArrayList<String>();
        for(int i=0;i<MainActivity.bookShelfList.size(); i++){
            bookshelfNames.add(MainActivity.bookShelfList.get(i).getBookshelfName());
        }
        bookshelfNames.add("添加新书架");
        ArrayAdapter<String> spinnerAdapter2 = new ArrayAdapter<String>(BookEditActivity.this,
                R.layout.support_simple_spinner_dropdown_item, bookshelfNames);
        be_bookshelf_spinner.setAdapter(spinnerAdapter2);
        be_bookshelf_spinner.setSelection(selected);  //初始化选中的书架
        be_bookshelf_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO 选中某项后
                if(i == bookshelfNames.size()-1){
                    showDialog();
                }else {
                    selectedBookshelf = i;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    // 显示添加书架对话框；
    @SuppressLint("ResourceAsColor")
    private void showDialog() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(BookEditActivity.this);
        builder.title("书架名称")
                .inputRange(1, 10, getResources().getColor(R.color.colorAccent))
                .input(R.string.hint_bookshelf, R.string.prefill_tag, new MaterialDialog.InputCallback() {
                        @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        // 获取输入的书架名字
                        if(input.length() != 0) {
                            List<Book> bookList = new ArrayList<Book>();
                            BookShelf bookShelf = new BookShelf(input.toString(), bookList);
                            Toast.makeText(BookEditActivity.this, input.toString(), Toast.LENGTH_SHORT).show();
                            MainActivity.bookShelfList.add(bookShelf);

                            //TODO 刷新主界面的spinner
                            selectedBookshelf = MainActivity.bookShelfList.size()-1;
                            refreshBookShelfSpinner(selectedBookshelf);
                        }


                    }
                })
                .positiveText("确定")
                .negativeText("取消")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //确认按下响应
                    }
                }).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book_edit_menu, menu);
        MenuItem saveItem = menu.findItem(R.id.edit_save);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.edit_save:
                //点击保存按钮后,保存到指定书架
                boolean isNull = true;
                if(book == null){
                    book = new Book();
                } else {
                    isNull = false;
                }
                book.setTitle(cover_book_title_edit.getText().toString());
                book.setAuthor(cover_book_author_edit.getText().toString());
                book.setPubCompany(cover_book_publisher_edit.getText().toString());
                book.setPubYear(cover_book_publ_year_edit.getText().toString());
                book.setPubMonth(cover_book_publ_month_edit.getText().toString());
                book.setIsbn(cover_book_isbn_edit.getText().toString());
                book.setReadStatus(selectedStatus);
                book.setBookShelfName(MainActivity.bookShelfList.get(selectedBookshelf).getBookshelfName());
                book.setBookNote(be_note_edit.getText().toString());
                book.setTag(be_label_edit.getText().toString());
                book.setWebsite(be_web_edit.getText().toString());


                Intent intent = new Intent();
                if(isNull){
                    BookShelfManager.addABook(selectedBookshelf, book);
                } else {
                    if(selectedBookshelf == MainActivity.selectedBookshelf)
                        BookShelfManager.setABook(selectedBookshelf, selectedBookPos, book);
                    else{
                        BookShelfManager.removeABook(book);
                        BookShelfManager.addABook(selectedBookshelf, book);
                    }

                }
                setResult(RESULT_OK, intent);
                finish();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case BookEditActivity.IMAGE_INTENT:
                if (data != null) {
                    Toast.makeText(BookEditActivity.this, data.getData()+"", Toast.LENGTH_SHORT).show();
                    //TODO:本地图片转为bitmap
                    book_cover_img.setImageURI(data.getData());
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                        ImageManager.SaveImage(BookEditActivity.this, bitmap, book.getUuid());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
