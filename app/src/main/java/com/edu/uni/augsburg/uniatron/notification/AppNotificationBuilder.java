package com.edu.uni.augsburg.uniatron.notification;

import android.app.Notification;

/**
 * @author Fabio Hellmann
 */
public interface AppNotificationBuilder {
    int APP_INSTALLATION_REQUEST_ID = 1;

    Notification build(String channelId);
}
