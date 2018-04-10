package com.hello.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.hello.utils.Log;

//后台保活使用的一像素activity
public class KeepLifeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.setGravity(Gravity.START | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);

        Log.i("KeepLifeActivity启动！！！");
    }

    @Override
    protected void onDestroy() {
        Log.i("KeepLifeActivity关闭！！！");

        super.onDestroy();
    }
}
