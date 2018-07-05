package com.edu.uni.augsburg.uniatron.ui.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.ui.CardView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppStatisticsCard implements CardView {

    private static final int TYPE = 2;
    private static final int ANIMATION_DURATION_LENGTH = 500;
    private List<PieEntry> mPieEntryList = new ArrayList<>();

    @Override
    public void update(CardView cardView) {
        mPieEntryList = ((AppStatisticsCard) cardView).mPieEntryList;
    }

    @Override
    public void onBindView(Context context, RecyclerView.ViewHolder viewHolder) {
        final ViewHolder holder = (ViewHolder) viewHolder;

        holder.mPieDataSet.clear();
        for (PieEntry pieEntry : mPieEntryList) {
            holder.mPieDataSet.addEntry(pieEntry);
        }
        holder.mAppUsagePieChart.animateY(ANIMATION_DURATION_LENGTH, Easing.EasingOption.EaseInQuad);
        holder.mAppUsagePieChart.notifyDataSetChanged();
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(Context context, ViewGroup viewGroup) {
        final View view = LayoutInflater.from(context)
                .inflate(R.layout.card_app_statistics, viewGroup, false);
        return new SummaryCard.ViewHolder(view);
    }

    public void addEntry(@NonNull final PieEntry pieEntry) {
        mPieEntryList.add(pieEntry);
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.appUsageChart)
        PieChart mAppUsagePieChart;
        private final PieDataSet mPieDataSet;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mAppUsagePieChart.setUsePercentValues(true);
            mAppUsagePieChart.getDescription().setEnabled(false);
            mAppUsagePieChart.setDrawHoleEnabled(true);
            mAppUsagePieChart.setDrawCenterText(true);
            mAppUsagePieChart.setClickable(false);
            mAppUsagePieChart.setRotationEnabled(false);
            mAppUsagePieChart.setHardwareAccelerationEnabled(true);
            mAppUsagePieChart.getLegend().setEnabled(false);
            mAppUsagePieChart.setHighlightPerTapEnabled(false);
            mAppUsagePieChart.setNoDataText(itemView.getContext().getString(R.string.home_chart_no_data));
            mAppUsagePieChart.setCenterText(itemView.getContext().getString(R.string.pie_chart_center_text));
            mAppUsagePieChart.setCenterTextColor(itemView.getContext().getResources().getColor(android.R.color.darker_gray));
            mAppUsagePieChart.setCenterTextSize(
                    itemView.getContext().getResources().getDimension(R.dimen.chart_title_text_size));
            mAppUsagePieChart.setEntryLabelColor(itemView.getContext().getResources().getColor(R.color.primaryTextColor));

            mPieDataSet = new PieDataSet(new ArrayList<>(), "");
            mPieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            final PieData pieData = new PieData(mPieDataSet);
            pieData.setValueFormatter(new PercentFormatter());
            pieData.setValueTextSize(itemView.getContext().getResources().getDimension(R.dimen.chart_value_text_size));
            pieData.setValueTextColor(itemView.getContext().getResources().getColor(R.color.primaryTextColor));
            mAppUsagePieChart.setData(pieData);
        }
    }
}
