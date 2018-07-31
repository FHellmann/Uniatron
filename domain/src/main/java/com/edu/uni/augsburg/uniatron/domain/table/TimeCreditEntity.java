package com.edu.uni.augsburg.uniatron.domain.table;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.edu.uni.augsburg.uniatron.domain.converter.DateConverterUtil;
import com.edu.uni.augsburg.uniatron.domain.dao.model.TimeCredit;
import com.edu.uni.augsburg.uniatron.domain.dao.model.TimeCreditType;

import java.util.Date;

/**
 * The model for the time credit.
 *
 * @author Fabio Hellmann
 */
@Entity
@TypeConverters({DateConverterUtil.class})
public class TimeCreditEntity implements TimeCredit {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;
    @ColumnInfo(name = "timestamp")
    private Date mTimestamp;
    @ColumnInfo(name = "time_bonus")
    private long mTimeBonus;
    @ColumnInfo(name = "steps")
    private int mStepCount;
    @ColumnInfo(name = "type")
    private TimeCreditType mType;

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

    public long getTimeBonus() {
        return mTimeBonus;
    }

    public void setTimeBonus(final long timeBonus) {
        this.mTimeBonus = timeBonus;
    }

    public int getStepCount() {
        return mStepCount;
    }

    public void setStepCount(final int stepCount) {
        this.mStepCount = stepCount;
    }

    public TimeCreditType getType() {
        return mType;
    }

    public void setType(final TimeCreditType type) {
        this.mType = type;
    }
}
