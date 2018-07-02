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
import com.edu.uni.augsburg.uniatron.ui.home.shop.TimeCreditShopActivity;

public class AidFinishNotificationBuilder implements AppNotificationBuilder {
    private final Context mContext;

    /**
     * Build Notification to inform user that times running out.
     *
     * @param context       the context
     */
    public AidFinishNotificationBuilder(@NonNull final Context context) {
        this.mContext = context;
    }

    @Override
    public Notification build() {
        // TODO Define this string in the string.xml
        final String detailedText = "You have finished your learning aid!";
        return new NotificationCompat.Builder(mContext, NotificationChannels.TIME_UP.name())
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(mContext.getString(R.string.channel_time_up))
                .setContentText(detailedText)
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
