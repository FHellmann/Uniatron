package com.edu.uni.augsburg.uniatron.ui.onboarding;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.ui.util.Permissions;

import java.util.ArrayList;

import agency.tango.materialintroscreen.SlideFragment;

/**
 * This fragment is especially for permission.
 *
 * @author Fabio Hellmann
 */
public class PermissionFragment extends SlideFragment {
    private static final String BACKGROUND_COLOR = "background_color";
    private static final String BUTTONS_COLOR = "buttons_color";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String NEEDED_PERMISSIONS = "needed_permission";
    private static final String POSSIBLE_PERMISSIONS = "possible_permission";
    private static final String IMAGE = "image";
    private String[] mNeededPermissions;
    private String[] mPossiblePermissions;

    /**
     * Create an instance of the permission fragment.
     *
     * @param model The model.
     * @return The fragment.
     */
    public static PermissionFragment createInstance(final PermissionModel model) {
        final PermissionFragment permissionFragment = new PermissionFragment();

        final Bundle bundle = new Bundle();
        bundle.putInt(BACKGROUND_COLOR, model.getBackgroundColor());
        bundle.putInt(BUTTONS_COLOR, model.getButtonsColor());
        bundle.putInt(IMAGE, model.getImage());
        bundle.putString(TITLE, model.getTitle());
        bundle.putString(DESCRIPTION, model.getDescription());
        bundle.putStringArray(NEEDED_PERMISSIONS, model.getNeededPermissions());
        bundle.putStringArray(POSSIBLE_PERMISSIONS, model.getPossiblePermissions());

        permissionFragment.setArguments(bundle);
        return permissionFragment;
    }

    @Override
    public void initializeView() {
        super.initializeView();
        final Bundle bundle = getArguments();
        mNeededPermissions = bundle.getStringArray(NEEDED_PERMISSIONS);
        mPossiblePermissions = bundle.getStringArray(POSSIBLE_PERMISSIONS);
    }

    @Override
    public void askForPermissions() {
        final ArrayList<String> notGrantedPermissions = new ArrayList<>();

        Stream.ofNullable(mNeededPermissions)
                .withoutNulls()
                .filter(permission -> permission.length() > 0)
                .filter(this::isPermissionNotGranted)
                .collect(Collectors.toCollection(() -> notGrantedPermissions));

        Stream.ofNullable(mPossiblePermissions)
                .withoutNulls()
                .filter(permission -> permission.length() > 0)
                .filter(this::isPermissionNotGranted)
                .collect(Collectors.toCollection(() -> notGrantedPermissions));

        if (notGrantedPermissions.contains(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)) {
            Permissions.IGNORE_BATTERY_OPTIMIZATION_SETTINGS.request(getContext());
        } else if (notGrantedPermissions.contains(Manifest.permission.PACKAGE_USAGE_STATS)) {
            Permissions.USAGE_ACCESS_SETTINGS.request(getContext());
        } else {
            super.askForPermissions();
        }
    }

    @Override
    public boolean canMoveFurther() {
        return !hasAnyPermissionsToGrant();
    }

    @Override
    public boolean hasAnyPermissionsToGrant() {
        return hasPermissionsToGrant(mNeededPermissions) || hasPermissionsToGrant(mPossiblePermissions);
    }

    @Override
    public boolean hasNeededPermissionsToGrant() {
        return hasPermissionsToGrant(mNeededPermissions);
    }

    private boolean hasPermissionsToGrant(@NonNull final String... permissions) {
        return Stream.ofNullable(permissions)
                .withoutNulls()
                .filter(permission -> permission.length() > 0)
                .anyMatch(this::isPermissionNotGranted);
    }

    private boolean isPermissionNotGranted(final String permission) {
        switch (permission) {
            case Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS:
                return Permissions.IGNORE_BATTERY_OPTIMIZATION_SETTINGS.isNotGranted(getContext());
            case Manifest.permission.PACKAGE_USAGE_STATS:
                return Permissions.USAGE_ACCESS_SETTINGS.isNotGranted(getContext());
            default:
                return ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED;
        }
    }
}
