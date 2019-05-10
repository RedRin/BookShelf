package com.example.bookshelf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by lenovo on 2019/4/20.
 */


//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Bitmap bitmap = ImageManager.GetImageInputStream("https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1555746626&di=5a00d7e2542631cfdff9988793e27782&src=http://nres.ingdan.com/uploads/20160201/1454287776483324.png");
//                    if(ImageManager.IsImageExists(MainActivity.this,"testImg"))
//                        Log.e("test", "run: 文件已存在");
//                    else
//                        if(ImageManager.SaveImage(MainActivity.this, bitmap, "testImg"))
//                            Log.d("test", "run: 保存成功");
//                }
//            }).start();

public class ImageManager {
    public static Bitmap GetImageInputStream(String imageurl) {
        URL url;
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        InputStream inputStream = null;
        try {
            url = new URL(imageurl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(6000);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            int code = connection.getResponseCode();
            if(code == 200){
                inputStream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                Log.v("ImageManager", "GetImageInputStream: 下载成功");
                return bitmap;
            } else {
                Log.e("ImageManager", "GetImageInputStream: 请求码不是200");
                return null;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("ImageManager", "GetImageInputStream: URL错误");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ImageManager", "GetImageInputStream: IO错误");
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                    inputStream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("ImageManager", "GetImageInputStream: 关闭流失败");
                }
            }
        }

    }

    public static boolean IsImageExists(Context context, String fileName){
        String dirsrc = context.getFilesDir()+"";
        File dir = new File(dirsrc);
        if(!dir.exists()){
            dir.mkdirs();
        }

        String src = context.getFilesDir() + "/" + fileName + ".png";
        File outputFile = new File(src);

        return outputFile.exists();
    }

    public static boolean SaveImage(Context context, Bitmap bitmap, String saveName){
        if(bitmap == null)
            return false;

        String dirsrc = context.getFilesDir()+"";
        File dir = new File(dirsrc);
        if(!dir.exists()){
            dir.mkdirs();
        }

        String src = context.getFilesDir() + "/" + saveName + ".png";
        File outputFile = new File(src);

        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            Log.e("ImageManager", "SaveImage: 创建文件失败");
            return false;
        }

        FileOutputStream fileOutputStream=null;

        try {
            fileOutputStream = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("ImageManager", "SaveImage: 文件不存在");
            return false;
        } finally {
            if(fileOutputStream != null){
                try {
                    fileOutputStream.close();
                    fileOutputStream=null;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("ImageManager", "SaveImage: 关闭流失败");
                }

            }
        }

    }

    public static Bitmap GetLocalBitmap(Context context, String imageName){
        FileInputStream fis=null;
        Bitmap bitmap=null;
        try {
            fis=new FileInputStream(context.getFilesDir() + "/" + imageName + ".png");
            bitmap = BitmapFactory.decodeStream(fis);
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("ImageManager", "GetLocalBitmap: 图片文件不存在");
            return null;
        } finally{
            if(fis != null){
                try {
                    fis.close();
                    fis = null;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("ImageManager", "GetLocalBitmap: 关闭流失败");
                }
            }
        }
    }
}
