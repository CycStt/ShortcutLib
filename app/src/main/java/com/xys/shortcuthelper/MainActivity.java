package com.xys.shortcuthelper;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.yw.sclib.Sc;
import com.yw.sclib.ScCreateResultCallback;

public class MainActivity extends Activity {

    // 快捷方式名
    private String mShortcutName = "学习工具";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addShortcutTest(v);
            }
        });
    }

    public void addShortcutTest(View view) {
        new Sc.Builder(this, this).
                setName("资源创建快捷方式").
                setAllowRepeat(false).
                setIcon(R.mipmap.ic_launcher).
                setCallBack(new ScCreateResultCallback() {
                    @Override
                    public void createSuccessed(String createdOrUpdate, Object tag) {
                        Toast.makeText(MainActivity.this, createdOrUpdate, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void createError(String errorMsg, Object tag) {
                        Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();

                    }
                }).build().createSc();

        new Sc.Builder(MainActivity.this, MainActivity.this).
                setAllowRepeat(false).
                setName("网络图片快捷方式").
                setIcon("http://img1.qidian.com/upload/gamesy/2016/03/03/20160303165643tqfnt6pvx0.jpg").
                setCallBack(new ScCreateResultCallback() {
                    @Override
                    public void createSuccessed(String createdOrUpdate, Object tag) {
                        Toast.makeText(MainActivity.this, createdOrUpdate, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void createError(String errorMsg, Object tag) {
                        Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                }).build().createSc();
    }


}
