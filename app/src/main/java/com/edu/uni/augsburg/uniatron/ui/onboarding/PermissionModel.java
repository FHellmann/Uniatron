package com.edu.uni.augsburg.uniatron.ui.onboarding;

import com.annimon.stream.Stream;

/**
 * The permission model contains all the relevant data
 * for a {@link PermissionFragment}.
 *
 * @author Fabio Hellmann
 */
class PermissionModel {
    private int mBackgroundColor;
    private int mButtonsColor;
    private String mTitle;
    private String mDescription;
    private String[] mNeededPermissions;
    private String[] mPossiblePermissions;
    private int mImage;

    /**
     * Get the background color.
     *
     * @return The background color.
     */
    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    /**
     * Get the button color.
     *
     * @return The button color.
     */
    public int getButtonsColor() {
        return mButtonsColor;
    }

    /**
     * Get the title.
     *
     * @return The title.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Get the description.
     *
     * @return The description.
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Get the needed permissions.
     *
     * @return The needed permissions.
     */
    public String[] getNeededPermissions() {
        return Stream.ofNullable(mNeededPermissions).toArray(String[]::new);
    }

    /**
     * Get the possible permissions.
     *
     * @return The possible permissions.
     */
    public String[] getPossiblePermissions() {
        return Stream.ofNullable(mPossiblePermissions).toArray(String[]::new);
    }

    /**
     * Get the image.
     *
     * @return The image.
     */
    public int getImage() {
        return mImage;
    }

    /**
     * Set the background color
     *
     * @param backgroundColor The background color.
     */
    public void setBackgroundColor(final int backgroundColor) {
        mBackgroundColor = backgroundColor;
    }

    /**
     * Set the button color.
     *
     * @param buttonsColor The button color.
     */
    public void setButtonsColor(final int buttonsColor) {
        mButtonsColor = buttonsColor;
    }

    /**
     * Set the title.
     *
     * @param title The title.
     */
    public void setTitle(final String title) {
        mTitle = title;
    }

    /**
     * Set the description.
     *
     * @param description The description.
     */
    public void setDescription(final String description) {
        mDescription = description;
    }

    /**
     * Set the needed permissions.
     *
     * @param neededPermissions The needed permissions.
     */
    public void setNeededPermissions(final String... neededPermissions) {
        mNeededPermissions = Stream.ofNullable(neededPermissions).toArray(String[]::new);
    }

    /**
     * Set the possible permissions.
     *
     * @param possiblePermissions The possible permissions.
     */
    public void setPossiblePermissions(final String... possiblePermissions) {
        mPossiblePermissions = Stream.ofNullable(possiblePermissions).toArray(String[]::new);
    }

    /**
     * Set the image.
     *
     * @param image The image.
     */
    public void setImage(final int image) {
        mImage = image;
    }
}
