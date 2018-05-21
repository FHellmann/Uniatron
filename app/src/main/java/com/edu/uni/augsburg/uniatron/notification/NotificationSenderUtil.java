package com.edu.uni.augsburg.uniatron.notification;

import android.content.Context;
import android.support.annotation.NonNull;

import com.annimon.stream.Stream;

/**
 * This is a helper class to send notifications backward compatible.
 *
 * @author Fabio Hellmann
 */
public final class NotificationSenderUtil {
    private NotificationSenderUtil() {
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
