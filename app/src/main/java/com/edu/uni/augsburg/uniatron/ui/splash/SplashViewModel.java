package com.edu.uni.augsburg.uniatron.ui.splash;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.support.annotation.NonNull;

import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.AppContext;
import com.edu.uni.augsburg.uniatron.AppPreferences;
import com.edu.uni.augsburg.uniatron.ui.util.Permissions;

/**
 * The {@link SplashViewModel} provides the data for
 * the {@link SplashActivity}.
 *
 * @author Fabio Hellmann
 */
public class SplashViewModel extends AndroidViewModel {
    private final AppPreferences mSharedPrefsHandler;

    /**
     * Ctr.
     *
     * @param application The application context.
     */
    public SplashViewModel(@NonNull final Application application) {
        super(application);

        final AppContext instance = AppContext.getInstance(application);
        mSharedPrefsHandler = instance.getPreferences();
    }

    /**
     * Check whether the intro is needed or not.
     *
     * @param context The context.
     * @return {@code true} if the intro is needed, {@code false} otherwise.
     */
    public boolean isIntroNeeded(@NonNull final Context context) {
        return mSharedPrefsHandler.isFirstStart()
                || Stream.of(Permissions.values()).anyMatch(permission -> permission.isNotGranted(context));
    }
}
