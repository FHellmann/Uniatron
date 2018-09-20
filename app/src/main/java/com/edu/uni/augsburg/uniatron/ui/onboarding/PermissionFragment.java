package com.edu.uni.augsburg.uniatron.ui.onboarding;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.ui.util.Permissions;

import java.util.ArrayList;
import java.util.List;

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
        notGrantedPermissions.addAll(getPermissionsToGrant(mNeededPermissions));
        notGrantedPermissions.addAll(getPermissionsToGrant(mPossiblePermissions));

        final Optional<Permissions> permissionHandler = Stream.of(Permissions.values())
                .filter(permission -> notGrantedPermissions.contains(permission.getPermissionName()))
                .findFirst();
        if (permissionHandler.isPresent()) {
            permissionHandler.get().request(getContext());
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
        return !getPermissionsToGrant(mNeededPermissions).isEmpty() || !getPermissionsToGrant(mPossiblePermissions).isEmpty();
    }

    @Override
    public boolean hasNeededPermissionsToGrant() {
        return !getPermissionsToGrant(mNeededPermissions).isEmpty();
    }

    private List<String> getPermissionsToGrant(@NonNull final String... permissions) {
        return Stream.ofNullable(permissions)
                .withoutNulls()
                .filter(permission -> permission.length() > 0)
                .filter(this::isPermissionNotGranted)
                .collect(Collectors.toList());
    }

    private boolean isPermissionNotGranted(final String permission) {
        return Stream.of(Permissions.values())
                .filter(permissionHandler -> permissionHandler.getPermissionName().equalsIgnoreCase(permission))
                .findFirst()
                .map(permissionHandler -> permissionHandler.isNotGranted(getContext()))
                .orElse(ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED);
    }
}
