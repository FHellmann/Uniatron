package com.edu.uni.augsburg.uniatron;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;

import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.notification.installation.AppInstallationBroadcastReceiver;

/**
 * Initialize app broadcast receivers for implicit and explicit broadcasts (SDK > Android O)<br>
 * See: https://developer.android.com/about/versions/oreo/background#broadcasts
 *
 * @author Fabio Hellmann
 */
public final class BroadcastReceiverUtil {
    private BroadcastReceiverUtil() {}

    public static void registerReceivers(@NonNull final Context context) {
        Stream.of(Receivers.values()).forEach(type -> context.registerReceiver(
                type.getBroadcastReceiver(),
                type.getIntentFilter()
        ));
    }

    private enum Receivers {
        APP_INSTALLATION(
                new AppInstallationBroadcastReceiver(),
                Intent.ACTION_PACKAGE_ADDED,
                Intent.ACTION_PACKAGE_REPLACED
        );

        private final BroadcastReceiver broadcastReceiver;
        private final String[] actions;

        Receivers(BroadcastReceiver broadcastReceiver, String... actions) {
            this.broadcastReceiver = broadcastReceiver;
            this.actions = actions;
        }

        private BroadcastReceiver getBroadcastReceiver() {
            return broadcastReceiver;
        }

        private IntentFilter getIntentFilter() {
            final IntentFilter intentFilter = new IntentFilter();
            Stream.of(actions).forEach(intentFilter::addAction);
            return intentFilter;
        }
    }
}
