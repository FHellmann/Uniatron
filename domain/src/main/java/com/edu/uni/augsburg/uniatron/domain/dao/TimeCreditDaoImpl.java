package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.annimon.stream.Optional;
import com.edu.uni.augsburg.uniatron.domain.dao.model.LearningAid;
import com.edu.uni.augsburg.uniatron.domain.dao.model.TimeCredit;
import com.edu.uni.augsburg.uniatron.domain.dao.model.TimeCredits;
import com.edu.uni.augsburg.uniatron.domain.query.TimeCreditQuery;
import com.edu.uni.augsburg.uniatron.domain.table.TimeCreditEntity;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * The dao implementation for {@link TimeCreditDao}.
 *
 * @author Fabio Hellmann
 */
class TimeCreditDaoImpl implements TimeCreditDao {

    @NonNull
    private final TimeCreditQuery mTimeCreditQuery;

    /**
     * Ctr.
     *
     * @param timeCreditQuery The query to access the time credit table.
     */
    TimeCreditDaoImpl(@NonNull final TimeCreditQuery timeCreditQuery) {
        mTimeCreditQuery = timeCreditQuery;
    }

    public LiveData<TimeCredit> addTimeCredit(@NonNull final TimeCredit timeCredit, final double factor) {
        return LiveDataAsyncTask.execute(() -> {
            final TimeCreditEntity timeCreditEntity = new TimeCreditEntity();
            timeCreditEntity.setTimeBonus(timeCredit.getTimeBonus());
            timeCreditEntity.setStepCount((int) (timeCredit.getStepCount() * factor));
            timeCreditEntity.setTimestamp(new Date());
            timeCreditEntity.setType(timeCredit.getType());
            mTimeCreditQuery.add(timeCreditEntity);
            return timeCreditEntity;
        });
    }

    public LiveData<LearningAid> getLatestLearningAid() {
        return Transformations.map(
                mTimeCreditQuery.getLatestLearningAid(),
                data -> {
                    final long timePassed = data == null
                            ? 0 : System.currentTimeMillis() - data.getTime();
                    final long timeLeft = TimeCredits.CREDIT_LEARNING.getBlockedTime()
                            - TimeUnit.MINUTES.convert(timePassed, TimeUnit.MILLISECONDS);
                    if (timeLeft > 0
                            && timeLeft <= TimeCredits.CREDIT_LEARNING.getBlockedTime()) {
                        return new LearningAidImpl(timePassed > 0, timeLeft);
                    }
                    return new LearningAidImpl(false, 0);
                });
    }

    private static final class LearningAidImpl implements LearningAid {
        private final boolean mActive;
        private final long mTimeLeft;

        LearningAidImpl(final boolean active, final long timeLeft) {
            mActive = active;
            mTimeLeft = timeLeft;
        }

        @Override
        public Optional<Long> getLeftTime() {
            return mActive ? Optional.of(mTimeLeft) : Optional.empty();
        }
    }
}
