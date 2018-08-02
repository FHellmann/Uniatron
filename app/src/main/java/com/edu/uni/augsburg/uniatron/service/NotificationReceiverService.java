package com.edu.uni.augsburg.uniatron.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;

/**
 * The new added package will be added to the blacklist by this service.
 *
 * @author Fabio Hellmann
 */
public class NotificationReceiverService extends Service {
    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        // #30: It seems like intent is sometimes null at this time.
        // Then we can not add the app automatically.
        if (intent != null && intent.hasExtra(Intent.EXTRA_RETURN_RESULT)) {
            final String packageName = intent.getStringExtra(Intent.EXTRA_RETURN_RESULT);
            final SharedPreferencesHandler preferencesHandler = MainApplication.getInstance(getBaseContext()).getSharedPreferencesHandler();
            preferencesHandler.addAppToBlacklist(packageName);
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
