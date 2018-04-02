package com.hello.view.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.bumptech.glide.Glide;
import com.hello.R;
import com.hello.databinding.ActivitySettingBinding;
import com.hello.databinding.DialogChooseSexBinding;
import com.hello.model.pref.HelloPref;
import com.hello.utils.DialogUtil;
import com.hello.utils.ToastUtil;
import com.hello.utils.rx.RxLifeCycle;
import com.hello.viewmodel.SettingViewModel;

import javax.inject.Inject;

import static com.hello.viewmodel.SettingViewModel.HelloSex.BOY;
import static com.hello.viewmodel.SettingViewModel.HelloSex.GIRL;

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

        if (HelloPref.INSTANCE.isLogin()) {
            Glide.with(this).load(HelloPref.INSTANCE.getImage()).into(binding.headerView);
        }

        addChangeListener();
    }

    private void addChangeListener() {
        viewModel.getSuccess()
                .compose(RxLifeCycle.resumed(this))
                .doOnNext(b -> ToastUtil.showToast(this, b ?
                        R.string.text_save_over : R.string.text_save_error))
                .subscribe();
    }

    public void showSexDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        DialogChooseSexBinding sexBinding = DataBindingUtil.inflate(inflater,
                R.layout.dialog_choose_sex, null, false);

        sexBinding.setHelloSex(viewModel.getHelloSex().get());

        DialogUtil.showViewDialog(this, R.string.title_dialog, sexBinding.getRoot(),
                R.string.text_cancel, R.string.text_enter, null,
                (__, ___) -> {
                    if (sexBinding.radioGirl.isChecked()) {
                        viewModel.getHelloSex().set(GIRL);
                    } else {
                        viewModel.getHelloSex().set(BOY);
                    }
                });
    }
}
