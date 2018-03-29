package com.hello.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.hello.R;
import com.hello.databinding.ActivityAboutBinding;
import com.hello.utils.VersionUtil;
import com.hello.widget.view.ContractDialog;

public class AboutActivity extends AppActivity {
    private ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_about);
        binding.setVersion(VersionUtil.getVersionName(this));
        binding.setActivity(this);
    }

    public void showHelpDialog() {
        new ContractDialog(this).show();
    }

    public void setupHelp() {

    }
}
