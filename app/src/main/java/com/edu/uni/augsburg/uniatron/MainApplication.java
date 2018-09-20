package com.edu.uni.augsburg.uniatron;

import android.app.Application;
import android.support.annotation.Nullable;

import com.edu.uni.augsburg.uniatron.domain.QueryProvider;
import com.edu.uni.augsburg.uniatron.domain.dao.AppUsageDao;
import com.edu.uni.augsburg.uniatron.domain.dao.EmotionDao;
import com.edu.uni.augsburg.uniatron.domain.dao.StepCountDao;
import com.edu.uni.augsburg.uniatron.domain.dao.SummaryDao;
import com.edu.uni.augsburg.uniatron.domain.dao.TimeCreditDao;
import com.edu.uni.augsburg.uniatron.notification.NotificationChannels;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

/**
 * The application context for this app.
 *
 * @author Fabio Hellmann
 */
public class MainApplication extends Application implements AppContext {
    private AppPreferences mPreferences;
    private QueryProvider mQueryProvider;

    @Override
    public AppPreferences getPreferences() {
        return mPreferences;
    }

    @Override
    public AppUsageDao getAppUsageDao() {
        return AppUsageDao.create(mQueryProvider);
    }

    @Override
    public EmotionDao getEmotionDao() {
        return EmotionDao.create(mQueryProvider);
    }

    @Override
    public StepCountDao getStepCountDao() {
        return StepCountDao.create(mQueryProvider);
    }

    @Override
    public SummaryDao getSummaryDao() {
        return SummaryDao.create(mQueryProvider);
    }

    @Override
    public TimeCreditDao getTimeCreditDao() {
        return TimeCreditDao.create(mQueryProvider);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override
            public boolean isLoggable(final int priority, @Nullable final String tag) {
                return BuildConfig.DEBUG;
            }
        });
        mQueryProvider = QueryProvider.create(this);
        mPreferences = new SharedPreferencesHandler(this);
        NotificationChannels.setupChannels(this);
    }
}
