package com.edu.uni.augsburg.uniatron.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;

import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.R;

public final class NotificationSenderUtil {
    private static final String CHANNEL_ID = "UNIAtronNotificationChannel";
    @NonNull
    private final Context mContext;
    private final NotificationManagerCompat mNotificationManager;

    private int mNotificationIdCounter;

    private NotificationSenderUtil(@NonNull final Context context) {
        mContext = context;
        mNotificationManager = NotificationManagerCompat.from(context);
        createNotificationChannel(context);
    }

    public static void send(@NonNull final Context context,
                            @NonNull final Intent intent,
                            @NonNull final NotificationType type) {
        NotificationSenderUtil notificationSenderUtil = new NotificationSenderUtil(context);
        notificationSenderUtil.send(type, intent);
    }

    private void send(@NonNull final NotificationType type, @NonNull final Intent intent) {
        final AppNotificationBuilder notificationBuilder = type.build(mContext, intent);
        final Notification notification = notificationBuilder.build(CHANNEL_ID);
        mNotificationManager.notify(mNotificationIdCounter++, notification);
    }

    private void createNotificationChannel(@NonNull final Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.app_name);
            String description = context.getString(R.string.app_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other mNotificationBuilder behaviors after this
            NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);
            if (notificationManager != null
                    && Stream.of(notificationManager.getNotificationChannels())
                    .map(NotificationChannel::getId)
                    .noneMatch(channelId -> channelId.equals(CHANNEL_ID))) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
