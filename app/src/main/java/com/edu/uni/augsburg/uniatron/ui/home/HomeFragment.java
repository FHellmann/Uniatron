package com.edu.uni.augsburg.uniatron.ui.home;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.ui.home.dialogs.EmotionDialogFragment;
import com.edu.uni.augsburg.uniatron.ui.home.dialogs.ShopTimeCreditDialogFragment;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * The {@link HomeFragment} is the first screen the user will be seen.
 *
 * @author Fabio Hellmann
 */
public class HomeFragment extends Fragment {

    private static final int ANIMATION_DURATION_LENGTH = 500;

    @BindView(R.id.appUsageChart)
    PieChart mAppUsagePieChart;
    @BindView(R.id.stepButton)
    Button mStepButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view,
                              @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final PieDataSet pieDataSet = setupChart();

        final HomeViewModel homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);

        homeViewModel.getAppUsageOfTop5Apps(getContext()).observe(this, item -> {
            pieDataSet.clear();
            if (item != null && !item.isEmpty()) {
                Stream.of(item.entrySet())
                        .map(entry -> new PieEntry(entry.getValue().floatValue(), entry.getKey()))
                        .forEach(pieDataSet::addEntry);

                if (item.size() > HomeViewModel.MAX_COUNT) {
                    final float value = (float) Stream.of(item.values())
                            .mapToDouble(dValue -> dValue)
                            .sum();
                    pieDataSet.addEntry(new PieEntry(1 - value, getString(R.string.app_others)));
                }
            }
            mAppUsagePieChart.animateY(ANIMATION_DURATION_LENGTH, Easing.EasingOption.EaseInQuad);
            mAppUsagePieChart.notifyDataSetChanged();
        });

        homeViewModel.getRemainingAppUsageTime().observe(this, time -> {
            mAppUsagePieChart.setCenterText(
                    getString(R.string.pie_chart_center_text, time / 60, time % 60)
            );
            mAppUsagePieChart.invalidate();
        });

        homeViewModel.getRemainingStepCountToday().observe(this, stepCount -> {
            mStepButton.setText(String.valueOf(stepCount));
        });
    }

    @NonNull
    private PieDataSet setupChart() {
        mAppUsagePieChart.setUsePercentValues(true);
        mAppUsagePieChart.getDescription().setEnabled(false);
        mAppUsagePieChart.setDrawHoleEnabled(true);
        mAppUsagePieChart.setDrawCenterText(true);
        mAppUsagePieChart.setClickable(false);
        mAppUsagePieChart.setRotationEnabled(false);
        mAppUsagePieChart.setHardwareAccelerationEnabled(true);
        mAppUsagePieChart.getLegend().setEnabled(false);
        mAppUsagePieChart.setHighlightPerTapEnabled(false);
        mAppUsagePieChart.setNoDataText(getString(R.string.home_chart_no_data));
        mAppUsagePieChart.setCenterTextColor(getResources().getColor(R.color.secondaryTextColor));
        mAppUsagePieChart.setCenterTextSize(getResources()
                .getDimension(R.dimen.chart_title_text_size));
        mAppUsagePieChart.setEntryLabelTextSize(getResources()
                .getDimension(R.dimen.chart_value_text_size));
        mAppUsagePieChart.setEntryLabelColor(getResources().getColor(R.color.primaryTextColor));

        final PieDataSet pieDataSet = new PieDataSet(new ArrayList<>(), "");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        final PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(getResources().getDimension(R.dimen.chart_value_text_size));
        pieData.setValueTextColor(getResources().getColor(R.color.primaryTextColor));
        mAppUsagePieChart.setData(pieData);

        return pieDataSet;
    }

    /**
     * Is called when the step button is clicked.
     */
    @OnClick(R.id.stepButton)
    public void onStepButtonClick() {
        final FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            final ShopTimeCreditDialogFragment dialogFragment = new ShopTimeCreditDialogFragment();
            dialogFragment.setOnBuyButtonClickedListener(() -> {
                final EmotionDialogFragment dialog = new EmotionDialogFragment();
                dialog.show(fragmentManager, EmotionDialogFragment.NAME);
            });
            dialogFragment.show(fragmentManager, ShopTimeCreditDialogFragment.NAME);
        }
    }
}
