package com.edu.uni.augsburg.uniatron.ui.home;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.ui.MainActivity;
import com.edu.uni.augsburg.uniatron.ui.home.shop.TimeCreditShopActivity;
import com.edu.uni.augsburg.uniatron.ui.setting.SettingActivity;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.orhanobut.logger.Logger;

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

        setupBottomNavigation();
        final PieDataSet pieDataSet = setupChart();

        final HomeViewModel homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        homeViewModel.getAppUsageOfTop5Apps(getContext()).observe(this, item -> {
            pieDataSet.clear();
            if (item != null && !item.isEmpty()) {
                Stream.of(item.entrySet())
                        .map(entry -> new PieEntry(entry.getValue().floatValue(), entry.getKey()))
                        .forEach(pieDataSet::addEntry);

                if (item.size() == HomeViewModel.MAX_COUNT) {
                    final float value = (float) Stream.of(item.values())
                            .mapToDouble(dValue -> dValue)
                            .sum();
                    pieDataSet.addEntry(new PieEntry(1 - value, getString(R.string.app_others)));
                }
            }
            mAppUsagePieChart.animateY(ANIMATION_DURATION_LENGTH, Easing.EasingOption.EaseInQuad);
            mAppUsagePieChart.notifyDataSetChanged();
        });
    }

    private void setupBottomNavigation() {
        final MainActivity activity = (MainActivity) getActivity();
        activity.addTabListener(this, () -> {
            final BottomAppBar bottomAppBar = activity.getBottomAppBar();
            bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
            bottomAppBar.replaceMenu(R.menu.home);
            bottomAppBar.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.setting:
                        final Intent nextIntent = new Intent(getContext(), SettingActivity.class);
                        TaskStackBuilder.create(getContext())
                                .addNextIntentWithParentStack(nextIntent)
                                .startActivities();
                        return true;
                    default:
                        return false;
                }
            });
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
        mAppUsagePieChart.setCenterText(getString(R.string.pie_chart_center_text));
        mAppUsagePieChart.setCenterTextColor(getResources().getColor(android.R.color.darker_gray));
        mAppUsagePieChart.setCenterTextSize(
                getResources().getDimension(R.dimen.chart_title_text_size));
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
}
