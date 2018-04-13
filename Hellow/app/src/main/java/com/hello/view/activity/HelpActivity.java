package com.hello.view.activity;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;

import com.hello.R;
import com.hello.databinding.ActivityHelpBinding;
import com.hello.viewmodel.HelpViewModel;

import javax.inject.Inject;

public class HelpActivity extends AppActivity {
    private ActivityHelpBinding binding;

    @Inject
    HelpViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_help);
        binding.setActivity(this);
        binding.setViewModel(viewModel);
    }

    @Override
    protected void onDestroy() {
        viewModel.onCleared();

        super.onDestroy();
    }

    public void onBindItem(ViewDataBinding binding, Object data, int position) {
    }
}
