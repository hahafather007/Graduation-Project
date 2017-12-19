package com.hello.view.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.hello.R;
import com.hello.databinding.FragmentTodayTodoBinding;
import com.hello.utils.Log;
import com.hello.utils.rx.RxField;
import com.hello.utils.rx.RxLifeCycle;
import com.hello.viewmodel.TodayTodoViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class TodayTodoFragment extends AppFragment {
    private FragmentTodayTodoBinding binding;
    private InputMethodManager inputMethodManager;
    private VerticalViewPager verticalPager;

    @Inject
    TodayTodoViewModel viewModel;

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

        addChangeListener();
    }

    private void initView() {
        initViewPager();
        binding.editVoice.setOnFocusChangeListener((view, b) -> {
            verticalPager.setCurrentItem(1);
            binding.holderView.setVisibility(VISIBLE);
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

    private void addChangeListener() {
        RxField.of(viewModel.volume)
                .compose(RxLifeCycle.with(this))
                .map(String::valueOf)
                .delay(100, TimeUnit.MILLISECONDS)
                .subscribe(Log::i);

        RxField.ofNonNull(viewModel.apiResponse)
                .compose(RxLifeCycle.with(this))
                .subscribe();
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

    //话筒按钮被按下
    public void voiceBtnClick() {
        binding.editVoice.clearFocus();
        verticalPager.setCurrentItem(1);
        viewModel.startOrStopRecorder();
    }

    public void showMoreClick(View view) {
        switch (view.getId()) {
            case R.id.more_back:
                verticalPager.setCurrentItem(0);
        }
    }
}
