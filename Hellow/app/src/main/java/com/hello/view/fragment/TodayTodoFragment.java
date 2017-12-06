package com.hello.view.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hello.R;
import com.hello.databinding.FragmentTodayTodoBinding;
import com.hello.model.aiui.AIUIRepository;
import com.hello.view.activity.RemindActivity;

import javax.inject.Inject;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.hello.utils.IntentUtil.setupActivity;

public class TodayTodoFragment extends AppFragment {
    private FragmentTodayTodoBinding binding;

    @Inject
    AIUIRepository aiuiRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_today_todo, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = DataBindingUtil.bind(view);
        binding.setFragment(this);
    }

    public void showMore() {
        if (binding.holderView.getVisibility() == VISIBLE) {
            binding.holderView.setVisibility(GONE);
        } else {
            binding.holderView.setVisibility(VISIBLE);
        }
    }

    public void setupRemindActivity() {
        setupActivity(getContext(), RemindActivity.class);
    }
}
