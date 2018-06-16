package com.edu.uni.augsburg.uniatron.domain.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.edu.uni.augsburg.uniatron.domain.converter.DateConverterUtil;
import com.edu.uni.augsburg.uniatron.domain.converter.EmotionConverterUtil;
import com.edu.uni.augsburg.uniatron.model.Emotion;
import com.edu.uni.augsburg.uniatron.model.Emotions;

import java.util.Date;

/**
 * The model for the emotion.
 *
 * @author Fabio Hellmann
 */
@Entity
@TypeConverters({DateConverterUtil.class, EmotionConverterUtil.class})
public class EmotionEntity implements Emotion {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;
    @ColumnInfo(name = "timestamp")
    private Date mTimestamp;
    @ColumnInfo(name = "value")
    private Emotions mValue;

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

    public Emotions getValue() {
        return mValue;
    }

    public void setValue(final Emotions value) {
        this.mValue = value;
    }
}
