package com.edu.uni.augsburg.uniatron.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;

import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.R;
import com.orhanobut.logger.Logger;

/**
 * The different channels for notifications.
 *
 * @author Fabio Hellmann
 */
public enum NotificationChannels {
    /**
     * The notification channel for app blacklisting.
     */
    BLACKLIST(
            R.string.channel_blacklist,
            NotificationManagerCompat.IMPORTANCE_DEFAULT
    ),
    /**
     *  The notification channel for app usage time's running out
     */
    TIME_UP(
            R.string.channel_time_up,
            NotificationManagerCompat.IMPORTANCE_DEFAULT
            );
    //TODO modify setup and this class for times up
    //TODO add Notification Builder like PackageAddedNotificationBuilder (copy and modify)

    private final int mChannelNameResId;
    private final int mImportance;

    NotificationChannels(final int channelNameResId,
                         final int importance) {
        this.mChannelNameResId = channelNameResId;
        this.mImportance = importance;
    }

    /**
     * Setup the notification channel.
     *
     * @param context The context.
     */
    private void setup(@NonNull final Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            final NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);
            if (notificationManager != null
                    && Stream.of(notificationManager.getNotificationChannels())
                    .map(NotificationChannel::getId)
                    .noneMatch(channelId -> channelId.equals(name()))) {
                final NotificationChannel channel = new NotificationChannel(
                        name(),
                        context.getString(mChannelNameResId),
                        mImportance
                );

                notificationManager.createNotificationChannel(channel);
                Logger.d("Notification channel '" + channel + "' was created");
            }
        }
    }

    /**
     * Setup all the notification channels.
     *
     * @param context The context.
     */
    public static void setupChannels(@NonNull final Context context) {
        // setup all notification channels
        Stream.of(NotificationChannels.values()).forEach(channel -> channel.setup(context));
    }
}
