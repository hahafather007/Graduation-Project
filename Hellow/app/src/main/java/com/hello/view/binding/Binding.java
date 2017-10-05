package com.hello.view.binding;

import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2017/10/5.
 */

public class Binding {
    @BindingAdapter("visibility")
    public static void setVisibility(View view, boolean b) {
        if (b) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }
    @BindingAdapter("layout_alignParentBottom")
    public static void setLayout_alignParentBottom(View view,boolean b){
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams ) view.getLayoutParams();
        if (b){
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }else {
            params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }
        view.setLayoutParams(params);
    }
}
