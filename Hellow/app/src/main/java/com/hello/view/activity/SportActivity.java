package com.hello.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.hello.R;
import com.hello.databinding.ActivitySportBinding;
import com.hello.utils.rx.RxField;
import com.hello.utils.rx.RxLifeCycle;
import com.hello.viewmodel.SportViewModel;

import javax.inject.Inject;

public class SportActivity extends AppActivity {
    private ActivitySportBinding binding;

    @Inject
    SportViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sport);

        addChangeListener();
    }

    private void addChangeListener() {
        RxField.of(viewModel.getStep())
                .compose(RxLifeCycle.resumed(this))
                .subscribe(v -> binding.stepView.setCurrentCount(10000, v));
    }
}
