package com.hello.view.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.hello.R;
import com.hello.databinding.FragmentTodayTodoBinding;
import com.hello.viewmodel.TodayToDoViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class TodayTodoFragment extends AppFragment {
    private FragmentTodayTodoBinding binding;
    private InputMethodManager inputMethodManager;
    private VerticalViewPager verticalPager;

    @Inject
    TodayToDoViewModel viewModel;

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

        initView();
        inputMethodManager =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void initView() {
        initViewPager();

        binding.editVoice.setOnFocusChangeListener((view, b) -> {
            verticalPager.setCurrentItem(1);
            binding.holderView.setVisibility(VISIBLE);
        });
        binding.editVoice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    binding.btnVoice.setVisibility(GONE);
                    binding.btnMessage.setVisibility(VISIBLE);
                } else {
                    binding.btnVoice.setVisibility(VISIBLE);
                    binding.btnMessage.setVisibility(GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initViewPager() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new SecondaryNewsFragment());
        fragments.add(new SecondaryHelloFragment());

        verticalPager = binding.verticalPager;
        verticalPager.setAdapter(new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
        verticalPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0 && positionOffsetPixels == 0) {
                    //当viewpager滚动完毕缩回选项
                    binding.holderView.setVisibility(VISIBLE);
                }
            }
        });
    }

    public void showMore() {
        //将控件焦点清除
        binding.editVoice.clearFocus();
        verticalPager.setCurrentItem(1);
        if (binding.holderView.getVisibility() == VISIBLE) {//将holder隐藏，选项顶上去
            inputMethodManager.hideSoftInputFromWindow(binding.editVoice.getWindowToken(), 0);
            binding.holderView.setVisibility(GONE);
        } else {//holder显示，选项缩回
            binding.holderView.setVisibility(VISIBLE);
        }
    }

    public void startOrStopRecording() {
        binding.editVoice.clearFocus();
        verticalPager.setCurrentItem(1);
        viewModel.startOrStopRecording();

    }

    public void sendMessage() {
        viewModel.sendMessage(binding.editVoice.getText().toString());
        binding.editVoice.getText().clear();
    }

    public void showMoreClick(View view) {
        switch (view.getId()) {
            case R.id.more_back:
                verticalPager.setCurrentItem(0);
        }
    }
}
