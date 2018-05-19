package com.edu.uni.augsburg.uniatron.notification;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.notification.installation.AppInstallationNotificationBuilder;

public enum NotificationType {
    APP_INSTALLATION(AppInstallationNotificationBuilder::new);

    @NonNull
    private final NotificationBuilder mNotificationBuilder;

    NotificationType(@NonNull final NotificationBuilder notificationBuilder) {
        this.mNotificationBuilder = notificationBuilder;
    }

    public AppNotificationBuilder build(@NonNull Context context, @NonNull Intent intent) {
        return mNotificationBuilder.build(context, intent);
    }

    private interface NotificationBuilder {
        AppNotificationBuilder build(@NonNull Context context, @NonNull Intent intent);
    }
}
