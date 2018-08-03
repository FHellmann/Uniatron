package com.edu.uni.augsburg.uniatron.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.service.handler.AppUsageDetector;
import com.edu.uni.augsburg.uniatron.service.handler.PackageChangeDetector;
import com.edu.uni.augsburg.uniatron.service.handler.StepCountDetector;
import com.orhanobut.logger.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * A sticky service for all the background tasks.
 *
 * @author Danilo Hoss
 */
public class StickyAppService extends Service {

    private List<Detector> mDetectorList;

    @Nullable
    @Override
    public IBinder onBind(@NonNull final Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d("Create detectors...");
        mDetectorList = Arrays.asList(
                PackageChangeDetector.create(this),
                AppUsageDetector.create(this),
                StepCountDetector.create(this)
        );
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Stream.of(mDetectorList).forEach(detector -> detector.start(this));
        Logger.d("Detectors started!");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Stream.of(mDetectorList).forEach(detector -> detector.destroy(this));
        Logger.d("Detectors destroyed!");
        super.onDestroy();
    }
}
