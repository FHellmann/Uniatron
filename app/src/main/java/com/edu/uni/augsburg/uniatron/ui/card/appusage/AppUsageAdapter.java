package com.edu.uni.augsburg.uniatron.ui.card.appusage;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edu.uni.augsburg.uniatron.R;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The adapter for the app usage items displayed in a list
 * inside the app usage card.
 *
 * @author Fabio Hellmann
 */
public class AppUsageAdapter extends RecyclerView.Adapter<AppUsageAdapter.ViewHolderListItem> {
    private static final int SMALL_VISIBLE_AMOUNT = 5;
    private final Context mContext;
    private final List<AppUsageViewItem> mAppData;
    private boolean mShowSmallVisibleAmount = true;

    AppUsageAdapter(@NonNull final Context context,
                    @NonNull final List<AppUsageViewItem> appData) {
        super();
        mContext = context;
        mAppData = appData;
    }

    void setShowSomeItems(final boolean visible) {
        mShowSmallVisibleAmount = visible;
    }

    boolean isShowSmallVisibleAmount() {
        return mShowSmallVisibleAmount;
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
        final AppUsageViewItem item = mAppData.get(position);

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
