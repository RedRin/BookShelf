package com.example.bookshelf.Util;

/**
 * Created by lenovo on 2019/5/23.
 */

public class Util {
    public static int compareString(String a1, String a2){
        char[] s1 = a1.toCharArray();
        char[] s2 = a2.toCharArray();
        int length = a1.length() > a2.length() ? a2.length() : a1.length();
        for(int i=0; i<length; i++){
            if(s1[i] < s2[i]){
                return 1;
            }
        }
        return -1;
    }
}
