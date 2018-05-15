package com.edu.uni.augsburg.uniatron.domain;

import android.support.annotation.NonNull;

import com.annimon.stream.IntStream;
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
    private static final int MOCK_DATA_ITEM_COUNT = 500;

    private DatabaseUtil() {
    }

    public static void createRandomData(@NonNull final AppDatabase appDatabase) {
        final Random random = new Random();

        IntStream.range(0, MOCK_DATA_ITEM_COUNT)
                .mapToObj(index -> getAppUsage(random, index))
                .forEach(item -> appDatabase.appUsageDao().add(item));

        IntStream.range(0, MOCK_DATA_ITEM_COUNT)
                .mapToObj(index -> getStepCount(random, index))
                .forEach(item -> appDatabase.stepCountDao().add(item));

        IntStream.range(0, MOCK_DATA_ITEM_COUNT)
                .mapToObj(index -> getTimeCredit(random, index))
                .forEach(item -> appDatabase.timeCreditDao().add(item));

        IntStream.range(0, MOCK_DATA_ITEM_COUNT)
                .mapToObj(index -> getEmotion(random, index))
                .forEach(item -> appDatabase.emotionDao().add(item));
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

        timeCreditEntity.setTime(timeCredits.getTimeInMinutes());
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
        calendar.add(Calendar.DATE, -(index % 50));

        return calendar.getTime();
    }
}
