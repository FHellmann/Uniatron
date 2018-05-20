package com.edu.uni.augsburg.uniatron;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.notification.installation.AppInstallationBroadcastReceiver;

/**
 * The different broadcast receivers which are used in this app.
 *
 * @author Fabio Hellmann
 */
public enum BroadcastReceiverType {
    APP_INSTALLATION(
            new AppInstallationBroadcastReceiver(),
            Intent.ACTION_PACKAGE_ADDED,
            Intent.ACTION_PACKAGE_REPLACED
    );

    private final BroadcastReceiver broadcastReceiver;
    private final String[] actions;

    BroadcastReceiverType(BroadcastReceiver broadcastReceiver, String... actions) {
        this.broadcastReceiver = broadcastReceiver;
        this.actions = actions;
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
}
