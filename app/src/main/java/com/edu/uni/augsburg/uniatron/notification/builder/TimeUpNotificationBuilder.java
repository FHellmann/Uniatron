package com.edu.uni.augsburg.uniatron.notification.builder;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.notification.AppNotificationBuilder;
import com.edu.uni.augsburg.uniatron.notification.NotificationChannels;
import com.edu.uni.augsburg.uniatron.ui.shop.TimeCreditShopActivity;

/**
 * Notification is shown when the time is running out soon.
 */
public class TimeUpNotificationBuilder implements AppNotificationBuilder {
    private final Context mContext;
    private final Integer remainingTime;

    /**
     * Build Notification to inform user that times running out.
     *
     * @param context       the context
     * @param remainingTime remaining Time for apps in blacklist
     */
    public TimeUpNotificationBuilder(@NonNull final Context context, final Integer remainingTime) {
        this.mContext = context;
        this.remainingTime = remainingTime;
    }

    @Override
    public Notification build() {
        final int remainingTimeRounded = Math.round(remainingTime / 60.0f);
        return new NotificationCompat.Builder(mContext, NotificationChannels.TIME_UP.name())
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle(mContext.getString(R.string.channel_time_up))
                .setContentText(mContext.getString(R.string.time_notification, remainingTimeRounded))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(PendingIntent.getActivity(
                        mContext,
                        0,
                        new Intent(mContext, TimeCreditShopActivity.class),
                        PendingIntent.FLAG_CANCEL_CURRENT))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setOngoing(false)
                .setOnlyAlertOnce(true)
                .build();
    }

    @Override
    public int getId() {
        return 0;
    }
}
