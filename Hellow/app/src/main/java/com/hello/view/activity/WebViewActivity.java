package com.hello.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.hello.R;
import com.hello.common.Constants;
import com.hello.databinding.ActivityWebviewBinding;

import static com.hello.common.Constants.URL;
import static com.hello.common.Constants.context;

public class WebViewActivity extends AppActivity {
    private ActivityWebviewBinding binding;

    public static Intent intentOfUrl(String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(URL, url);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_webview);
        initWebView();
    }

    private void initWebView() {
        binding.webView.loadUrl(getIntent().getStringExtra(Constants.URL));
    }
}
