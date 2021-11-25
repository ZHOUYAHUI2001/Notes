package com.edu.mynote.Note;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

// 数据库的具体实现
public class NoteDbReader {
    private static SQLiteDatabase dbreader;
    private static SQLiteDatabase dbwriter;

   //获取读数据库的对象
    public static SQLiteDatabase getDbreader(Context context) {
        NoteDB noteDB = new NoteDB(context);
        dbreader = noteDB.getReadableDatabase();
        return dbreader;//读数据库的操作对象
    }

    //获取数据库写的操作对象
    public static SQLiteDatabase getDbwriter(Context context) {
        NoteDB noteDB = new NoteDB(context);
        dbwriter = noteDB.getWritableDatabase();
        return dbwriter;//写数据库的操作对象
    }

    // 查找全部记事本
    public static Cursor selectAll(SQLiteDatabase dbreader) {
        Cursor cursor = dbreader.query(NoteDB.TABLE_NAME, null, null, null, null, null, null);
        //https://blog.csdn.net/lyq563874257/article/details/51008269
        //table:数据库表的名称
        //columns:数据库列名称数组 写入后最后返回的Cursor中只能查到这里的列的内容
        //selection:查询条件
        //selectionArgs:查询结果
        //groupBy:分组列
        //having:分组条件
        //orderBy:排序列
        //limit:分页查询限制(没用上)
        return cursor;
    }

   // 模糊查询
    public static Cursor select(SQLiteDatabase dbreader, String find) {
        Cursor cursor = dbreader.rawQuery("select * from " + NoteDB.TABLE_NAME +
                " where " + NoteDB.CONTENT + " like '%" + find + "%'", null);
        return cursor;
    }

    //删除
    public static int delete(int id, SQLiteDatabase dbwriter) {
        int delete = dbwriter.delete(NoteDB.TABLE_NAME, NoteDB.ID + "=" + id, null);
        return delete;
    }


    // 保存记事本
    public static void save(Note note, SQLiteDatabase dbwriter) {
        ContentValues cv = new ContentValues();
        boolean type = true;
        //如果是新加的文本，那么就加入时间，并且用insert语句
        if (note.getnId() == 0) {
            cv.put(NoteDB.AUTHOR, note.getnAuthor());
            cv.put(NoteDB.TIME, note.getnTime());
        } else {
            type = false;
        }
        cv.put(NoteDB.CONTENT, note.getnContent());
        if (!type) {
            dbwriter.update(NoteDB.TABLE_NAME, cv, NoteDB.ID + "=" + note.getnId(), null);
        } else {
            dbwriter.insert(NoteDB.TABLE_NAME, null, cv);
        }

    }

   //删除已经存在的密码
    public static void deletePaswd(SQLiteDatabase dbwriter){
        dbwriter.delete(NoteDB.TABLE_NAME_PWD,null,null);
    }


   //获取数据库中保存的密码
    public static String getPasswd(SQLiteDatabase dbreader){
        Cursor cursor = dbreader.rawQuery("select * from "+NoteDB.TABLE_NAME_PWD, null);
        cursor.moveToNext();
        String password = cursor.getString(cursor.getColumnIndex(NoteDB.PWD));
        return password;
    }
   //查找数据库中有没有密码，若有密码，则返回false表示不是第一次登录
    public static boolean selectPaswd(SQLiteDatabase dbreader) {
        Cursor cursor = dbreader.rawQuery("select * from "+NoteDB.TABLE_NAME_PWD, null);
        return !cursor.moveToNext();
    }
   //注册密码界面，注册成功返回true，错误返回false
    public static boolean registerPaswd(SQLiteDatabase dbwriter,String passwd){
        ContentValues cv = new ContentValues();
        cv.put(NoteDB.PWD,passwd);
        long insert = dbwriter.insert(NoteDB.TABLE_NAME_PWD, null, cv);
        if (insert>0){
            return true;
        }
        return false;
    }
}
