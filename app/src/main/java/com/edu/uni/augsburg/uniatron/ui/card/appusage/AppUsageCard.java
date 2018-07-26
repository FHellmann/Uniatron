package com.edu.uni.augsburg.uniatron.ui.card.appusage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.ui.card.CardHelper;
import com.edu.uni.augsburg.uniatron.ui.card.CardViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * The card which displays the app usage data.
 *
 * @author Fabio Hellmann
 */
public class AppUsageCard implements CardViewHolder {

    private final List<AppUsageViewItem> mAppUsageList = new ArrayList<>();

    /**
     * Add all the entries to display.
     *
     * @param data The data to display.
     */
    public void addAll(@NonNull final List<AppUsageViewItem> data) {
        mAppUsageList.addAll(data);
        Collections.sort(mAppUsageList, (usage1, usage2) ->
                Long.compare(usage2.getApplicationUsage(), usage1.getApplicationUsage()));
    }

    @Override
    public void update(@NonNull final CardViewHolder cardViewHolder) {
        mAppUsageList.clear();
        addAll(((AppUsageCard) cardViewHolder).mAppUsageList);
    }

    @Override
    public void onBindView(@NonNull final Context context,
                           @NonNull final RecyclerView.ViewHolder viewHolder) {
        CardHelper.setFullSpan(viewHolder);
        final ViewHolder holder = (ViewHolder) viewHolder;

        final LinearLayoutManager layout = new LinearLayoutManager(context);
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        holder.mRecyclerView.setLayoutManager(layout);
        holder.mRecyclerView.setLayoutFrozen(true);
        holder.mRecyclerView.setAdapter(new AppUsageAdapter(context, mAppUsageList));
        final long totalAppUsage = TimeUnit.SECONDS.convert(getTotalAppUsage(), TimeUnit.MILLISECONDS);
        holder.mTextAppUsageTotal.setText(context.getString(R.string.time_in_minutes, totalAppUsage / 60, totalAppUsage % 60));
        holder.mButtonShowAll.setText(R.string.show_all);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final Context context,
                                                      @NonNull final ViewGroup viewGroup) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_app_usage, viewGroup, false));
    }

    private long getTotalAppUsage() {
        return Stream.of(mAppUsageList).mapToLong(AppUsageViewItem::getApplicationUsage).sum();
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.recyclerViewAppUsageData)
        RecyclerView mRecyclerView;
        @BindView(R.id.buttonShowAll)
        MaterialButton mButtonShowAll;
        @BindView(R.id.textAppUsageTotal)
        TextView mTextAppUsageTotal;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        /**
         * Called when the show all button is clicked.
         */
        @OnClick(R.id.buttonShowAll)
        public void onShowAllClicked() {
            final AppUsageAdapter adapter = (AppUsageAdapter) mRecyclerView.getAdapter();
            if (adapter != null) {
                if (adapter.isShowSmallVisibleAmount()) {
                    mButtonShowAll.setText(R.string.show_only_top_five);
                } else {
                    mButtonShowAll.setText(R.string.show_all);
                }
                adapter.setShowSomeItems(!adapter.isShowSmallVisibleAmount());
                adapter.notifyDataSetChanged();
            }
        }
    }
}
