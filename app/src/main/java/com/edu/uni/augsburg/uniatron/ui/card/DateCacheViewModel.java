package com.edu.uni.augsburg.uniatron.ui.card;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.ui.CardViewModel;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class DateCacheViewModel extends AndroidViewModel implements CardViewModel {
    private final Map<MediatorLiveData, LiveData> mCache = new HashMap<>();

    DateCacheViewModel(@NonNull Application application) {
        super(application);
    }

    public void setup(@NonNull final Date date, final int calendarType) {
        for (Map.Entry<MediatorLiveData, LiveData> entry : mCache.entrySet()) {
            entry.getKey().removeSource(entry.getValue());
        }
        mCache.clear();
    }

    void register(@NonNull final MediatorLiveData liveData,
                  @NonNull final LiveData source) {
        mCache.put(liveData, source);
    }
}
