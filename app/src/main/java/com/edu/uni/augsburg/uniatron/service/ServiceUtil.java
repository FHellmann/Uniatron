package com.edu.uni.augsburg.uniatron.service;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;

/**
 * A util class to start services.
 *
 * @author Fabio Hellmann
 */
public final class ServiceUtil {
    private ServiceUtil() {
    }

    /**
     * Start a service.
     *
     * @param context      The context.
     * @param serviceClass The class of the service.
     */
    public static void startService(@NonNull final Context context,
                                    @NonNull final Class<?> serviceClass) {
        final Intent serviceIntent = new Intent(context, serviceClass);

        // fixes crash on post Android O devices;
        // services cannot be started in background!
        // IllegalStateException: Not allowed to start service Intent
        // { StepCountService }: app is in background
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }
}
