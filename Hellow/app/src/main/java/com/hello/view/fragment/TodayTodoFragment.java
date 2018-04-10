package com.hello.view.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.annimon.stream.function.Supplier;
import com.hello.R;
import com.hello.databinding.FragmentTodayTodoBinding;
import com.hello.utils.ToastUtil;
import com.hello.utils.rx.RxField;
import com.hello.utils.rx.RxLifeCycle;
import com.hello.view.activity.MainActivity;
import com.hello.viewmodel.TodayToDoViewModel;
import com.hello.widget.listener.SimpleTextWatcher;

import javax.inject.Inject;

import io.reactivex.Observable;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.hello.view.activity.MainActivity.OnPageScrollListener;

public class TodayTodoFragment extends AppFragment implements OnPageScrollListener {
    private FragmentTodayTodoBinding binding;
    private InputMethodManager inputMethodManager;
    private ViewTag viewTag;

    private boolean isRecording;

    @Inject
    TodayToDoViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_today_todo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = DataBindingUtil.bind(view);
        binding.setFragment(this);

        inputMethodManager =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).addScrollListener(this);
        }

        initView();
        addChangeListener();
    }

    @Override
    public void onDestroy() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).removeScrollListener(this);
        }
        viewModel.onCleared();

        super.onDestroy();
    }

    //防止按住说话时滑动pager冲突
    @Override
    public void onScroll() {
        if (isRecording) {
            isRecording = false;

            viewModel.stopRecording();
            binding.recordPopup.getRoot().setVisibility(GONE);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        viewTag = ViewTag.NEWS;

        binding.editVoice.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                setupHello();

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

        binding.btnVoice.setOnTouchListener((v, event) -> {
            setupHello();
            binding.editVoice.clearFocus();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isRecording = true;

                    viewModel.startRecording();
                    binding.recordPopup.getRoot().bringToFront();
                    binding.recordPopup.getRoot().setVisibility(VISIBLE);
                    break;
                case MotionEvent.ACTION_UP:
                    isRecording = false;

                    viewModel.stopRecording();
                    binding.recordPopup.getRoot().setVisibility(GONE);
                    break;
            }
            return true;
        });
    }

    private void addChangeListener() {
        viewModel.error
                .compose(RxLifeCycle.with(this))
                .doOnNext(__ -> ToastUtil.showToast(getContext(), R.string.test_network_error))
                .subscribe();

        RxField.of(viewModel.volume)
                .compose(RxLifeCycle.with(this))
                .doOnNext(v -> binding.recordPopup.icon.getDrawable().setLevel(3000 + 7000 * v / 30))
                .subscribe();
    }

    @SuppressLint("CheckResult")
    private void setPrimaryItem(Class clazz, Supplier<Fragment> constructor) {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        Observable.fromIterable(fm.getFragments())
                .doOnNext(transaction::hide)
                .filter(clazz::isInstance)
                .lastOrError()
                .subscribe(transaction::show, __ -> transaction.add(R.id.holderLayout, constructor.get()));
        transaction.commitNowAllowingStateLoss();
    }

    private void setupNews() {
        if (viewTag != ViewTag.NEWS) {
            viewTag = ViewTag.NEWS;

            binding.holderView.setVisibility(VISIBLE);
            setPrimaryItem(SecondaryNewsFragment.class, SecondaryNewsFragment::new);
        }
    }

    private void setupHello() {
        if (viewTag != ViewTag.HELLO) {
            viewTag = ViewTag.HELLO;

            setPrimaryItem(SecondaryHelloFragment.class, SecondaryHelloFragment::new);
        }
    }

    public void showMore() {
        setupHello();

        //将控件焦点清除
        binding.editVoice.clearFocus();
        if (binding.holderView.getVisibility() == VISIBLE) {//将holder隐藏，选项顶上去
            inputMethodManager.hideSoftInputFromWindow(binding.editVoice.getWindowToken(), 0);
            binding.holderView.setVisibility(GONE);
        } else {//holder显示，选项缩回
            binding.holderView.setVisibility(VISIBLE);
        }
    }

    public void sendMessage() {
        viewModel.sendMessage(binding.editVoice.getText().toString());
        binding.editVoice.getText().clear();
    }

    public void showMoreClick(View view) {
        switch (view.getId()) {
            case R.id.more_back:
                setupNews();
        }
    }

    public void voiceMethod() {
        binding.editVoice.setVisibility(GONE);
        binding.btnVoice.setVisibility(VISIBLE);
        binding.icEdit.setVisibility(VISIBLE);
        binding.icVoice.setVisibility(GONE);
        binding.editVoice.clearFocus();
        inputMethodManager.hideSoftInputFromWindow(binding.editVoice.getWindowToken(), 0);
    }

    public void editMethod() {
        binding.btnVoice.setVisibility(GONE);
        binding.editVoice.setVisibility(VISIBLE);
        binding.icVoice.setVisibility(VISIBLE);
        binding.icEdit.setVisibility(GONE);
    }

    //用于标记当前的fragment
    public enum ViewTag {
        NEWS,
        HELLO
    }
}
