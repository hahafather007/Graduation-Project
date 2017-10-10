package com.hello.view.fragment;


import android.content.Context;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.hello.R;
import com.hello.databinding.FragmentTodayTodoBinding;
import com.hello.view.activity.TipActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class TodayTodoFragment extends Fragment {
    public ObservableBoolean isShowMore = new ObservableBoolean(false);
    private long durationMillis = 300;
    private int verticalanimUp = 1;
    private int verticalanimDown = 0;
    private FragmentTodayTodoBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_today_todo, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = DataBindingUtil.bind(view);
        binding.setView(this);
        initView();
    }

    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_showMore:
                binding.editVoice.clearFocus();
                //隐藏软键盘
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                if (isShowMore.get()) {
                    hideMore();
                } else {
                    showMore();
                }
                break;
            case R.id.btn_tip:
                Intent intent = new Intent(getActivity(), TipActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_sport:

                break;
        }
    }

    //隐藏更多列表的动画
    private void hideMore() {
        isShowMore.set(false);
        startVerticalAnim(binding.showMoreLayout, verticalanimDown, binding.showMoreLayout.getHeight());
        startVerticalAnim(binding.voiceLayout, verticalanimUp, -binding.showMoreLayout.getHeight());
        startVerticalAnim(binding.btnVoice, verticalanimUp, -binding.showMoreLayout.getHeight());
        binding.showMoreLayout.setVisibility(View.GONE);
    }

    //显示更多列表的动画
    private void showMore() {
        isShowMore.set(true);
        startVerticalAnim(binding.showMoreLayout, verticalanimUp, binding.showMoreLayout.getHeight());
        startVerticalAnim(binding.voiceLayout, verticalanimUp, binding.showMoreLayout.getHeight());
        startVerticalAnim(binding.btnVoice, verticalanimUp, binding.showMoreLayout.getHeight());
        binding.showMoreLayout.setVisibility(View.VISIBLE);
    }

    //动画效果
    private void startVerticalAnim(View view, int type, int height) {
        TranslateAnimation animation;
        if (type == verticalanimUp) {
            animation = new TranslateAnimation(0, 0, height, 0);
        } else {
            animation = new TranslateAnimation(0, 0, 0, height);
        }
        //设置动画的回弹效果
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(durationMillis);
        view.startAnimation(animation);
    }

    private void initView() {
        binding.editVoice.setOnFocusChangeListener((v, b) -> hideMore());
        binding.editVoice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

}
