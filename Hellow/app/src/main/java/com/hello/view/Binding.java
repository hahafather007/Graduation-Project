package com.hello.view;

import android.databinding.BindingAdapter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.annimon.stream.IntPair;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.bumptech.glide.Glide;
import com.hello.R;
import com.hello.utils.ToastUtil;
import com.hello.view.binder.DataBindingItemViewBinder;
import com.hello.widget.listener.SingleClickListener;
import com.hello.widget.view.GlideApp;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhouwei.blurlibrary.EasyBlur;

import java.util.List;

import me.drakeet.multitype.ItemViewBinder;
import me.drakeet.multitype.MultiTypeAdapter;

import static com.hello.view.binder.DataBindingItemViewBinder.OnBindItem;

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
        GlideApp.with(view.getContext()).load(img).placeholder(holder).into(view);
    }

    @BindingAdapter("onSingleClick")
    public static void setOnSingleClickListener(View view, SingleClickListener listener) {
        view.setOnClickListener(listener);
    }

    @BindingAdapter("blurryGround")
    public static void setBlurryGround(View view, Drawable drawable) {
        view.setBackgroundDrawable(new BitmapDrawable(EasyBlur.with(view.getContext())
                .policy(EasyBlur.BlurPolicy.FAST_BLUR)
                .bitmap(((BitmapDrawable) drawable).getBitmap())
                .radius(10)
                .blur()));
    }

    @BindingAdapter("items")
    public static void setItems(RecyclerView view, List items) {
        final MultiTypeAdapter adapter = getOrCreateAdapter(view);
        adapter.setItems(items);
        adapter.notifyDataSetChanged();
    }

    @BindingAdapter("itemsScroll")
    public static void setItemsToScroll(RecyclerView view, List items) {
        final MultiTypeAdapter adapter = getOrCreateAdapter(view);
        adapter.setItems(items);
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() != 0) {
            view.scrollToPosition(adapter.getItemCount() - 1);
        }
    }

    private static MultiTypeAdapter getOrCreateAdapter(RecyclerView view) {
        if (!(view.getAdapter() instanceof MultiTypeAdapter))
            view.setAdapter(new MultiTypeAdapter());

        return (MultiTypeAdapter) view.getAdapter();
    }

    @BindingAdapter({"itemLayout", "onBindItem"})
    public static void setAdapter(RecyclerView view, int resId, OnBindItem onBindItem) {
        final MultiTypeAdapter adapter = getOrCreateAdapter(view);
        //noinspection unchecked
        adapter.register(Object.class, new DataBindingItemViewBinder(resId, onBindItem));
    }

    @BindingAdapter({"linkers", "onBindItem"})
    public static void setAdapter(RecyclerView view, List<Linker> linkers, OnBindItem
            onBindItem) {
        final MultiTypeAdapter adapter = getOrCreateAdapter(view);
        //noinspection unchecked
        final ItemViewBinder[] binders = Stream.of(linkers)
                .map(Linker::getLayoutId)
                .map(v -> new DataBindingItemViewBinder(v, onBindItem))
                .toArray(ItemViewBinder[]::new);
        //noinspection unchecked
        adapter.register(Object.class)
                .to(binders)
                .withLinker(o -> Stream.of(linkers)
                        .map(Linker::getMatcher)
                        .indexed()
                        .filter(v -> v.getSecond().apply(o))
                        .findFirst()
                        .map(IntPair::getFirst)
                        .orElse(0));
    }

    public static class Linker {
        private final Function<Object, Boolean> matcher;
        private final int layoutId;

        public static Linker of(Function<Object, Boolean> matcher, int layoutId) {
            return new Linker(matcher, layoutId);
        }

        Linker(Function<Object, Boolean> matcher, int layoutId) {
            this.matcher = matcher;
            this.layoutId = layoutId;
        }

        Function<Object, Boolean> getMatcher() {
            return matcher;
        }

        int getLayoutId() {
            return layoutId;
        }
    }

    @BindingAdapter("onRefresh")
    public static void setOnRefreshListener(SmartRefreshLayout view,
                                            OnRefreshListener listener) {
        view.setOnRefreshListener(listener);
    }

    @BindingAdapter("refreshing")
    public static void setIfRefresh(SmartRefreshLayout view, boolean refreshing) {
        if (!refreshing) {
            view.finishRefresh();
            ToastUtil.showToast(view.getContext(), R.string.refresh_finish);
        }
    }
}
