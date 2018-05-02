package com.hello.view.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.hello.R;

import dagger.android.AndroidInjection;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

@SuppressLint("Registered")
public class AppActivity extends SwipeBackActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            AndroidInjection.inject(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        initToolBar();
    }

    @SuppressWarnings("ConstantConditions")
    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(__ -> finish());
        }
    }
}
