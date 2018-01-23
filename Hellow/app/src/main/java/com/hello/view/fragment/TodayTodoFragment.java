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
import com.hello.utils.ToastUtil;
import com.hello.viewmodel.TodayToDoViewModel;
import com.hello.widget.listener.SimpleTextWatcher;
import com.hello.widget.view.VerticalViewPager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class TodayTodoFragment extends AppFragment {
    private FragmentTodayTodoBinding binding;
    private InputMethodManager inputMethodManager;
    private VerticalViewPager verticalPager;
    private boolean editClick;

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
        addChangeListener();
        inputMethodManager =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void initView() {
        initViewPager();

        binding.editVoice.setOnClickListener(view -> {
            if (verticalPager.getCurrentItem() == 0) {
                verticalPager.setCurrentItem(1);
                editClick = true;
            }
        });
        binding.editVoice.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.holderView.setVisibility(VISIBLE);
            }
        });
        //对输入文字的长度进行监听，由此判断消息发送按钮是否显示
        binding.editVoice.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    binding.icVoice.setVisibility(GONE);
                    binding.btnMessage.setVisibility(VISIBLE);
                } else {
                    binding.btnMessage.setVisibility(GONE);
                    binding.icVoice.setVisibility(VISIBLE);
                }
            }
        });
    }

    private void addChangeListener() {
        viewModel.error
                .subscribe(__ -> ToastUtil.showToast(getContext(), R.string.test_network_error));
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
                    binding.editVoice.setFocusable(false);
                    binding.editVoice.setFocusableInTouchMode(false);
                } else if (position == 1 && positionOffsetPixels == 0) {
                    binding.editVoice.setFocusable(true);
                    binding.editVoice.setFocusableInTouchMode(true);
                    /*若点击EditTextView，弹出软键盘的同时切换页面，可能会导致页面高度计算不正确，
                    所以设置在切换前不能获得焦点，等待页面切换完成后再获取焦点弹出软键盘*/
                    if (editClick) {
                        editClick = false;
                        binding.editVoice.requestFocus();
                        inputMethodManager.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                    }
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

    public void voiceMethod() {
        binding.editVoice.setVisibility(GONE);
        binding.btnVoice.setVisibility(VISIBLE);
        binding.icEdit.setVisibility(VISIBLE);
        binding.icVoice.setVisibility(GONE);
    }

    public void editMethod() {
        binding.btnVoice.setVisibility(GONE);
        binding.editVoice.setVisibility(VISIBLE);
        binding.icVoice.setVisibility(VISIBLE);
        binding.icEdit.setVisibility(GONE);
    }
}
