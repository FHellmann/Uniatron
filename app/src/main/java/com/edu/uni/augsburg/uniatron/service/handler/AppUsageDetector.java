package com.edu.uni.augsburg.uniatron.service.handler;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;

import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.notification.builder.TimeUpNotificationBuilder;
import com.edu.uni.augsburg.uniatron.service.Detector;
import com.edu.uni.augsburg.uniatron.service.StickyAppService;
import com.edu.uni.augsburg.uniatron.ui.MainActivity;
import com.edu.uni.augsburg.uniatron.ui.shop.TimeCreditShopActivity;
import com.rvalerio.fgchecker.AppChecker;

import java.util.concurrent.TimeUnit;

/**
 * The screen on and off events will be handled by this class.
 *
 * @author Fabio Hellmann
 */
public final class AppUsageDetector extends BroadcastReceiver implements Detector {

    private static final int NOTIFICATION_ONE_MINUTE = 1;
    private static final int NOTIFICATION_FIVE_MINUTES = 5;
    private static final int NOTIFICATION_TEN_MINUTES = 10;
    private static final int DELAY_IN_MILLISECONDS = 1000;

    private final AppChecker mAppChecker = new AppChecker();
    private final AppUsageModel mModel;

    /**
     * Ctr.
     *
     * @param context The context.
     */
    private AppUsageDetector(@NonNull final Context context) {
        super();
        mModel = new AppUsageModel(
                context,
                packageName -> blockByTimeCreditIfTimeUp(context),
                packageName -> blockByLearningAid(context)
        );
        mModel.addNotifyOnTimeUpSoonListeners(
                remainingTime -> showNotificationIfTimeAlmostUp(context, remainingTime),
                TimeUnit.MILLISECONDS.convert(NOTIFICATION_ONE_MINUTE, TimeUnit.MINUTES),
                TimeUnit.MILLISECONDS.convert(NOTIFICATION_FIVE_MINUTES, TimeUnit.MINUTES),
                TimeUnit.MILLISECONDS.convert(NOTIFICATION_TEN_MINUTES, TimeUnit.MINUTES)
        );
    }

    @Override
    public void onReceive(@NonNull final Context context, @NonNull final Intent intent) {
        final String action = intent.getAction();
        if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            stopAppChecker();
        } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
            startAppChecker(context);
        }
        context.startService(new Intent(context, StickyAppService.class));
    }

    private void startAppChecker(@NonNull final Context context) {
        stopAppChecker(); // In case the checker is already running

        final AppChecker checker = mAppChecker
                .whenOther(packageName -> mModel.onAppUsed(packageName, DELAY_IN_MILLISECONDS))
                .timeout(DELAY_IN_MILLISECONDS);
        Stream.of(mModel.getPackageFilters(context))
                .forEach(packageName -> checker.when(packageName, process -> {
                    // App package to filter
                }));
        checker.start(context);
    }

    private void stopAppChecker() {
        mAppChecker.stop();
    }

    private void showNotificationIfTimeAlmostUp(@NonNull final Context context,
                                                final long remainingTime) {
        final TimeUpNotificationBuilder builder = new TimeUpNotificationBuilder(
                context,
                remainingTime
        );
        final Notification notification = builder.build();
        final int notificationId = builder.getId();
        NotificationManagerCompat.from(context).notify(notificationId, notification);
    }

    private void blockByTimeCreditIfTimeUp(@NonNull final Context context) {
        final Intent blockIntent = new Intent(context, MainActivity.class);
        blockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(blockIntent);
    }

    private void blockByLearningAid(@NonNull final Context context) {
        final Intent blockIntent = new Intent(context, TimeCreditShopActivity.class);
        blockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(blockIntent);
    }

    @Override
    public void start(@NonNull final Context context) {
        final IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.SCREEN_OFF");
        context.registerReceiver(this, filter);
        startAppChecker(context);
    }

    @Override
    public void destroy(@NonNull final Context context) {
        mModel.destroy();
        context.unregisterReceiver(this);
    }

    /**
     * Creates the detector.
     *
     * @param context The context.
     * @return The detector.
     */
    public static Detector create(@NonNull final Context context) {
        return new AppUsageDetector(context);
    }
}
