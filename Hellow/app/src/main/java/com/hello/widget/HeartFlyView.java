package com.hello.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hello.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.hello.utils.DimensionUtil.dp2px;

public class HeartFlyView extends RelativeLayout {
    private List<Drawable> drawableList = new ArrayList<>();

    private int viewWidth = dp2px(getContext(), 16);
    private int viewHeight = dp2px(getContext(), 16);

    private int maxHeartNum = 8;
    private int minHeartNum = 2;

    private int riseDuration = 4000;

    private int bottomPadding = 200;
    private int originsOffset = 60;

    private float maxScale = 1.0f;
    private float minScale = 1.0f;

    private int innerDelay = 200;

    public HeartFlyView(Context context) {
        super(context);
    }

    public HeartFlyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeartFlyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDefaultDrawableList() {
        List<Drawable> drawableList = new ArrayList<>();
        drawableList.add(getResources().getDrawable(R.drawable.img_emoji));
        setDrawableList(drawableList);
    }

    public void setDrawableList(List<Drawable> drawableList) {
        this.drawableList = drawableList;
    }

    public void setRiseDuration(int riseDuration) {
        this.riseDuration = riseDuration;
    }

    public void setBottomPadding(int px) {
        this.bottomPadding = px;
    }

    public void setOriginsOffset(int px) {
        this.originsOffset = px;
    }

    public void setScaleAnimation(float maxScale, float minScale) {
        this.maxScale = maxScale;
        this.minScale = minScale;
    }

    public void setAnimationDelay(int delay) {
        this.innerDelay = delay;
    }

    public void setMaxHeartNum(int maxHeartNum) {
        this.maxHeartNum = maxHeartNum;
    }

    public void setMinHeartNum(int minHeartNum) {
        this.minHeartNum = minHeartNum;
    }

    public HeartFlyView setItemViewSize(int viewWidth, int viewHeight) {
        this.viewHeight = viewHeight;
        this.viewWidth = viewWidth;
        return this;
    }

    public HeartFlyView setGiftBoxImage(Drawable drawable, int positionX, int positionY) {
        ImageView imageView = new ImageView(getContext());
        imageView.setImageDrawable(drawable);
        addView(imageView, imageView.getWidth(), imageView.getHeight());
        imageView.setX(positionX);
        imageView.setY(positionY);
        return this;
    }

    public void startAnimation(final int rankWidth, final int rankHeight) {
        Observable.timer(innerDelay, TimeUnit.MILLISECONDS)
                .repeat((int) (Math.random() * (maxHeartNum - minHeartNum)) + minHeartNum)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> bubbleAnimation(rankWidth, rankHeight));
    }

    public void startAnimation(final int rankWidth, final int rankHeight, int count) {
        Observable.timer(innerDelay, TimeUnit.MILLISECONDS)
                .repeat(count)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> bubbleAnimation(rankWidth, rankHeight));
    }

    public void startAnimation(final int rankWidth, final int rankHeight, int delay, int count) {
        Observable.timer(delay, TimeUnit.MILLISECONDS)
                .repeat(count)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> bubbleAnimation(rankWidth, rankHeight));
    }

    private void bubbleAnimation(int rankWidth, int rankHeight) {
        rankHeight -= bottomPadding;
        int seed = (int) (Math.random() * 3);
        switch (seed) {
            case 0:
                rankWidth -= originsOffset;
                break;
            case 1:
                rankWidth += originsOffset;
                break;
            case 2:
                rankHeight -= originsOffset;
                break;
        }

        int heartDrawableIndex;
        LinearLayout.LayoutParams layoutParams
                = new LinearLayout.LayoutParams(viewWidth, viewHeight);
        heartDrawableIndex = (int) (drawableList.size() * Math.random());
        ImageView tempImageView = new ImageView(getContext());
        tempImageView.setImageDrawable(drawableList.get(heartDrawableIndex));
        addView(tempImageView, layoutParams);

        ObjectAnimator riseAlphaAnimator
                = ObjectAnimator.ofFloat(tempImageView, View.ALPHA, 1.5f, 0.0f);
        riseAlphaAnimator.setDuration(riseDuration);

        ObjectAnimator riseScaleXAnimator
                = ObjectAnimator.ofFloat(tempImageView, View.SCALE_X, minScale, maxScale);
        riseScaleXAnimator.setDuration(riseDuration);

        ObjectAnimator riseScaleYAnimator
                = ObjectAnimator.ofFloat(tempImageView, View.SCALE_Y, minScale, maxScale);
        riseScaleYAnimator.setDuration(riseDuration);

        ValueAnimator valueAnimator = getBesselAnimator(tempImageView, rankWidth, rankHeight);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(valueAnimator).with(riseAlphaAnimator)
                .with(riseScaleXAnimator).with(riseScaleYAnimator);
        animatorSet.start();
    }

    private ValueAnimator getBesselAnimator(final ImageView imageView, int rankWidth, int rankHeight) {
        float point0[] = new float[2];
        point0[0] = rankWidth / 2;
        point0[1] = rankHeight;

        float point1[] = new float[2];
        point1[0] = (float) ((rankWidth) * (0.10)) + (float) (Math.random() * (rankWidth) * (0.8));
        point1[1] = (float) (rankHeight - Math.random() * rankHeight * (0.5));

        float point2[] = new float[2];
        point2[0] = (float) (Math.random() * rankWidth);
        point2[1] = (float) (Math.random() * (rankHeight - point1[1]));

        float point3[] = new float[2];
        point3[0] = (float) (Math.random() * rankWidth);
        point3[1] = 0;

        PointEvaluator pointEvaluator = new PointEvaluator(point1, point2);
        ValueAnimator valueAnimator = ValueAnimator.ofObject(pointEvaluator, point0, point3);
        valueAnimator.setDuration(riseDuration);
        valueAnimator.addUpdateListener(animation -> {
            float[] currentPosition = (float[]) animation.getAnimatedValue();
            imageView.setTranslationX(currentPosition[0]);
            imageView.setTranslationY(currentPosition[1]);
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                removeView(imageView);
                imageView.setImageDrawable(null);
            }
        });
        return valueAnimator;
    }

    public class PointEvaluator implements TypeEvaluator<float[]> {
        private float point1[] = new float[2];
        private float point2[] = new float[2];

        public PointEvaluator(float[] point1, float[] point2) {
            this.point1 = point1;
            this.point2 = point2;
        }

        @Override
        public float[] evaluate(float fraction, float[] point0, float[] point3) {
            float[] currentPosition = new float[2];
            currentPosition[0] = point0[0] * (1 - fraction) * (1 - fraction) * (1 - fraction)
                    + point1[0] * 3 * fraction * (1 - fraction) * (1 - fraction)
                    + point2[0] * 3 * (1 - fraction) * fraction * fraction
                    + point3[0] * fraction * fraction * fraction;
            currentPosition[1] = point0[1] * (1 - fraction) * (1 - fraction) * (1 - fraction)
                    + point1[1] * 3 * fraction * (1 - fraction) * (1 - fraction)
                    + point2[1] * 3 * (1 - fraction) * fraction * fraction
                    + point3[1] * fraction * fraction * fraction;
            return currentPosition;
        }
    }

}
