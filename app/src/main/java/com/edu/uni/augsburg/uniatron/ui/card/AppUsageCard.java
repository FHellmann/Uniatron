package com.edu.uni.augsburg.uniatron.ui.card;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import com.edu.uni.augsburg.uniatron.ui.CardViewHolder;
import com.elyeproj.loaderviewlibrary.LoaderImageView;
import com.elyeproj.loaderviewlibrary.LoaderTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
public class AppUsageCard implements CardViewHolder {

    private static final int TYPE = 2;
    private final List<AppUsageItem> mAppUsageList = new ArrayList<>();

    /**
     * Add all the entries to display.
     *
     * @param data The data to display.
     */
    public void addAll(@NonNull final List<AppUsageItem> data) {
        mAppUsageList.addAll(data);
        Collections.sort(mAppUsageList, (usage1, usage2) ->
                Integer.compare(usage2.getApplicationUsage(), usage1.getApplicationUsage()));
    }

    @Override
    public void update(@NonNull final CardViewHolder cardViewHolder) {
        mAppUsageList.clear();
        addAll(((AppUsageCard) cardViewHolder).mAppUsageList);
    }

    @Override
    public void onBindView(@NonNull final Context context,
                           @NonNull final RecyclerView.ViewHolder viewHolder) {
        final ViewHolder holder = (ViewHolder) viewHolder;

        final LinearLayoutManager layout = new LinearLayoutManager(context);
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        holder.mRecyclerView.setLayoutManager(layout);
        holder.mRecyclerView.setLayoutFrozen(true);
        holder.mRecyclerView.setAdapter(new StatisticsAdapter(context, mAppUsageList));
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
                .inflate(R.layout.card_app_usage, viewGroup, false));
    }

    private int getTotalAppUsage() {
        return Stream.of(mAppUsageList).mapToInt(AppUsageItem::getApplicationUsage).sum();
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
        private final List<AppUsageItem> mAppData;
        private boolean mShowSmallVisibleAmount = true;

        StatisticsAdapter(@NonNull final Context context,
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

            final String applicationPackage = item.getApplicationPackage();
            final int usageTime = item.getApplicationUsage();
            final double usageTimePercent = item.getApplicationUsagePercent();

            new AppInfoLoader(
                    mContext.getPackageManager(),
                    mContext.getString(R.string.unknwon),
                    mContext.getResources().getDrawable(android.R.drawable.sym_def_app_icon),
                    (appLabel, appIcon) -> {
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
            ).execute(applicationPackage);
        }

        @Override
        public int getItemCount() {
            return mAppData.size() > SMALL_VISIBLE_AMOUNT && mShowSmallVisibleAmount ? SMALL_VISIBLE_AMOUNT : mAppData.size();
        }
    }

    static final class ViewHolderListItem extends RecyclerView.ViewHolder {
        @BindView(R.id.imageAppIcon)
        LoaderImageView mImageAppIcon;
        @BindView(R.id.textAppName)
        LoaderTextView mTextAppName;
        @BindView(R.id.textAppUsage)
        LoaderTextView mTextAppUsage;
        @BindView(R.id.textAppUsagePercent)
        LoaderTextView mTextAppUsagePercent;

        ViewHolderListItem(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static final class AppUsageItem {
        private final String mApplicationPackage;
        private final int mApplicationUsage;
        private final double mApplicationUsagePercent;

        AppUsageItem(@NonNull final String applicationPackage,
                     final int applicationUsage,
                     final double applicationUsagePercent) {
            mApplicationPackage = applicationPackage;
            this.mApplicationUsage = applicationUsage;
            this.mApplicationUsagePercent = applicationUsagePercent;
        }

        public String getApplicationPackage() {
            return mApplicationPackage;
        }

        int getApplicationUsage() {
            return mApplicationUsage;
        }

        double getApplicationUsagePercent() {
            return mApplicationUsagePercent;
        }
    }

    static final class AppInfoLoader extends AsyncTask<String, Void, Map<String, Drawable>> {
        private final PackageManager mPackageManager;
        @NonNull
        private final String mPlaceHolderText;
        private final Drawable mPlaceholder;
        private final OnFinished mOnFinished;

        AppInfoLoader(@NonNull final PackageManager packageManager,
                      @NonNull final String placeHolderText,
                      @NonNull final Drawable placeHolderImage,
                      @NonNull final OnFinished onFinished) {
            mPackageManager = packageManager;
            mPlaceHolderText = placeHolderText;
            mPlaceholder = placeHolderImage;
            mOnFinished = onFinished;
        }

        @Override
        protected Map<String, Drawable> doInBackground(@NonNull final String... packageName) {
            final HashMap<String, Drawable> map = new HashMap<>();

            final String appPackageName = packageName[0];
            final String applicationLabel = getApplicationLabel(appPackageName);
            final Drawable applicationIcon = getApplicationIcon(appPackageName);
            map.put(applicationLabel, applicationIcon);

            return map;
        }

        @Override
        protected void onPostExecute(Map<String, Drawable> result) {
            Stream.of(result).findFirst()
                    .ifPresent(entry -> mOnFinished.finished(entry.getKey(), entry.getValue()));
        }

        @NonNull
        private String getApplicationLabel(@NonNull final String packageName) {
            final List<ApplicationInfo> installedApplications = mPackageManager
                    .getInstalledApplications(PackageManager.GET_META_DATA);
            return Stream.of(installedApplications)
                    .filter(app -> app.packageName.equals(packageName))
                    .findFirst()
                    .map(appInfo -> mPackageManager.getApplicationLabel(appInfo).toString())
                    .orElse(mPlaceHolderText);
        }

        @NonNull
        private Drawable getApplicationIcon(@NonNull final String packageName) {
            final List<ApplicationInfo> installedApplications = mPackageManager
                    .getInstalledApplications(PackageManager.GET_META_DATA);
            return Stream.of(installedApplications)
                    .filter(app -> app.packageName.equals(packageName))
                    .findFirst()
                    .map(mPackageManager::getApplicationIcon)
                    .orElse(mPlaceholder);
        }

        interface OnFinished {
            void finished(@NonNull final String appLabel, @NonNull final Drawable appIcon);
        }
    }
}
