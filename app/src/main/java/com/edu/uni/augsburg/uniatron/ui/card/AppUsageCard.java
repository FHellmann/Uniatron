package com.edu.uni.augsburg.uniatron.ui.card;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.ui.CardView;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * The card which displays the app usage data.
 *
 * @author Fabio Hellmann
 */
public class AppUsageCard implements CardView {

    private static final int TYPE = 2;
    private Map<String, Integer> mAppUsageData = new HashMap<>();

    /**
     * Add all the entries to display.
     *
     * @param data The data to display.
     */
    public void addAll(@NonNull final Map<String, Integer> data) {
        mAppUsageData.putAll(data);
    }

    @Override
    public void update(@NonNull final CardView cardView) {
        mAppUsageData = ((AppUsageCard) cardView).mAppUsageData;
    }

    @Override
    public void onBindView(@NonNull final Context context,
                           @NonNull final RecyclerView.ViewHolder viewHolder) {
        final ViewHolder holder = (ViewHolder) viewHolder;

        final LinearLayoutManager layout = new LinearLayoutManager(context);
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        holder.mRecyclerView.setLayoutManager(layout);
        holder.mRecyclerView.setLayoutFrozen(true);
        holder.mRecyclerView.setAdapter(new StatisticsAdapter(context, mAppUsageData));
        final int totalAppUsage = getTotalAppUsage();
        holder.mTextAppUsageTotal.setText(context.getString(R.string.app_usage_total, totalAppUsage / 60, totalAppUsage % 60));
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final Context context,
                                                      @NonNull final ViewGroup viewGroup) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.card_app_statistics, viewGroup, false));
    }

    private int getTotalAppUsage() {
        return Stream.of(mAppUsageData).mapToInt(Map.Entry::getValue).sum();
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
            final StatisticsAdapter adapter = (StatisticsAdapter) mRecyclerView.getAdapter();
            if (adapter.mShowSmallVisibleAmount) {
                mButtonShowAll.setText(R.string.show_only_top_five);
            } else {
                mButtonShowAll.setText(R.string.show_all);
            }
            adapter.setShowSomeItems(!adapter.mShowSmallVisibleAmount);
            adapter.notifyDataSetChanged();
        }
    }

    static final class StatisticsAdapter extends RecyclerView.Adapter<AppUsageCard.ViewHolderListItem> {
        private static final int SMALL_VISIBLE_AMOUNT = 5;
        private final Context mContext;
        private final Map<String, Integer> mAppData;
        private boolean mShowSmallVisibleAmount = true;

        StatisticsAdapter(@NonNull final Context context,
                          @NonNull final Map<String, Integer> appData) {
            super();
            mContext = context;
            mAppData = sortByValueDesc(appData);
        }

        private static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDesc(
                @NonNull final Map<K, V> map) {
            return Stream.of(map.entrySet())
                    .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (entry1, entry2) -> entry1,
                            LinkedHashMap::new
                    ));
        }

        private double getPercentageValue(final int usageTime) {
            return usageTime * 100.0 / Stream.of(mAppData).mapToInt(Map.Entry::getValue).sum();
        }

        void setShowSomeItems(final boolean visible) {
            mShowSmallVisibleAmount = visible;
        }

        @NonNull
        @Override
        public ViewHolderListItem onCreateViewHolder(@NonNull final ViewGroup viewGroup,
                                                     final int position) {
            return new ViewHolderListItem(LayoutInflater.from(mContext)
                    .inflate(R.layout.card_app_statistics_item, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderListItem viewHolderListItem,
                                     final int position) {
            final Map.Entry<String, Integer> elementAt = getElementAt(position);

            final String applicationLabel = getApplicationLabel(mContext, elementAt.getKey())
                    .orElse(mContext.getString(R.string.unknwon));
            final Drawable applicationIcon = getApplicationIcon(mContext, elementAt.getKey())
                    .orElse(mContext.getResources().getDrawable(android.R.drawable.sym_def_app_icon));
            final int usageTime = elementAt.getValue();

            viewHolderListItem.mTextAppName.setText(applicationLabel);
            viewHolderListItem.mImageAppIcon.setImageDrawable(applicationIcon);
            viewHolderListItem.mTextAppUsage.setText(mContext.getString(R.string.app_usage_time, usageTime / 60, usageTime % 60));
            viewHolderListItem.mTextAppUsagePercent.setText(String.format(
                    Locale.getDefault(),
                    "%d %%",
                    Math.round(getPercentageValue(elementAt.getValue()))
            ));
        }

        @Override
        public int getItemCount() {
            return mAppData.size() > SMALL_VISIBLE_AMOUNT && mShowSmallVisibleAmount ? SMALL_VISIBLE_AMOUNT : mAppData.size();
        }

        private Map.Entry<String, Integer> getElementAt(final int position) {
            return Stream.of(mAppData).skip(position).findFirst().orElseThrow();
        }

        @NonNull
        private Optional<String> getApplicationLabel(@NonNull final Context context,
                                                     @NonNull final String packageName) {
            final PackageManager packageManager = context.getPackageManager();
            final List<ApplicationInfo> installedApplications = packageManager
                    .getInstalledApplications(PackageManager.GET_META_DATA);
            return Stream.of(installedApplications)
                    .filter(app -> app.packageName.equals(packageName))
                    .findFirst()
                    .map(appInfo -> packageManager.getApplicationLabel(appInfo).toString());
        }

        @NonNull
        private Optional<Drawable> getApplicationIcon(@NonNull final Context context,
                                                      @NonNull final String packageName) {
            final PackageManager packageManager = context.getPackageManager();
            final List<ApplicationInfo> installedApplications = packageManager
                    .getInstalledApplications(PackageManager.GET_META_DATA);
            return Stream.of(installedApplications)
                    .filter(app -> app.packageName.equals(packageName))
                    .findFirst()
                    .map(packageManager::getApplicationIcon);
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
}
