package com.edu.mynote.Note;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class NoteDB extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "note";    //数据库表名“note”
    public static final String ID = "id";              //id号
    public static final String CONTENT = "content";    //内容
    public static final String TIME = "time";          //事件
    public static final String AUTHOR="author";        //作者
    public static final String TABLE_NAME_PWD = "password";    //密码表"password"
    public static final String PWD = "password";              //密码

    public NoteDB(Context context) {
        super(context, TABLE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {//建表“note”、"password"
        String sql = "create table " + TABLE_NAME + " ( " + ID + " integer primary key AUTOINCREMENT, "
                + CONTENT + " TEXT NOT NULL, "
                + AUTHOR + " TEXT NOT NULL, "
                + TIME + " TEXT NOT NULL )";
        db.execSQL(sql);
        String sql2 = "create table "+TABLE_NAME_PWD+" ( "+PWD+" TEXT primary key);";
        db.execSQL(sql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
