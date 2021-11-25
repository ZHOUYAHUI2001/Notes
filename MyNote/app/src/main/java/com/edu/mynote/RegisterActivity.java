package com.edu.mynote;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.edu.mynote.Note.NoteDbReader;
import com.edu.mynote.util.SPDataUtils;

public class RegisterActivity extends BaseActivity {
    private EditText password;
    private EditText rePassword;
    private Button register;
    private String mainSource;
    private EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        SQLiteDatabase dbreader = NoteDbReader.getDbreader(this);

        boolean isFirst = NoteDbReader.selectPaswd(dbreader);
        mainSource = getIntent().getStringExtra("source");
        //仅首次打开需注册
        if (!isFirst && mainSource == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        init();

        //修改密码
        if (mainSource != null) {
            if (mainSource.equals("MainActivity")) {
                LinearLayout ll = findViewById(R.id.ll_goback);
                //显示返回按钮
                ll.setVisibility(View.VISIBLE);
                password.setHint("输入新密码");
            }
        }
    }

    private void init() {
        password = findViewById(R.id.edit_password);
        rePassword = findViewById(R.id.edit_re_password);
        register = findViewById(R.id.register);
        name =findViewById(R.id.edit_name);
        name.setText(SPDataUtils.getName(this));
    }

    protected void onResume() {
        super.onResume();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "请输入6位数密码", Toast.LENGTH_SHORT).show();
                } else if (!password.getText().toString().equals(rePassword.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "输入两次密码不同！", Toast.LENGTH_SHORT).show();
                } else if (password.getText().toString().length() != 6) {
                    Toast.makeText(RegisterActivity.this, "请输入6位数字", Toast.LENGTH_SHORT).show();
                } else {
                    //判断是否是应是注册界面
                    if (mainSource == null) {
                        registerPwd();
                    } else if (mainSource != null && mainSource.equals("MainActivity")) {
                        registerPwd2();
                    }
                }

            }
        });
    }


    private void registerPwd() {
        SQLiteDatabase dbwriter = NoteDbReader.getDbwriter(this);
        boolean success = NoteDbReader.registerPaswd(dbwriter, password.getText().toString());
        if (success) {
            if (mainSource != null && mainSource.equals("MainActivity")) {
                SPDataUtils.saveUserInfo(RegisterActivity.this,name.getText().toString()) ;
                Toast.makeText(this, "修改用户密码成功", Toast.LENGTH_SHORT).show();
            } else {
                SPDataUtils.saveUserInfo(RegisterActivity.this,name.getText().toString()) ;
                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            if (mainSource != null && mainSource.equals("MainActivity")) {
                Toast.makeText(this, "修改密码失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void registerPwd2() {
        SQLiteDatabase dbwriter = NoteDbReader.getDbwriter(this);
        //先删除密码
        NoteDbReader.deletePaswd(dbwriter);
        registerPwd();
    }

    public void goBack(View view) {
        finish();
    }
}
