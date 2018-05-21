package com.edu.uni.augsburg.uniatron.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.edu.uni.augsburg.uniatron.service.tasks.PackageAddedTask;
import com.edu.uni.augsburg.uniatron.service.tasks.StepCounterTask;

/**
 * The step count service collects steps and commits them to the database.
 *
 * @author Leon Wöhrl
 */
public class StickyService extends Service {

    private StepCounterTask mStepCounterTask;
    private PackageAddedTask mPackageAddedTask;

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mStepCounterTask = new StepCounterTask(this);
        mStepCounterTask.onCreate();

        mPackageAddedTask = new PackageAddedTask(this);
        mPackageAddedTask.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        // this causes the OS to restart the service if it has been force stopped
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mStepCounterTask.onDestroy();
        mPackageAddedTask.onDestroy();

        super.onDestroy();
    }
}
