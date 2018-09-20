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
                data -> new LearningAidImpl(data == null
                        ? 0
                        : TimeCredits.CREDIT_LEARNING.getBlockedTime() - (System.currentTimeMillis() - data.getTime())));
    }

    private static final class LearningAidImpl implements LearningAid {
        private final long mTimeLeft;
        private final boolean mActive;

        LearningAidImpl(final long timeLeft) {
            mActive = timeLeft > 0;
            mTimeLeft = timeLeft;
        }

        @Override
        public boolean isActive() {
            return getLeftTime().isPresent();
        }

        @Override
        public Optional<Long> getLeftTime() {
            return mActive ? Optional.of(mTimeLeft) : Optional.empty();
        }
    }
}
