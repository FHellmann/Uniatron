package com.edu.uni.augsburg.uniatron.notification.installation;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.notification.AppNotificationBuilder;

import java.util.concurrent.TimeUnit;

public class AppInstallationNotificationBuilder implements AppNotificationBuilder {
    private static final int DISPLAY_TIMEOUT_MINUTES = 5;

    private final Context mContext;
    private final Intent mIntent;

    public AppInstallationNotificationBuilder(@NonNull final Context context,
                                              @NonNull final Intent intent) {
        this.mContext = context;
        this.mIntent = intent;
    }

    @Override
    public Notification build(String channelId) {
        return new NotificationCompat.Builder(mContext, channelId)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(mContext.getString(R.string.app_name))
                .setContentText("App installation detected")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTimeoutAfter(TimeUnit.MINUTES.toMinutes(DISPLAY_TIMEOUT_MINUTES))
                .addAction(buildBlacklistAddAction())
                .build();
    }

    private NotificationCompat.Action buildBlacklistAddAction() {
        final String installedPackageName = mIntent.getData().getEncodedSchemeSpecificPart();

        final Intent blacklistIntent = new Intent(mContext, AppInstallationBroadcastReceiver.class);
        blacklistIntent.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, installedPackageName);
        final PendingIntent blacklistPendingIntent = PendingIntent.getBroadcast(
                mContext,
                APP_INSTALLATION_REQUEST_ID,
                blacklistIntent,
                0
        );

        final NotificationCompat.Action.Builder builder = new NotificationCompat.Action.Builder(
                R.drawable.ic_phonelink_lock_black_24dp,
                mContext.getString(R.string.pref_app_blacklist),
                blacklistPendingIntent
        );

        return builder.build();
    }
}
