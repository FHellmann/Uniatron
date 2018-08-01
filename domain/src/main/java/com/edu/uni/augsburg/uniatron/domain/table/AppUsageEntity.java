package com.edu.uni.augsburg.uniatron.domain.table;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.edu.uni.augsburg.uniatron.domain.converter.DateConverterUtil;
import com.edu.uni.augsburg.uniatron.domain.dao.model.AppUsage;

import java.util.Date;

/**
 * The model for the app usage.
 *
 * @author Fabio Hellmann
 */
@Entity(indices = {@Index("app_name")})
@TypeConverters({DateConverterUtil.class})
public class AppUsageEntity implements AppUsage {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;
    @ColumnInfo(name = "app_name")
    private String mPackageName;
    @ColumnInfo(name = "timestamp")
    private Date mTimestamp;
    @ColumnInfo(name = "usage_time")
    private long mUsageTime;
    @Ignore
    private double mUsageTimeAllPercent;

    public long getId() {
        return mId;
    }

    public void setId(final long identifier) {
        this.mId = identifier;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(final String packageName) {
        this.mPackageName = packageName;
    }

    public Date getTimestamp() {
        return (Date) mTimestamp.clone();
    }

    public void setTimestamp(final Date timestamp) {
        this.mTimestamp = (Date) timestamp.clone();
    }

    public long getUsageTime() {
        return mUsageTime;
    }

    public void setUsageTime(final long usageTime) {
        this.mUsageTime = usageTime;
    }

    public double getUsageTimeAllPercent() {
        return mUsageTimeAllPercent;
    }

    public void setUsageTimeAllPercent(final double usageTimeAllPercent) {
        mUsageTimeAllPercent = usageTimeAllPercent;
    }
}
