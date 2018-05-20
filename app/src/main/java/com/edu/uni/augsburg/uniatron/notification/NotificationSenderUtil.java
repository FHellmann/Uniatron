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

/**
 * This is a helper class to send notifications backward compatible.
 *
 * @author Fabio Hellmann
 */
public final class NotificationSenderUtil {
    private static final String CHANNEL_ID = "UNIAtronNotificationChannel";
    private static int mNotificationIdCounter;

    private NotificationSenderUtil() {
    }

    /**
     * Send a notification.
     *
     * @param context The context.
     * @param intent  The intent of the event.
     * @param type    The type of notification to send.
     */
    public static void send(@NonNull final Context context,
                            @NonNull final Intent intent,
                            @NonNull final NotificationType type) {
        createNotificationChannel(context);

        final NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);
        final AppNotificationBuilder notificationBuilder = type.build(context, intent);
        final Notification notification = notificationBuilder.build(CHANNEL_ID);
        notificationManager.notify(mNotificationIdCounter++, notification);
    }

    private static void createNotificationChannel(@NonNull final Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final CharSequence name = context.getString(R.string.app_name);
            final String description = context.getString(R.string.app_channel_description);
            final int importance = NotificationManager.IMPORTANCE_DEFAULT;
            final NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other mNotificationBuilder behaviors after this
            final NotificationManager notificationManager =
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
