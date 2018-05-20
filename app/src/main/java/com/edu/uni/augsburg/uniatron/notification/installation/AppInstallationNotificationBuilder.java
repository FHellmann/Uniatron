package com.edu.uni.augsburg.uniatron.notification.installation;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.notification.AppNotificationBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This notification will be displayed to the user when a new app is installed.
 *
 * @author Fabio Hellmann
 */
public class AppInstallationNotificationBuilder implements AppNotificationBuilder {
    private static final int DISPLAY_TIMEOUT_MINUTES = 5;

    private final Context mContext;
    private final Intent mIntent;

    /**
     * Ctr.
     *
     * @param context The context.
     * @param intent  The intent of the event.
     */
    public AppInstallationNotificationBuilder(@NonNull final Context context,
                                              @NonNull final Intent intent) {
        this.mContext = context;
        this.mIntent = intent;
    }

    @Override
    public Notification build(final String channelId) {
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
        final Intent blacklistIntent = new Intent(mContext, AppInstallationBroadcastReceiver.class);
        blacklistIntent.putExtra(
                Intent.EXTRA_INSTALLER_PACKAGE_NAME,
                getLastInstalledAppPackageName()
        );
        final PendingIntent blacklistPendingIntent = PendingIntent.getBroadcast(
                mContext,
                APP_INSTALLATION_ID,
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

    private String getLastInstalledAppPackageName() {
        final Uri data = mIntent.getData();
        if (data == null) {
            // If the intent does not provide the installed package name
            final PackageManager packageManager = mContext.getPackageManager();
            final List<ApplicationInfo> installedApplications =
                    packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            return Stream.of(installedApplications)
                         .map(applicationInfo -> {
                             try {
                                 return packageManager.getPackageInfo(
                                         applicationInfo.packageName,
                                         0
                                 );
                             } catch (PackageManager.NameNotFoundException e) {
                                 return null;
                             }
                         })
                         .filter(packageInfo -> packageInfo != null)
                         .sortBy(packageInfo -> packageInfo.firstInstallTime)
                         .findFirst()
                         .map(packageInfo -> packageInfo.packageName)
                         .orElse("");
        } else {
            // The intent provides the installed package name
            return data.getEncodedSchemeSpecificPart();
        }
    }
}
