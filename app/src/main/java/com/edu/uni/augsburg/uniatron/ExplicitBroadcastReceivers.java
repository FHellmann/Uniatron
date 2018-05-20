package com.edu.uni.augsburg.uniatron;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;

import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.notification.installation.AppInstallationBroadcastReceiver;

import java.util.Arrays;
import java.util.List;

/**
 * The different broadcast receivers which are used in this app.
 *
 * @author Fabio Hellmann
 */
public enum ExplicitBroadcastReceivers {
    /** The app installation detection broadcast receiver. */
    APP_INSTALLATION(
            new AppInstallationBroadcastReceiver(),
            Intent.ACTION_PACKAGE_ADDED,
            Intent.ACTION_PACKAGE_REPLACED
    );

    private final BroadcastReceiver broadcastReceiver;
    private final List<String> actions;

    ExplicitBroadcastReceivers(final BroadcastReceiver broadcastReceiver, final String... actions) {
        this.broadcastReceiver = broadcastReceiver;
        this.actions = Arrays.asList(actions);
    }

    /**
     * Get the broadcast receiver.
     *
     * @return The broadcast receiver.
     */
    public BroadcastReceiver getBroadcastReceiver() {
        return broadcastReceiver;
    }

    /**
     * Get the intent filter with all the actions.
     *
     * @return The intent filter.
     */
    public IntentFilter getIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        Stream.of(actions).forEach(intentFilter::addAction);
        return intentFilter;
    }

    /**
     * Register the broadcast receiver on the context.
     *
     * @param context The context.
     */
    public void register(@NonNull final Context context) {
        context.registerReceiver(getBroadcastReceiver(), getIntentFilter());
    }
}
