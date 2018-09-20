package com.edu.uni.augsburg.uniatron.ui.onboarding;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

/**
 * The model class which contains the app package name, label and icon.
 *
 * @author Fabio Hellmann
 */
class LaunchableAppDetails {
    private final String mPackageName;
    private final String mApplicationLabel;
    private final Drawable mApplicationIcon;

    /**
     * Ctr.
     *
     * @param packageName      The package name of the app.
     * @param applicationLabel The label of the app.
     * @param applicationIcon  The icon of the app.
     */
    LaunchableAppDetails(@NonNull final String packageName,
                         @NonNull final String applicationLabel,
                         @NonNull final Drawable applicationIcon) {
        mPackageName = packageName;
        mApplicationLabel = applicationLabel;
        mApplicationIcon = applicationIcon;
    }

    /**
     * Get the package name.
     *
     * @return The package name.
     */
    public String getPackageName() {
        return mPackageName;
    }

    /**
     * Get the application label.
     *
     * @return The application label.
     */
    public String getApplicationLabel() {
        return mApplicationLabel;
    }

    /**
     * Get the application icon.
     *
     * @return The application icon.
     */
    public Drawable getApplicationIcon() {
        return mApplicationIcon;
    }
}
