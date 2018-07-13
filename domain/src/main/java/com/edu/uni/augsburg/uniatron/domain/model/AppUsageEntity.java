package com.edu.uni.augsburg.uniatron.domain.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.edu.uni.augsburg.uniatron.domain.converter.DateConverterUtil;
import com.edu.uni.augsburg.uniatron.model.AppUsage;

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
    private String mAppName;
    @ColumnInfo(name = "timestamp")
    private Date mTimestamp;
    @ColumnInfo(name = "usage_time")
    private long mUsageTime;

    public long getId() {
        return mId;
    }

    public void setId(final long identifier) {
        this.mId = identifier;
    }

    public String getAppName() {
        return mAppName;
    }

    public void setAppName(final String appName) {
        this.mAppName = appName;
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
}
