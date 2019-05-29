package com.example.bookshelf;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.bookshelf.Util.MySpinner;
import com.example.bookshelf.Util.Util;
import com.github.clans.fab.FloatingActionMenu;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final static int ID_BOOKS = 100;
    final static int ID_SEARCH = 101;
    final static int ID_SETTING = 103;
    final static int ID_ABOUT = 104;
    final static int ID_ADD_TAG = 10;
    final static int TAG_START = 11;
    public static final int SCAN_INTENT_CODE = 100;
    public static final int EDIT_INTENT_CODE = 101;

    NavigationView navigationView;
    Toolbar toolbar;
    RecyclerView bookListView;
    BookAdapter bookAdapter;


    // TODO 替换;
    ArrayList<String> tags = new ArrayList<>();  //标签
    List<Book> nowbookList = new ArrayList<>();  //当前显示的书籍
    public static List<BookShelf> bookShelfList = new ArrayList<BookShelf>();  //所有的bookshelf
    ArrayAdapter<String> bookshelfAdapter;
    Spinner bookshelfSpinner;
    public static int selectedBookshelf = 0;
    String[] sortSect = { "标题", "作者", "出版社", "出版时间" }; //排序dialog项
    int Selected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPrivilege();  //动态获取权限
        toolbar = (Toolbar) findViewById(R.id.toolbar_activity_main);
        setSupportActionBar(toolbar);

        //右下角图标
        FloatingActionMenu fab=(FloatingActionMenu)findViewById(R.id.fab);
        fab.setClosedOnTouchOutside(true);
        //扫码添加
        com.github.clans.fab.FloatingActionButton fab_camera = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.fab_add_camera);
        fab_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:扫码添加
                jumpToScan();
            }
        });

        //单独添加
        com.github.clans.fab.FloatingActionButton fab_single = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.fab_add_single);
        fab_single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  TODO isbn添加按钮；
                addBookByIsbn();
            }
        });
        //批量添加
        com.github.clans.fab.FloatingActionButton fab_batch = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.fab_add_batch);
        fab_batch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:手动添加
                jumpToBookEdit();
            }
        });

        //滑动窗口
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //书列表
        bookAdapter = new BookAdapter(this, new ArrayList<Book>());
        bookListView = (RecyclerView) findViewById(R.id.lv_books);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        bookListView.setLayoutManager(linearLayoutManager);
        bookListView.setAdapter(bookAdapter);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        // TODO 初始化标签链表；
        tags.add("A");
        resetNavMenu();
        navigationView.setNavigationItemSelectedListener(this);
    }



    public void refreshBookList(List<Book> bookList){
        bookAdapter = new BookAdapter(this, bookList);
        bookListView.setAdapter(bookAdapter);
        bookAdapter.notifyDataSetChanged();
    }

    private List<String> loadBookshelfs(){
        BookShelfManager bookShelfManager = new BookShelfManager();
        bookShelfManager.read(MainActivity.this);

        bookShelfList = bookShelfManager.getBookShelfList();
        // TODO: 2019/4/20 添加书架
        ArrayList<String> bookshelfNames = new ArrayList<String>();
        for(int i=0;i<bookShelfList.size(); i++){
            bookshelfNames.add(bookShelfList.get(i).getBookshelfName());
        }
        return bookshelfNames;
    }

    // TODO 启动扫码功能；
    private void jumpToScan() {
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        startActivityForResult(intent, MainActivity.SCAN_INTENT_CODE);

    }

    // TODO 跳转书籍编辑页面；
    private void jumpToBookEdit() {
        Intent intent = new Intent(MainActivity.this, BookEditActivity.class);
        // TODO

        startActivityForResult(intent , MainActivity.EDIT_INTENT_CODE);
    }

    // 重新加载menu；
    private void resetNavMenu() {
        Menu menu = navigationView.getMenu();
        menu.clear();
        menu.add(0, ID_BOOKS, 0, "书籍").setIcon(R.drawable.ic_bookshelf);
        menu.add(0, ID_SEARCH, 0, "搜索").setIcon(R.drawable.ic_search);

        SubMenu tags_menu = menu.addSubMenu("标签");
        // 此处设置添加新标签；
        // TODO 将所有标签显示出；
        int size = tags.size();
        for (int i = 0; i < size; i++)
            tags_menu.add(1, TAG_START + i, 0, tags.get(i)).setIcon(R.drawable.ic_label);
        tags_menu.add(1, ID_ADD_TAG, 0, "添加新标签").setIcon(R.drawable.ic_add);

        menu.add(2, ID_SETTING, 0, "设置").setIcon(R.drawable.ic_settings);
        menu.add(2, ID_ABOUT, 0, "关于").setIcon(R.drawable.ic_about);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void refreshBookshelfSpinner(){
        ArrayList<String> bookshelfNames = new ArrayList<String>();
        for(int i=0;i<bookShelfList.size(); i++){
            bookshelfNames.add(bookShelfList.get(i).getBookshelfName());
        }
        bookshelfAdapter = new ArrayAdapter<String>(MainActivity.this,
                R.layout.support_simple_spinner_dropdown_item, bookshelfNames);
        bookshelfSpinner.setAdapter(bookshelfAdapter);
        bookshelfAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        // TODO
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("搜索书籍");
        //输入键盘回车键变为搜索图标；
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                // TODO

                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.isEmpty()){
                    nowbookList = (ArrayList<Book>) bookShelfList.get(selectedBookshelf).getBookList();
                    refreshBookList(nowbookList);
                } else {
                    List<Book> ret = new ArrayList<Book>();
                    for(int i=0; i<nowbookList.size(); i++){
                        Book book = nowbookList.get(i);
                        if(book.getTitle().contains(s)){
                            ret.add(book);
                        } else if(book.getAuthor().contains(s)){
                            ret.add(book);
                        } else if(book.getPubCompany().contains(s)){
                            ret.add(book);
                        }
                    }
                    refreshBookList(ret);
                }
                return false;
            }
        });

        MenuItem bookshelvesItem = menu.findItem(R.id.action_bookshelves);
        bookshelfSpinner = (Spinner)MenuItemCompat.getActionView(bookshelvesItem);
        // TODO 设置下拉栏； bookshelf名字过滤
        List<String> bookshelfNames = new ArrayList<>();
        bookshelfNames.addAll(loadBookshelfs());
        bookshelfAdapter = new ArrayAdapter<String>(MainActivity.this,
                R.layout.support_simple_spinner_dropdown_item, bookshelfNames);
        bookshelfSpinner.setAdapter(bookshelfAdapter);
        bookshelfSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO 选中某个书架后
                selectedBookshelf = i;
                nowbookList = (ArrayList<Book>) bookShelfList.get(selectedBookshelf).getBookList();
                refreshBookList(nowbookList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            // TODO
            return true;
        }else if (id == R.id.action_sort) {
            //TODO: 排序
            showSortDialog();

            return true;
        } else if(id == R.id.action_delete){
            if(bookShelfList.size() > 0){
                bookShelfList.remove(selectedBookshelf);
                refreshBookshelfSpinner();
            }
        } else if(id == R.id.action_rename){
            showRenameBookshelf();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == ID_BOOKS) {
            // Handle the camera action
        } else if (id == ID_SEARCH) {

        } else if (id == ID_ADD_TAG) {
            // 此处设置弹出对话框，结束后调用resetNavMenu刷新；
            showDialog();
            //resetNavMenu();
        } else if (id == ID_SETTING) {

        } else if (id == ID_ABOUT) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    Handler refreshBookListHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                refreshBookList(bookShelfList.get(selectedBookshelf).getBookList());
            } else if(msg.what == 2){
                Toast.makeText(MainActivity.this, "没有找到这本书", Toast.LENGTH_SHORT).show();
            }

        }
    };

    // 显示添加书架对话框；
    @SuppressLint("ResourceAsColor")
    private void addBookByIsbn() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(MainActivity.this);
        builder.title("请输入isbn")
                .inputRange(1, 13, getResources().getColor(R.color.colorAccent))
                .input(R.string.hint_isbn, R.string.prefill_tag, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        // 获取输入的书架名字
                        final String  isbn = input.toString();
                        if(input.length() != 0) {
                            new Thread(){
                                public void run(){
                                    Book book = BookInfoManager.getBookFromISBN(MainActivity.this, isbn);
                                    if(book != null){
                                        BookShelfManager.addABook(selectedBookshelf, book);
                                        refreshBookListHandler.sendEmptyMessage(1);
                                    } else {
                                        refreshBookListHandler.sendEmptyMessage(2);
                                    }
                                }
                            }.start();

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


        // 显示添加对话框；
    @SuppressLint("ResourceAsColor")
    private void showDialog() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(MainActivity.this);
        builder.title("标签名称")
                .inputRange(1, 15, getResources().getColor(R.color.colorAccent))
                .input(R.string.hint_tag, R.string.prefill_tag, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        // TODO 获取要添加的标签的名称
                        if(input.length() != 0)
                            tags.add(input.toString());
                        resetNavMenu();
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
//                        if(dialog.getInputEditText().length() == 0) {
//                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
//                        }else {
//                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
//                        }
                    }
                }).show();
    }

    //显示排序选择对话框；
    private void showSortDialog() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(MainActivity.this);
        builder.title("排序依据")
                .titleGravity(GravityEnum.CENTER)
                .titleColor(Color.parseColor("#000000"))
                .items(sortSect)
                .autoDismiss(false)
                .widgetColor(Color.RED)
                .positiveText("排序")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // TODO
                        //Toast.makeText(MainActivity.this, "" + which, Toast.LENGTH_SHORT).show();
                    }
                })
                // TODO 设置初始选择的排序依据；
                .itemsCallbackSingleChoice(Selected, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        if (TextUtils.isEmpty(text)) {
                            Toast.makeText(MainActivity.this, "请选择排序", Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(MainActivity.this, which+"", Toast.LENGTH_SHORT).show();
                            //  TODO 查找 text 在 sortSect 中的位置；
                            switch(which){
                                case 0:
                                    Collections.sort(nowbookList, new Comparator<Book>(){
                                        @Override
                                        public int compare(Book book, Book t1) {
                                            return book.getTitle().compareTo(t1.getTitle());
                                        }
                                    });
                                    refreshBookList(nowbookList);
                                    break;
                                case 1:
                                    Collections.sort(nowbookList, new Comparator<Book>(){
                                        @Override
                                        public int compare(Book book, Book t1) {
                                            return book.getAuthor().compareTo(t1.getAuthor());
                                        }
                                    });
                                    refreshBookList(nowbookList);
                                    break;
                                case 2:
                                    Collections.sort(nowbookList, new Comparator<Book>(){
                                        @Override
                                        public int compare(Book book, Book t1) {
                                            return book.getPubCompany().compareTo(t1.getPubCompany());
                                        }
                                    });
                                    refreshBookList(nowbookList);
                                    break;
                                case 3:
                                    Collections.sort(nowbookList, new Comparator<Book>(){
                                        @Override
                                        public int compare(Book book, Book t1) {
                                            if(book.getPubYear().equals(t1.getPubYear())){
                                                return book.getPubMonth().compareTo(t1.getPubMonth());
                                            }
                                            return book.getPubYear().compareTo(t1.getPubYear());
                                        }
                                    });
                                    refreshBookList(nowbookList);
                            }
                            dialog.dismiss();
                        }
                        return false;
                    }
                }).show();
    }

    @SuppressLint("ResourceAsColor")
    private void showRenameBookshelf() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(MainActivity.this);
        builder.title("书架名称")
                .inputRange(1, 10, getResources().getColor(R.color.colorAccent))
                .input(R.string.hint_bookshelf, R.string.prefill_tag, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        // 获取输入的书架名字
                        if(input.length() != 0) {
                            bookShelfList.get(selectedBookshelf).setBookshelfName(input.toString());
                            //TODO 刷新主界面的spinner
                            selectedBookshelf = MainActivity.bookShelfList.size()-1;
                            refreshBookshelfSpinner();
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

    // TODO 子页面返回；
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MainActivity.SCAN_INTENT_CODE:
                // TODO 扫码返回；
                if (data != null) {
                    final String content = data.getStringExtra(Constant.CODED_CONTENT);
                    new Thread(){
                        public void run(){
                            Book book = BookInfoManager.getBookFromISBN(MainActivity.this, content);
                            if(book != null){
                                BookShelfManager.addABook(selectedBookshelf, book);
                                refreshBookListHandler.sendEmptyMessage(1);
                            } else {
                                refreshBookListHandler.sendEmptyMessage(2);
                            }
                        }
                    }.start();
                }
                break;
            case MainActivity.EDIT_INTENT_CODE:
                refreshBookList(bookShelfList.get(selectedBookshelf).getBookList());
                refreshBookshelfSpinner();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        BookShelfManager bookShelfManager = new BookShelfManager();
        bookShelfManager.setBookShelfList(bookShelfList);
        bookShelfManager.save(MainActivity.this);
        super.onDestroy();
    }

    private void getPrivilege(){
        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.CAMERA);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.INTERNET);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_WIFI_STATE);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }


        if(!permissionList.isEmpty()){
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        }
    }
}
