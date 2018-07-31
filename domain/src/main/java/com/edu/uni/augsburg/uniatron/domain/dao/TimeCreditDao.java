package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.domain.QueryProvider;
import com.edu.uni.augsburg.uniatron.domain.dao.model.LearningAid;
import com.edu.uni.augsburg.uniatron.domain.dao.model.TimeCredit;
import com.edu.uni.augsburg.uniatron.domain.query.TimeCreditQuery;

/**
 * The dao to operate on the time credit table.
 *
 * @author Fabio Hellmann
 */
public interface TimeCreditDao {

    /**
     * Add a new time credit.
     *
     * @param timeCredit The time credit will be generated out of this.
     * @param factor     The factor to multiply with.
     * @return The time credit.
     */
    LiveData<TimeCredit> addTimeCredit(@NonNull TimeCredit timeCredit, double factor);

    /**
     * Check whether the learning aid is active or not.
     *
     * @return The difference in time to the latest learning aid.
     * @see TimeCreditQuery#getLatestLearningAid()
     */
    LiveData<LearningAid> getLatestLearningAid();

    /**
     * Creates a new instance to access the time credit data.
     *
     * @param queryProvider The query provider.
     * @return The time credit dao.
     */
    static TimeCreditDao create(@NonNull final QueryProvider queryProvider) {
        return new TimeCreditDaoImpl(queryProvider.timeCreditQuery());
    }
}
