package com.edu.uni.augsburg.uniatron.domain;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.annimon.stream.IntStream;
import com.edu.uni.augsburg.uniatron.domain.AppDatabase;
import com.edu.uni.augsburg.uniatron.domain.model.AppUsageEntity;
import com.edu.uni.augsburg.uniatron.domain.model.EmotionEntity;
import com.edu.uni.augsburg.uniatron.domain.model.StepCountEntity;
import com.edu.uni.augsburg.uniatron.domain.model.TimeCreditEntity;
import com.edu.uni.augsburg.uniatron.model.Emotions;
import com.edu.uni.augsburg.uniatron.model.TimeCredits;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

public final class DatabaseUtil {
    private static final String[] FAVORIT_APPS = {
            "Neftlix", "YouTube", "Spotify", "Soundcloud", "Instagram", "Facebook", "Twitter",
            "Pinterest", "Skype", "Messenger", "WhatsApp", "Snapchat", "Tinder", "Kindle"
    };
    private static final int MOCK_DATA_ITEM_COUNT = 5000;

    private DatabaseUtil() {
    }

    public static void createRandomData(@NonNull final AppDatabase appDatabase) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                final Random random = new Random(42);

                Log.i("Database", "### Starting Database mockup data creation! ###");

                long start = System.currentTimeMillis();
                IntStream.range(0, MOCK_DATA_ITEM_COUNT)
                        .mapToObj(index -> getAppUsage(random, index))
                        .forEach(item -> appDatabase.appUsageDao().add(item));
                Log.i("Database", "### Added " + MOCK_DATA_ITEM_COUNT + " app usage data in " + (System.currentTimeMillis() - start) + "ms");

                start = System.currentTimeMillis();
                IntStream.range(0, MOCK_DATA_ITEM_COUNT)
                        .mapToObj(index -> getStepCount(random, index))
                        .forEach(item -> appDatabase.stepCountDao().add(item));
                Log.i("Database", "### Added " + MOCK_DATA_ITEM_COUNT + " step count data in " + (System.currentTimeMillis() - start) + "ms");

                start = System.currentTimeMillis();
                IntStream.range(0, MOCK_DATA_ITEM_COUNT)
                        .mapToObj(index -> getTimeCredit(random, index))
                        .forEach(item -> appDatabase.timeCreditDao().add(item));
                Log.i("Database", "### Added " + MOCK_DATA_ITEM_COUNT + " time credit data in " + (System.currentTimeMillis() - start) + "ms");

                start = System.currentTimeMillis();
                IntStream.range(0, MOCK_DATA_ITEM_COUNT)
                        .mapToObj(index -> getEmotion(random, index))
                        .forEach(item -> appDatabase.emotionDao().add(item));
                Log.i("Database", "### Added " + MOCK_DATA_ITEM_COUNT + " emotion data in " + (System.currentTimeMillis() - start) + "ms");

                Log.i("Database", "### Database mockup data finished! ###");
                return null;
            }
        }.execute();
    }

    private static AppUsageEntity getAppUsage(Random random, int index) {
        final AppUsageEntity appUsageEntity = new AppUsageEntity();
        appUsageEntity.setTimestamp(getRandomDate(index));
        appUsageEntity.setTime(random.nextInt(100));
        appUsageEntity.setAppName(FAVORIT_APPS[random.nextInt(FAVORIT_APPS.length)]);
        return appUsageEntity;
    }

    private static StepCountEntity getStepCount(Random random, int index) {
        final StepCountEntity stepCountEntity = new StepCountEntity();
        stepCountEntity.setTimestamp(getRandomDate(index));
        stepCountEntity.setStepCount(random.nextInt(1000));
        return stepCountEntity;
    }

    private static TimeCreditEntity getTimeCredit(Random random, int index) {
        final TimeCreditEntity timeCreditEntity = new TimeCreditEntity();

        final TimeCredits timeCredits = TimeCredits.CREDIT_100;

        timeCreditEntity.setTime(timeCredits.getTime());
        timeCreditEntity.setTimestamp(getRandomDate(index));
        timeCreditEntity.setStepCount(timeCredits.getStepCount());

        return timeCreditEntity;
    }

    private static EmotionEntity getEmotion(Random random, int index) {
        final EmotionEntity emotionEntity = new EmotionEntity();
        emotionEntity.setTimestamp(getRandomDate(index));
        emotionEntity.setValue(Emotions.values()[random.nextInt(Emotions.values().length)]);
        return emotionEntity;
    }

    private static Date getRandomDate(int index) {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.add(Calendar.DATE, -index);

        return calendar.getTime();
    }
}
