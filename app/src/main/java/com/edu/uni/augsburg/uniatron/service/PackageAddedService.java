package com.edu.uni.augsburg.uniatron.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.edu.uni.augsburg.uniatron.service.handler.PackageAddedHandler;

/**
 * A service to listen on package added events.
 *
 * @author Fabio Hellmann
 */
public class PackageAddedService extends Service {
    private BroadcastReceiver mPackageAddedReceiver = new PackageAddedHandler();

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        final IntentFilter intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addDataScheme("package");
        registerReceiver(mPackageAddedReceiver, intentFilter);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mPackageAddedReceiver);

        super.onDestroy();
    }
}
