package com.hello.view.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.net.Uri;
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
import com.hello.model.data.LightSwitchData;
import com.hello.model.data.MusicState;
import com.hello.model.data.TuLingData;
import com.hello.model.data.UserTalkData;
import com.hello.model.data.WeatherData;
import com.hello.utils.BrowserUtil;
import com.hello.utils.LightUtil;
import com.hello.utils.MusicUtil;
import com.hello.utils.ToastUtil;
import com.hello.utils.ValidUtilKt;
import com.hello.utils.rx.RxField;
import com.hello.utils.rx.RxLifeCycle;
import com.hello.view.Binding;
import com.hello.view.activity.MainActivity;
import com.hello.viewmodel.SecondaryHelloViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.hello.utils.MusicUtil.*;
import static com.hello.utils.NavigationUtil.openMap;
import static com.hello.utils.PackageUtil.runAppByName;

public class SecondaryHelloFragment extends AppFragment implements MainActivity.OnListenedNewsCreateListener {
    public List<Binding.Linker> linkers;

    private FragmentSecondaryHelloBinding binding;
    private CompositeDisposable disposable = new CompositeDisposable();

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

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).addCreateListener(this);
        }

        initView();
        addChangeListener();
    }

    @Override
    public void onDestroy() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).removeCreateListener(this);
        }
        MusicUtil.stopMusic();
        viewModel.onCleared();

        super.onDestroy();
    }

    @Override
    public void onOpenCreate() {
        stopMusic();
    }

    @SuppressWarnings("ConstantConditions")
    private void addChangeListener() {
        viewModel.tuLing
                .map(TuLingData::getUrl)
                .filter(ValidUtilKt::isStrValid)
                .compose(RxLifeCycle.with(this))
                .doOnNext(v -> BrowserUtil.openUrl(getContext(), v))
                .subscribe();

        RxField.ofNonNull(viewModel.music)
                .compose(RxLifeCycle.with(this))
                .doOnNext(v -> {
                    if (v.getState() == MusicState.ON) {
                        binding.musicHolder.setVisibility(VISIBLE);

                        playMusic(viewModel.music.get().getUrl(), new MediaListener() {
                            @Override
                            public void error() {
                                ToastUtil.showToast(getContext(), R.string.test_network_error);
                            }

                            @Override
                            public void complete() {
                                stopMusic();
                            }
                        }, disposable);
                    } else {
                        stopMusic();
                    }
                })
                .subscribe();

        //拨打电话
        RxField.ofNonNull(viewModel.phoneNum)
                .compose(RxLifeCycle.with(this))
                .doOnNext(v -> {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    Uri data = Uri.parse("tel:" + v);
                    intent.setData(data);
                    startActivity(intent);
                })
                .subscribe();

        RxField.ofNonNull(viewModel.location)
                .compose(RxLifeCycle.with(this))
                .doOnNext(v -> openMap(getContext(), v))
                .subscribe();

        RxField.ofNonNull(viewModel.appName)
                .compose(RxLifeCycle.with(this))
                .doOnNext(v -> runAppByName(getContext(), v))
                .subscribe();

        RxField.ofNonNull(viewModel.msgData)
                .compose(RxLifeCycle.with(this))
                .doOnNext(v -> {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    Uri data = Uri.parse("smsto:" + v.getNumber());
                    intent.setData(data);
                    intent.putExtra("sms_body", v.getMsg());
                    startActivity(intent);
                })
                .subscribe();

        RxField.ofNonNull(viewModel.lightData)
                .compose(RxLifeCycle.with(this))
                .doOnNext(v -> {
                    if (v.getState() == LightSwitchData.State.ON) {
                        LightUtil.lightSwitch(getContext(), true);
                    } else {
                        LightUtil.lightSwitch(getContext(), false);
                    }
                })
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

    public void playOrPauseMusic() {
        if (viewModel.musicPlaying.get()) {
            pauseMusic();
        } else {
            continueMusic();
        }

        viewModel.musicPlaying.set(!viewModel.musicPlaying.get());
    }

    public void stopMusic() {
        MusicUtil.stopMusic();

        binding.musicHolder.setVisibility(GONE);
    }
}
