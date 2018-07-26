package com.edu.uni.augsburg.uniatron.ui.card.appusage;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

/**
 * The item of an app usage and all it's belongings.
 *
 * @author Fabio Hellmann
 */
public class AppUsageViewItem {
    private final String mAppLabel;
    private final Drawable mAppIcon;
    private final long mApplicationUsage;
    private final double mApplicationUsagePercent;

    /**
     * Ctr.
     *
     * @param appLabel                The application label.
     * @param appIcon                 The application icon.
     * @param applicationUsage        The application usage time.
     * @param applicationUsagePercent The application usage time in perecent.
     */
    AppUsageViewItem(@NonNull final String appLabel,
                     @NonNull final Drawable appIcon,
                     final long applicationUsage,
                     final double applicationUsagePercent) {
        mAppLabel = appLabel;
        mAppIcon = appIcon;
        mApplicationUsage = applicationUsage;
        mApplicationUsagePercent = applicationUsagePercent;
    }

    public String getAppLabel() {
        return mAppLabel;
    }

    public Drawable getAppIcon() {
        return mAppIcon;
    }

    public long getApplicationUsage() {
        return mApplicationUsage;
    }

    public double getApplicationUsagePercent() {
        return mApplicationUsagePercent;
    }
}
