package com.edu.uni.augsburg.uniatron.notification;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.notification.installation.AppInstallationNotificationBuilder;

/**
 * The different notifications.
 *
 * @author Fabio Hellmann
 */
public enum NotificationType {
    /** The app installation notification. */
    APP_INSTALLATION(AppInstallationNotificationBuilder::new);

    @NonNull
    private final NotificationBuilder mNotificationBuilder;

    NotificationType(@NonNull final NotificationBuilder notificationBuilder) {
        this.mNotificationBuilder = notificationBuilder;
    }

    /**
     * Creates the notification builder.
     *
     * @param context The context.
     * @param intent  The intent of the event.
     *
     * @return The notification builder.
     */
    public AppNotificationBuilder build(@NonNull final Context context,
                                        @NonNull final Intent intent) {
        return mNotificationBuilder.build(context, intent);
    }

    private interface NotificationBuilder {
        AppNotificationBuilder build(@NonNull Context context, @NonNull Intent intent);
    }
}
