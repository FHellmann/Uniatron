package com.edu.uni.augsburg.uniatron.service.util;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;

import com.annimon.stream.Stream;

/**
 * A util class for services.
 *
 * @author Fabio Hellmann
 */
public final class ServiceUtil {
    private ServiceUtil() {
    }

    /**
     * Start a service.
     *
     * @param context        The context.
     * @param serviceClasses The classes of the service.
     */
    public static void startServices(@NonNull final Context context,
                                     @NonNull final Class<?>... serviceClasses) {
        Stream.of(serviceClasses)
                .map(serviceClass -> new Intent(context, serviceClass))
                .forEach(intent -> {
                    // fixes crash on post Android O devices;
                    // services cannot be started in background!
                    // IllegalStateException: Not allowed to start service Intent
                    // { StepCountService }: app is in background
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent);
                    } else {
                        context.startService(intent);
                    }
                });
    }
}
