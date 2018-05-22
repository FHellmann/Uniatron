package com.edu.uni.augsburg.uniatron.notification;

import android.app.Notification;

/**
 * The builder creates the notification.
 *
 * @author Fabio Hellmann
 */
public interface AppNotificationBuilder {
    /** The app installation request id. */
    int APP_INSTALLATION_ID = 1;

    /**
     * Build the notification.
     *
     * @return The notification.
     */
    Notification build();

    /**
     * Get the id of the notification.
     *
     * @return The notification id.
     */
    int getId();
}