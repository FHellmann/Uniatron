package com.edu.uni.augsburg.uniatron.ui.onboarding;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

/**
 * The builder class for the {@link PermissionFragment}.
 *
 * @author Fabio Hellmann
 */
public class PermissionFragmentBuilder {
    private final PermissionModel mModel = new PermissionModel();

    /**
     * Set the background color.
     *
     * @param backgroundColor The background color.
     * @return The builder.
     */
    public PermissionFragmentBuilder backgroundColor(@ColorRes final int backgroundColor) {
        mModel.setBackgroundColor(backgroundColor);
        return this;
    }

    /**
     * Set the button color.
     *
     * @param buttonsColor The button color.
     * @return The builder.
     */
    public PermissionFragmentBuilder buttonsColor(@ColorRes final int buttonsColor) {
        mModel.setButtonsColor(buttonsColor);
        return this;
    }

    /**
     * Set the mTitle.
     *
     * @param title The mTitle.
     * @return The builder.
     */
    public PermissionFragmentBuilder title(final String title) {
        mModel.setTitle(title);
        return this;
    }

    /**
     * Set the mDescription.
     *
     * @param description The mDescription.
     * @return The builder.
     */
    public PermissionFragmentBuilder description(final String description) {
        mModel.setDescription(description);
        return this;
    }

    /**
     * Set the needed permissions.
     *
     * @param neededPermissions The needed permissions.
     * @return The builder.
     */
    public PermissionFragmentBuilder neededPermissions(final String... neededPermissions) {
        mModel.setNeededPermissions(neededPermissions);
        return this;
    }

    /**
     * Set the possible permissions.
     *
     * @param possiblePermissions The possible permissions.
     * @return The builder.
     */
    public PermissionFragmentBuilder possiblePermissions(final String... possiblePermissions) {
        mModel.setPossiblePermissions(possiblePermissions);
        return this;
    }

    /**
     * Set the image.
     *
     * @param image The image.
     * @return The builder.
     */
    public PermissionFragmentBuilder image(@DrawableRes final int image) {
        mModel.setImage(image);
        return this;
    }

    /**
     * Build the {@link PermissionFragment}.
     *
     * @return The fragment.
     */
    public PermissionFragment build() {
        return PermissionFragment.createInstance(mModel);
    }
}
