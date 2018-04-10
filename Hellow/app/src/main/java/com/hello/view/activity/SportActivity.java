package com.hello.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.hello.R;
import com.hello.databinding.ActivitySportBinding;
import com.hello.model.db.table.StepInfo;
import com.hello.utils.ValidUtilKt;
import com.hello.utils.rx.RxField;
import com.hello.utils.rx.RxLifeCycle;
import com.hello.viewmodel.SportViewModel;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SportActivity extends AppActivity {
    private ActivitySportBinding binding;
    private boolean textDanced;

    @Inject
    SportViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sport);

        initChart();
        addChangeListener();
    }

    @Override
    public void onDestroy() {
        viewModel.onCleared();

        super.onDestroy();
    }

    private void addChangeListener() {
        RxField.of(viewModel.getStep())
                .skip(1)
                .compose(RxLifeCycle.resumed(this))
                .doOnNext(v -> {
                    binding.stepView.setCurrentCount(10000, v);
                    binding.energyDanceText.setText(String.valueOf(v * 0.04f));
                    binding.jouleDanceText.setText(String.valueOf(v * 0.04f * 4.1859f));
                    //根据步数的不同，显示的文字颜色不同
                    if (v >= 6000) {
                        binding.energyDanceText.setTextColor(0xFFFF0000);//红
                        binding.jouleDanceText.setTextColor(0xFFFF0000);//红
                    } else if (v >= 3000) {
                        binding.energyDanceText.setTextColor(0xFFFFCC00);//黄
                        binding.jouleDanceText.setTextColor(0xFFFFCC00);//黄
                    } else {
                        binding.energyDanceText.setTextColor(0xFF66CC00);//绿
                        binding.jouleDanceText.setTextColor(0xFF66CC00);//绿
                    }

                    //文字跳动效果
                    if (!textDanced) {
                        binding.energyDanceText.setFormat("%.0f");
                        binding.energyDanceText.setDuration(2000);
                        binding.energyDanceText.dance();

                        binding.jouleDanceText.setFormat("%.1f");
                        binding.jouleDanceText.setDuration(2000);
                        binding.jouleDanceText.dance();

                        textDanced = true;
                    }
                })
                .subscribe();

        RxField.of(viewModel.getStepInfoes())
                .compose(RxLifeCycle.resumed(this))
                .doOnNext(v -> {
                    List<BarEntry> entries = new ArrayList<>();
                    for (int i = 0; i < v.size(); i++) {
                        entries.add(new BarEntry(i, v.get(i).stepCount));
                    }

                    BarDataSet dataSet = new BarDataSet(entries, getString(R.string.text_days_steps));
                    dataSet.setDrawValues(true);
                    List<IBarDataSet> iBarDataSets = new ArrayList<>();
                    iBarDataSets.add(dataSet);

                    binding.barChart.setData(new BarData(iBarDataSets));
                    binding.barChart.invalidate();
                })
                .subscribe();
    }

    private void initChart() {
        //图标右下角的描述文字
        binding.barChart.setDescription(null);
        //不显示网格
        binding.barChart.getAxisLeft().setDrawGridLines(false);
        binding.barChart.getXAxis().setDrawGridLines(false);
        //右侧不显示Y轴
        binding.barChart.getAxisRight().setEnabled(false);
        //设置Y轴最小值
        binding.barChart.getAxisLeft().setAxisMinimum(0);
        binding.barChart.animateY(2000);
        //关闭缩放
        binding.barChart.setScaleEnabled(false);

        binding.barChart.getXAxis().setValueFormatter((value, axis) -> {
            StepInfo info = viewModel.getStepInfoes().get((int) value);

            if (info.stepCount == 0) {
                return getString(R.string.text_no_have);
            } else if (info.time.equals(LocalDate.now().toString("yyyy-MM-dd"))) {
                return getString(R.string.text_today);
            } else if (ValidUtilKt.isStrValid(info.time)) {
                return info.time.substring(6);
            } else {
                return "";
            }
        });
    }
}
