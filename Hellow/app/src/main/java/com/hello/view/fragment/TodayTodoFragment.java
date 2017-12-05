package com.hello.view.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hello.R;
import com.hello.databinding.FragmentTodayTodoBinding;

import static android.view.View.*;
import static android.view.View.VISIBLE;

public class TodayTodoFragment extends Fragment {
    private FragmentTodayTodoBinding binding;

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
}
