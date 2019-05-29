package com.example.bookshelf;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by lenovo on 2019/5/22.
 */

public class JsonParser {

    public static Book jsonToBook(Context context, String jsonSrc){
        Book book = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonSrc);
            String remark = jsonObject.getJSONObject("showapi_res_body").getString("remark");
            if(remark.equals("success")){
                JSONObject data = jsonObject.getJSONObject("showapi_res_body").getJSONObject("data");
                book = new Book();
                book.setTitle(data.getString("title"));
                book.setAuthor(data.getString("author"));
                book.setPubCompany(data.getString("publisher"));
                String pubdate = data.getString("pubdate");
                if(pubdate.contains("-")){
                    book.setPubYear(pubdate.substring(0, pubdate.indexOf("-")));
                    book.setPubMonth(pubdate.substring(pubdate.indexOf("-")+1, pubdate.length()));
                }

                book.setIsbn(data.getString("isbn"));
                book.setWebsite("https://market.cloud.tencent.com/products/7494");
                Bitmap bitmap = ImageManager.GetImageInputStream(data.getString("img"));
                ImageManager.SaveImage(context, bitmap, book.getUuid());
            }
        } catch (JSONException e) {
            Log.e("JsonParser", "jsonToBook: Json解析错误");
            e.printStackTrace();
        }

        return book;
    }
}
