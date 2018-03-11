package com.hello.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.hello.R;
import com.hello.databinding.ActivityWebviewBinding;

import static com.hello.common.Constants.TITLENAME;
import static com.hello.common.Constants.URL;

public class WebViewActivity extends AppActivity {
    private ActivityWebviewBinding binding;

    public static Intent intentOfUrl(Context context, String url, String title) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(URL, url);
        intent.putExtra(TITLENAME, title);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_webview);
        setTitle(getIntent().getStringExtra(TITLENAME));
        initWebView();
    }

    private void initWebView() {
        binding.webView.loadUrl(getIntent().getStringExtra(URL));
    }
}
