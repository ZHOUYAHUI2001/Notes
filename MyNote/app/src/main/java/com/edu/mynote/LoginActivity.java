package com.edu.mynote;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.edu.mynote.Note.NoteDbReader;
import com.edu.mynote.util.SPDataUtils;

public class LoginActivity extends BaseActivity {
    private EditText editText;
    private Button button;
    private EditText name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

        SQLiteDatabase dbreader = NoteDbReader.getDbreader(this);
        //获取数据库中密码，用final修饰，不能再次修改
        final String passwd = NoteDbReader.getPasswd(dbreader);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                }else if(passwd.equals(editText.getText().toString())){
                    SPDataUtils.saveUserInfo(LoginActivity.this,name.getText().toString()) ;
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }else {
                    Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void init() {
        editText = findViewById(R.id.edit_password);
        button = findViewById(R.id.login);
        name =findViewById(R.id.edit_name);
        name.setText(SPDataUtils.getName(this));
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
}
