package com.edu.mynote;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.edu.mynote.Note.Note;
import com.edu.mynote.Note.NoteDbReader;
import com.edu.mynote.util.SPDataUtils;
import com.edu.mynote.util.SDCardUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.qzb.richeditor.RichEditor;

public class NotesActivity extends BaseActivity/* implements PermissionsUtil.IPermissionsCallback*/ {
    private String imagePath = "";
    private RichEditor re;//图文混排
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmmss", Locale.CHINA);//用以生成当前时间的文件，避免文件名重复
    private File file;//保存路径
    private Uri uri; //图片的绝对路径
    private TextView time;
    private TextView author;
    private ImageView ibt_delete;
    private ImageView ibt_save;
    private ImageView ibt_blod;//加粗
    private Note mNote; //笔记
    private int mId;//id

    private Handler mHandler;
    //Handler：异步消息的处理：当发出一个消息之后，首先进入一个消息队列，发送消息的函数即刻返回
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        init();

        ibt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(true);
            }
        });
        ibt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
    }


    private void init() {
        re = findViewById(R.id.editor);
        time = findViewById(R.id.showtime);
        author=findViewById(R.id.showAuthor);
        ibt_delete = findViewById(R.id.ibt_delete);
        ibt_save = findViewById(R.id.ibt_save);
        ibt_blod = findViewById(R.id.ibt_blod);
        //获取传过来的Note对象，如果没有Note对象，则当前页面应显示为“新建记事本”
        mNote = (Note) getIntent().getSerializableExtra("nontInfo");

        if (mNote == null) {
            re.setPlaceholder("input text here........");
            ibt_delete.setVisibility(View.GONE);
            time.setVisibility(View.GONE);
            author.setVisibility(View.GONE);
        } else {
            ibt_delete.setVisibility(View.VISIBLE);//显示删除
            time.setVisibility(View.VISIBLE);//显示时间
            time.setTextColor(this.getResources().getColor(R.color.colorBlack));
            time.setText("时间："+mNote.getnTime());
            //显示作者
            author.setVisibility(View.VISIBLE);
            author.setTextColor(this.getResources().getColor(R.color.colorBlack));
            author.setText("作者："+mNote.getnAuthor());
            
            mId = mNote.getnId();
            re.setHtml(mNote.getnContent());
        }

        re.setPadding(10, 10, 10, 10);//设置位置
        re.setBackgroundColor(this.getResources().getColor(R.color.colorTransprent));//设置背景
    }

    //拍照
    public  void  addPhoto(View view){
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file= new File(getExternalCacheDir(),simpleDateFormat.format(new Date())+".jpg");
        uri = FileProvider.getUriForFile(this,"com.wuwl.mynote.fileProvider",file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);//k+v
        startActivityForResult(intent,2); //第一个为请求的活动，第二个为请求码前面定义110

    }

    //相册导入图片
    public void addPic(View view) {
        if (!(ActivityCompat.checkSelfPermission(NotesActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                || !(ActivityCompat.checkSelfPermission(NotesActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            //没有权限，申请SD卡读取权限
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            //申请权限
            ActivityCompat.requestPermissions(NotesActivity.this, permissions, 3);
        } else {
            //拥有权限
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 3 && grantResults.length == 2
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.e("PowerTAG", "权限申请成功");
        } else {
            Log.e("PowerTAG", "权限申请失败");
        }
    }

    public void save(boolean saveType) {
        //内容为空，点击保存，不做反应
        if (re.getHtml() == null||re.getHtml().equals("")) {
            return;
        }
        SQLiteDatabase dbwriter = NoteDbReader.getDbwriter(this);
        Log.d("html", re.getHtml());
        Log.d("html", getTime());
        mNote = new Note();//新建日记
        mNote.setnContent(re.getHtml());//把日记内容放入Conteent
        //如果时间作者不为空，则显示在notes里面
        if (time.getVisibility() == View.GONE) {
            mNote.setnTime(getTime());
            mNote.setnAuthor(SPDataUtils.getName(this));
        } else {
            mNote.setnId(mId);

        }
        NoteDbReader.save(mNote, dbwriter);
        //如果时手动保存就结束当前界面
        if (saveType) {
            finish();
        }
    }

    private void delete() {
        SQLiteDatabase dbwriter = NoteDbReader.getDbwriter(this);
        NoteDbReader.delete(mNote.getnId(), dbwriter);
        finish();
    }

    //获取系统当前时间
    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date();
        String str = sdf.format(date);
        return str;
    }

    //startActivityForResult(intent, 1)的回调，1为相册，2位相机，用于显示图片和保存图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {//相册
                InputStream inputStream = null;
                try {
                    Uri uri = data.getData();
                    ContentResolver contentResolver = NotesActivity.this.getContentResolver();
                    Bitmap bitmap;
                    //返回uri成为输入流
                    inputStream = contentResolver.openInputStream(uri);
                    //解码成位图
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    imagePath = SDCardUtil.saveMyBitmap(bitmap, System.currentTimeMillis() + "");
                    //添加图片
                    re.insertImage(imagePath, "IMG", 100);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println(e);
                }finally {
                    if (inputStream!=null){
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }else if(requestCode ==2){//相机
                InputStream inputStream = null;
                ContentResolver contentResolver = NotesActivity.this.getContentResolver();
                Bitmap bitmap;
                //返回uri成为输入流
                try {
                    inputStream = contentResolver.openInputStream(uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                //解码成位图
                bitmap = BitmapFactory.decodeStream(inputStream);
                try {
                    imagePath = SDCardUtil.saveMyBitmap(bitmap, System.currentTimeMillis() + "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //富文本添加图片方法
                re.insertImage(imagePath, "IMG", 100);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    // 返回键
    public void goBack(View view) {
        finish();
    }

    // activity回到可交互状态，调用onResume();
    @Override
    protected void onResume() {
        super.onResume();
        //判断time有没有显示，如果不显示，则是新建记事本页面，不启动自动保存
        if (time.getVisibility() != View.GONE) {
            mHandler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    mHandler.postDelayed(this, 10000);
                    if (re.getHtml() == null||re.getHtml().equals("")) {
                        return;
                    }
                    save(false);
                }
            };
            mHandler.postDelayed(runnable, 10000);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mHandler != null) {
            mHandler.removeCallbacks(runnable);
        }
    }
/******************************************************************************/
    //加粗
    public void blod(View view) {
        re.setBold();
    }
    // 斜体
    public void italic(View view) {
        re.setItalic();
    }
    //下划线
    public void underline(View view) {
        re.setUnderline();
    }
}
