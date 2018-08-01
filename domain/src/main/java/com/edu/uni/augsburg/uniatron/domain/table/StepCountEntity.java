package com.edu.uni.augsburg.uniatron.domain.table;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.edu.uni.augsburg.uniatron.domain.converter.DateConverterUtil;
import com.edu.uni.augsburg.uniatron.domain.dao.model.StepCount;

import java.util.Date;

/**
 * The model for the step count.
 *
 * @author Fabio Hellmann
 */
@Entity
@TypeConverters({DateConverterUtil.class})
public class StepCountEntity implements StepCount {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;
    @ColumnInfo(name = "timestamp")
    private Date mTimestamp;
    @ColumnInfo(name = "step_count")
    private int mStepCount;

    public long getId() {
        return mId;
    }

    public void setId(final long identifier) {
        this.mId = identifier;
    }

    public Date getTimestamp() {
        return (Date) mTimestamp.clone();
    }

    public void setTimestamp(final Date timestamp) {
        this.mTimestamp = (Date) timestamp.clone();
    }

    public int getStepCount() {
        return mStepCount;
    }

    public void setStepCount(final int stepCount) {
        this.mStepCount = stepCount;
    }
}
