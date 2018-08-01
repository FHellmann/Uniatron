package com.edu.uni.augsburg.uniatron.service.handler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;

import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.service.StickyAppService;

/**
 * The BroadCastReceiver starts the StepCountService as soon as boot is completed.
 *
 * @author Leon Wöhrl
 */
public class BootCompletedDetector extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equalsIgnoreCase(intent.getAction())) {
            startServices(context, StickyAppService.class);
        }
    }

    private static void startServices(@NonNull final Context context,
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
