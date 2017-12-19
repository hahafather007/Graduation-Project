package com.hello.view.fragment;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hello.R;
import com.hello.databinding.FragmentSecondaryNewsBinding;
import com.hello.databinding.ItemNewsTopBinding;
import com.hello.view.activity.RemindActivity;
import com.hello.view.activity.WebViewActivity;
import com.hello.viewmodel.SecondaryNewsViewModel;

import javax.inject.Inject;

import static com.hello.utils.IntentUtil.setupActivity;

public class SecondaryNewsFragment extends AppFragment {
    private FragmentSecondaryNewsBinding binding;

    @Inject
    SecondaryNewsViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_secondary_news, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = DataBindingUtil.bind(view);
        binding.setFragment(this);
        binding.setViewModel(viewModel);
    }

    public void onBindItem(ViewDataBinding binding, Object data, int position) {
        final ItemNewsTopBinding itemBinding = (ItemNewsTopBinding) binding;
        itemBinding.setFragment(this);
    }

    public void setupRemindActivity() {
        setupActivity(getContext(), RemindActivity.class);
    }

    public void openNewsDetail(String url) {
        startActivity(WebViewActivity.intentOfUrl(getContext(), url));
    }
}
