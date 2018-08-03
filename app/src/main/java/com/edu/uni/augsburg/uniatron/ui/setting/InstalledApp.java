package com.edu.uni.augsburg.uniatron.ui.setting;

/**
 * A model object for the loaded installed application.
 *
 * @author Fabio Hellmann
 */
public class InstalledApp {
    private final String mPackageName;
    private final String mLabel;

    InstalledApp(final String packageName, final String label) {
        mPackageName = packageName;
        mLabel = label;
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
    public String getLabel() {
        return mLabel;
    }
}
