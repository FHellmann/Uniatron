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
    /** The notification channel for app blacklisting. */
    BLACKLIST(
            "BLACKLIST",
            R.string.channel_blacklist,
            NotificationManagerCompat.IMPORTANCE_DEFAULT
    );

    @NonNull
    private final String mChannelName;
    private final int mChannelNameResId;
    private final int mImportance;

    NotificationChannels(@NonNull final String channelName,
                         final int channelNameResId,
                         final int importance) {
        this.mChannelName = channelName;
        this.mChannelNameResId = channelNameResId;
        this.mImportance = importance;
    }

    /**
     * Get the channel name.
     *
     * @return The channel name.
     */
    @NonNull
    public String getName() {
        return mChannelName;
    }

    /**
     * Setup the notification channel.
     *
     * @param context The context.
     */
    public void setup(@NonNull final Context context) {
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
                    .noneMatch(channelId -> channelId.equals(mChannelName))) {
                final NotificationChannel channel = new NotificationChannel(
                        mChannelName,
                        context.getString(mChannelNameResId),
                        mImportance
                );

                notificationManager.createNotificationChannel(channel);
                Logger.d("Notification channel '" + channel + "' was created");
            }
        }
    }
}
