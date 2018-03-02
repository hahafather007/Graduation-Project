package com.hello.widget.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.annimon.stream.Stream;
import com.hello.R;
import com.hello.databinding.ItemTulingCookItemBinding;
import com.hello.model.data.CookData;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

//菜谱使用的View
public class CookView extends LinearLayout {
    private CookData data;

    public CookView(Context context) {
        super(context);
    }

    public CookView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CookView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setData(CookData data) {
        this.data = data;

        initView();
    }

    private void initView() {
        setOrientation(VERTICAL);

        //等待100毫秒在载入，防止recyclerView跳动
        Observable.just(data.getList())
                .delay(100, TimeUnit.MILLISECONDS)
                .take(5)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(d -> Stream.of(d).forEach(v -> {
                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context
                            .LAYOUT_INFLATER_SERVICE);
                    ItemTulingCookItemBinding binding = DataBindingUtil.inflate(inflater,
                            R.layout.item_tuling_cook_item, this, false);
                    binding.setData(v);
                    addView(binding.getRoot());
                }));
    }
}
