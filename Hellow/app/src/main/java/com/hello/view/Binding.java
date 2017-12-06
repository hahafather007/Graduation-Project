package com.hello.view;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.hello.widget.SingleClickListener;

public class Binding {
    @BindingAdapter("visible")
    public static void setVisibility(View view, boolean b) {
        if (b) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @BindingAdapter("android:src")
    public static void setImage(ImageView view, String img) {
        Glide.with(view.getContext()).load(img).into(view);
    }

    @BindingAdapter({"android:src", "placeholder"})
    public static void setImageWithHolder(ImageView view, String img, Drawable holder) {

    }

    @BindingAdapter("onSingleClick")
    public static void setOnSingleClickListener(View view, SingleClickListener listener) {
        view.setOnClickListener(listener);
    }
}
