package com.hello.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.hello.R;
import com.hello.databinding.ActivitySettingBinding;
import com.hello.databinding.DialogChooseSexBinding;
import com.hello.utils.DialogUtil;
import com.hello.viewmodel.SettingViewModel;

import javax.inject.Inject;

public class SettingActivity extends AppActivity {
    private ActivitySettingBinding binding;

    @Inject
    SettingViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        binding.setActivity(this);
        binding.setViewModel(viewModel);
    }

    public void showSexDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        DialogChooseSexBinding sexBinding = DataBindingUtil.inflate(inflater,
                R.layout.dialog_choose_sex, null, false);

        DialogUtil.showViewDialog(this, R.string.title_dialog, sexBinding.getRoot(),
                R.string.text_cancel, R.string.text_enter, null,
                (__, ___) -> {
                });
    }
}
