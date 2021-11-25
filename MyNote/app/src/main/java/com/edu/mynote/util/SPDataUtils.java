package com.edu.mynote.util;
import android.content.Context;
import android.content.SharedPreferences;
import com.edu.mynote.Note.Note;

public class SPDataUtils {
    private static final String myFileName = "mydata";

    //SharedPreferences保存用户信息
    public static boolean saveUserInfo(Context context, String name) {
        boolean flag = false;
        SharedPreferences sp = context.getSharedPreferences(myFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("name", name);
        editor.commit();
        flag = true;
        return flag;
    }

    // 获取用户信息的方法
    public static String getName(Context context) {
        SharedPreferences sp = context.getSharedPreferences(myFileName, Context.MODE_PRIVATE);
        String name = sp.getString("name", null);
        Note note =new Note();
        return  name;
    }
}