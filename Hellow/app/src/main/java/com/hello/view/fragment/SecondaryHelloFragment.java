package com.hello.view.fragment;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hello.R;
import com.hello.databinding.FragmentSecondaryHelloBinding;
import com.hello.databinding.ItemAiuiCookBinding;
import com.hello.model.data.CookResult;
import com.hello.model.data.HelloTalkData;
import com.hello.model.data.UserTalkData;
import com.hello.model.data.WeatherData;
import com.hello.view.Binding;
import com.hello.viewmodel.SecondaryHelloViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SecondaryHelloFragment extends AppFragment {
    private FragmentSecondaryHelloBinding binding;
    public List<Binding.Linker> linkers;

    @Inject
    SecondaryHelloViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_secondary_hello, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = DataBindingUtil.bind(view);
        binding.setFragment(this);
        binding.setViewModel(viewModel);

        initView();
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

        linkers = new ArrayList<>();
        linkers.add(linkerUserTalk);
        linkers.add(linkerHelloTalk);
        linkers.add(linkerCook);
        linkers.add(linkerWeather);
    }

    public void onBindItem(ViewDataBinding binding, Object data, int position) {
        if (data instanceof CookResult) {
            ItemAiuiCookBinding cookBinding = (ItemAiuiCookBinding) binding;
            cookBinding.setFragment(this);
        }
    }
}
