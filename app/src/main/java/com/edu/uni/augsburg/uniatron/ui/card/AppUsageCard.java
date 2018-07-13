package com.edu.uni.augsburg.uniatron.ui.card;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.ui.CardViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
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

    private static final int TYPE = 10;
    private final List<AppUsageItem> mAppUsageList = new ArrayList<>();

    /**
     * Add all the entries to display.
     *
     * @param data The data to display.
     */
    public void addAll(@NonNull final List<AppUsageItem> data) {
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
        final StaggeredGridLayoutManager.LayoutParams layoutParams =
                (StaggeredGridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams();
        layoutParams.setFullSpan(true);

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
    public int getType() {
        return TYPE;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final Context context,
                                                      @NonNull final ViewGroup viewGroup) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.card_app_usage, viewGroup, false));
    }

    private long getTotalAppUsage() {
        return Stream.of(mAppUsageList).mapToLong(AppUsageItem::getApplicationUsage).sum();
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
            if (adapter.mShowSmallVisibleAmount) {
                mButtonShowAll.setText(R.string.show_only_top_five);
            } else {
                mButtonShowAll.setText(R.string.show_all);
            }
            adapter.setShowSomeItems(!adapter.mShowSmallVisibleAmount);
            adapter.notifyDataSetChanged();
        }
    }

    static final class AppUsageAdapter extends RecyclerView.Adapter<AppUsageCard.ViewHolderListItem> {
        private static final int SMALL_VISIBLE_AMOUNT = 5;
        private final Context mContext;
        private final List<AppUsageItem> mAppData;
        private boolean mShowSmallVisibleAmount = true;

        AppUsageAdapter(@NonNull final Context context,
                        @NonNull final List<AppUsageItem> appData) {
            super();
            mContext = context;
            mAppData = appData;
        }

        void setShowSomeItems(final boolean visible) {
            mShowSmallVisibleAmount = visible;
        }

        @NonNull
        @Override
        public ViewHolderListItem onCreateViewHolder(@NonNull final ViewGroup viewGroup,
                                                     final int position) {
            return new ViewHolderListItem(LayoutInflater.from(mContext)
                    .inflate(R.layout.card_app_usage_item, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderListItem viewHolderListItem,
                                     final int position) {
            final AppUsageItem item = mAppData.get(position);

            final String appLabel = item.getAppLabel();
            final Drawable appIcon = item.getAppIcon();
            final long usageTime = TimeUnit.SECONDS.convert(item.getApplicationUsage(), TimeUnit.MILLISECONDS);
            final double usageTimePercent = item.getApplicationUsagePercent();

            viewHolderListItem.mTextAppName.setText(appLabel);
            viewHolderListItem.mImageAppIcon.setImageDrawable(appIcon);
            viewHolderListItem.mTextAppUsage.setText(mContext.getString(
                    R.string.app_usage_time,
                    usageTime / 60,
                    usageTime % 60
            ));
            viewHolderListItem.mTextAppUsagePercent.setText(String.format(
                    Locale.getDefault(),
                    "%d %%",
                    Math.round(usageTimePercent)
            ));
        }

        @Override
        public int getItemCount() {
            return mAppData.size() > SMALL_VISIBLE_AMOUNT && mShowSmallVisibleAmount ? SMALL_VISIBLE_AMOUNT : mAppData.size();
        }
    }

    static final class ViewHolderListItem extends RecyclerView.ViewHolder {
        @BindView(R.id.imageAppIcon)
        ImageView mImageAppIcon;
        @BindView(R.id.textAppName)
        TextView mTextAppName;
        @BindView(R.id.textAppUsage)
        TextView mTextAppUsage;
        @BindView(R.id.textAppUsagePercent)
        TextView mTextAppUsagePercent;

        ViewHolderListItem(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static final class AppUsageItem {
        private String mAppLabel;
        private Drawable mAppIcon;
        private long mApplicationUsage;
        private double mApplicationUsagePercent;

        public void setAppLabel(final String appLabel) {
            this.mAppLabel = appLabel;
        }

        String getAppLabel() {
            return mAppLabel;
        }

        public void setAppIcon(final Drawable appIcon) {
            this.mAppIcon = appIcon;
        }

        Drawable getAppIcon() {
            return mAppIcon;
        }

        public void setApplicationUsage(final long applicationUsage) {
            this.mApplicationUsage = applicationUsage;
        }

        long getApplicationUsage() {
            return mApplicationUsage;
        }

        public void setApplicationUsagePercent(final double applicationUsagePercent) {
            this.mApplicationUsagePercent = applicationUsagePercent;
        }

        double getApplicationUsagePercent() {
            return mApplicationUsagePercent;
        }
    }
}
