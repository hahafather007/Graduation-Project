package com.hello.view.fragment;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hello.R;
import com.hello.databinding.FragmentSecondaryHelloBinding;
import com.hello.databinding.ItemAiuiCookBinding;
import com.hello.databinding.ItemTulingCookBinding;
import com.hello.databinding.ItemTulingTalkBinding;
import com.hello.model.data.CookData;
import com.hello.model.data.CookResult;
import com.hello.model.data.DescriptionData;
import com.hello.model.data.HelloTalkData;
import com.hello.model.data.TuLingData;
import com.hello.model.data.UserTalkData;
import com.hello.model.data.WeatherData;
import com.hello.utils.BrowserUtil;
import com.hello.utils.ValidUtilKt;
import com.hello.utils.rx.Observables;
import com.hello.utils.rx.RxLifeCycle;
import com.hello.view.Binding;
import com.hello.viewmodel.SecondaryHelloViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SecondaryHelloFragment extends AppFragment {
    public List<Binding.Linker> linkers;

    private FragmentSecondaryHelloBinding binding;

    @Inject
    SecondaryHelloViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_secondary_hello, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = DataBindingUtil.bind(view);
        binding.setFragment(this);
        binding.setViewModel(viewModel);

        initView();
        addChangeListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        viewModel.onCleared();
    }

    @SuppressWarnings("ConstantConditions")
    private void addChangeListener() {
        viewModel.tuLing
                .map(TuLingData::getUrl)
                .filter(ValidUtilKt::isStrValid)
                .compose(RxLifeCycle.with(this))
                .doOnNext(v -> BrowserUtil.openUrl(getContext(), v))
                .subscribe();
    }

    private void initView() {
        Binding.Linker linkerUserTalk = Binding.Linker.of(o ->
                o instanceof UserTalkData, R.layout.item_aiui_default_user);
        Binding.Linker linkerHelloTalk = Binding.Linker.of(o ->
                o instanceof HelloTalkData, R.layout.item_aiui_default_hello);
        Binding.Linker linkerCook = Binding.Linker.of(o ->
                o instanceof CookResult, R.layout.item_aiui_cook);
        Binding.Linker linkerWeather = Binding.Linker.of(o ->
                o instanceof WeatherData, R.layout.item_aiui_weather);
        Binding.Linker linkerDescription = Binding.Linker.of(o ->
                o instanceof DescriptionData, R.layout.item_aiui_description);
        Binding.Linker linkerTuLing = Binding.Linker.of(o ->
                o instanceof TuLingData, R.layout.item_tuling_talk);
        Binding.Linker linkerTuLingCook = Binding.Linker.of(o ->
                o instanceof CookData, R.layout.item_tuling_cook);

        linkers = new ArrayList<>();
        linkers.add(linkerUserTalk);
        linkers.add(linkerHelloTalk);
        linkers.add(linkerCook);
        linkers.add(linkerWeather);
        linkers.add(linkerDescription);
        linkers.add(linkerTuLing);
        linkers.add(linkerTuLingCook);
    }

    public void onBindItem(ViewDataBinding binding, Object data, int position) {
        if (data instanceof CookResult) {
            ItemAiuiCookBinding cookBinding = (ItemAiuiCookBinding) binding;
            cookBinding.setFragment(this);
        } else if (data instanceof TuLingData) {
            ItemTulingTalkBinding talkBinding = (ItemTulingTalkBinding) binding;
            talkBinding.setFragment(this);
        } else if (data instanceof CookData) {
            ItemTulingCookBinding cookBinding = (ItemTulingCookBinding) binding;
            cookBinding.cookView.updateData(((CookData) data));
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void openUrl(String url) {
        BrowserUtil.openUrl(getContext(), url);
    }
}
