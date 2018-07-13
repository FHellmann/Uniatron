package com.edu.uni.augsburg.uniatron.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.edu.uni.augsburg.uniatron.service.handler.AppUsageHandler;
import com.edu.uni.augsburg.uniatron.service.handler.PackageChangeHandler;

/**
 * A sticky service for all the background tasks.
 *
 * @author Danilo Hoss
 */
public class StickyAppService extends Service {

    private AppUsageHandler mAppUsageHandler;
    private PackageChangeHandler mPackageAddedReceiver;

    @Nullable
    @Override
    public IBinder onBind(@NonNull final Intent intent) {
        return null;
    }

    /**
     * Executes the start of the service.
     *
     * @param intent  the intent
     * @param flags   the flags
     * @param startId the startid
     * @return the way the service is started
     */
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        mPackageAddedReceiver = PackageChangeHandler.start(this);
        mAppUsageHandler = AppUsageHandler.start(this);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mAppUsageHandler.destroy(this);
        mPackageAddedReceiver.destroy(this);
        super.onDestroy();
    }
}
