package com.edu.mynote;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.edu.mynote.adapter.MainAdapter;
import com.edu.mynote.Note.Note;
import com.edu.mynote.Note.NoteDB;
import com.edu.mynote.Note.NoteDbReader;

public class SearchActivity extends BaseActivity {
    private EditText mEtSearch;
    private Cursor cursor;
    private MainAdapter myAdapter;
    private RecyclerView mRvList;
    private NoteDB mNoteDb;
    private Note mNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        init();//初始化
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                search();
            }
        });
    }

    private void init() {
        mEtSearch = findViewById(R.id.et_search);//搜索文本框
        mRvList = findViewById(R.id.rv_list);//显示listView
        mNoteDb = new NoteDB(this);//获得数据库
    }

    private void search(){
        //获取输入文本
        String findStr = mEtSearch.getText().toString().trim();
        if (!findStr.isEmpty()){
            SQLiteDatabase dbreader = NoteDbReader.getDbreader(this);
            //根据输入文本搜索，返回cursor
            cursor = NoteDbReader.select(dbreader, findStr);//模糊搜索
            myAdapter = new MainAdapter(this,cursor);//新建适配器
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRvList.setLayoutManager(linearLayoutManager);
            myAdapter.setOnItemClickLitener(new MainAdapter.OnItemClickLitener() {//用于显示搜索结果
                @Override
                public void onItemClick(View view, int position) {
                    cursor.moveToPosition(position);
                    Intent intent = new Intent(SearchActivity.this, NotesActivity.class);
                    mNote = new Note(cursor.getInt(cursor.getColumnIndex(mNoteDb.ID)),
                            cursor.getString(cursor.getColumnIndex(mNoteDb.AUTHOR)),
                            cursor.getString(cursor.getColumnIndex(mNoteDb.CONTENT)),
                            cursor.getString(cursor.getColumnIndex(mNoteDb.TIME)));
                    intent.putExtra("nontInfo",mNote);
                    startActivity(intent);
                }

                @Override
                public void onItemLongClick(View view, int position) {//长按删除按键
                    cursor.moveToPosition(position);
                    new AlertDialog.Builder(SearchActivity.this)
                            .setMessage("您确认删除此项目吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SQLiteDatabase dbwriter = NoteDbReader.getDbwriter(SearchActivity.this);
                                    NoteDbReader.delete(cursor.getInt(cursor.getColumnIndex(mNoteDb.ID)),dbwriter);
                                    search();
                                }
                            })
                            .setNegativeButton("取消",null)
                            .create()
                            .show();
                }
            });
            mRvList.setAdapter(myAdapter);
        }
    }

    public void goBack(View view) {
        finish();
    }//结束后返回上一个视图

    public void clean(View view) {
        mEtSearch.setText("");
    }//清空
}
