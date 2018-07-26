package com.edu.uni.augsburg.uniatron.model;

import android.support.annotation.NonNull;

/**
 * The app usage entry.
 *
 * @author Fabio Hellmann
 */
public class AppUsageItem {
    private final String mPackageName;
    private final long mApplicationUsage;
    private final double mApplicationUsagePercent;

    /**
     * Ctr.
     *
     * @param packageName             The application package name.
     * @param applicationUsage        The application usage time.
     * @param applicationUsagePercent The application usage time in perecent.
     */
    AppUsageItem(@NonNull final String packageName,
                 final long applicationUsage,
                 final double applicationUsagePercent) {
        mPackageName = packageName;
        mApplicationUsage = applicationUsage;
        mApplicationUsagePercent = applicationUsagePercent;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public long getApplicationUsage() {
        return mApplicationUsage;
    }

    public double getApplicationUsagePercent() {
        return mApplicationUsagePercent;
    }
}
