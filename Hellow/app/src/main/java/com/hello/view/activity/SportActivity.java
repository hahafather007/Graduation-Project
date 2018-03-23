package com.hello.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.hello.R;
import com.hello.databinding.ActivitySportBinding;
import com.hello.utils.rx.RxField;
import com.hello.utils.rx.RxLifeCycle;
import com.hello.viewmodel.SportViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SportActivity extends AppActivity {
    private ActivitySportBinding binding;

    @Inject
    SportViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sport);

        initChart();
        addChangeListener();

        viewModel.initStepInfoes();
    }

    private void addChangeListener() {
        RxField.of(viewModel.getStep())
                .compose(RxLifeCycle.resumed(this))
                .subscribe(v -> binding.stepView.setCurrentCount(10000, v));

        RxField.of(viewModel.getStepInfoes())
                .compose(RxLifeCycle.resumed(this))
                .subscribe(v -> {
                    List<BarEntry> entries = new ArrayList<>();
                    for (int i = 0; i < v.size(); i++) {
                        entries.add(new BarEntry(i + 1, v.get(i)));
                    }

                    BarDataSet dataSet = new BarDataSet(entries, "哈哈哈");
                    dataSet.setDrawValues(true);
                    List<IBarDataSet> iBarDataSets = new ArrayList<>();
                    iBarDataSets.add(dataSet);

                    binding.barChart.setData(new BarData(iBarDataSets));
                    binding.barChart.invalidate();
                });
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
    }
}
