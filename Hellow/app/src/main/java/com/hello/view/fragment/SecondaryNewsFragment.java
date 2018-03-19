package com.hello.view.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.hello.R;
import com.hello.databinding.FragmentSecondaryNewsBinding;
import com.hello.databinding.ItemNewsTopBinding;
import com.hello.utils.rx.RxField;
import com.hello.utils.rx.RxLifeCycle;
import com.hello.view.activity.SportActivity;
import com.hello.view.activity.WebViewActivity;
import com.hello.viewmodel.SecondaryNewsViewModel;

import javax.inject.Inject;

import me.drakeet.multitype.MultiTypeAdapter;

import static com.hello.utils.IntentUtil.setupActivity;

public class SecondaryNewsFragment extends AppFragment {
    private FragmentSecondaryNewsBinding binding;

    @Inject
    SecondaryNewsViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_secondary_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = DataBindingUtil.bind(view);
        binding.setFragment(this);
        binding.setViewModel(viewModel);
        binding.recyclerView.setLayoutAnimation(
                AnimationUtils.loadLayoutAnimation(getContext(), R.anim.news_list_anim));

        addChangeListener();
    }

    private void addChangeListener() {
        RxField.of(viewModel.newsList)
                .skip(1)
                .compose(RxLifeCycle.with(this))
                .subscribe(v -> {
                    MultiTypeAdapter adapter = ((MultiTypeAdapter) binding.recyclerView.getAdapter());
                    int beforeSize = adapter.getItemCount();
                    if (!viewModel.isNewData()) {
                        adapter.setItems(v);
                        adapter.notifyItemRangeInserted(beforeSize, adapter.getItemCount());
                    } else {
                        adapter.setItems(v);
                        adapter.notifyDataSetChanged();
                        binding.recyclerView.scheduleLayoutAnimation();
                    }
                });
    }

    public void onBindItem(ViewDataBinding binding, Object data, int position) {
        final ItemNewsTopBinding itemBinding = (ItemNewsTopBinding) binding;
        itemBinding.setFragment(this);
    }

    //打开系统自带新建提醒界面
    public void openCreateCalender() {
        startActivity(new Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI));
    }

    public void setupSportActivity() {
        setupActivity(getContext(), SportActivity.class);
    }

    public void openNewsDetail(String url) {
        startActivity(WebViewActivity.intentOfUrl(getContext(), url, getString(R.string.title_news_top)));
    }
}
