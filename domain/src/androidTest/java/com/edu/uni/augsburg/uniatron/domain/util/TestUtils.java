package com.edu.uni.augsburg.uniatron.domain.util;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class TestUtils {
    private TestUtils() {
    }

    public static <T> T getLiveDataValue(@NonNull final LiveData<T> liveData) throws InterruptedException {
        final List<T> data = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(1);
        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(@Nullable T o) {
                data.add(o);
                latch.countDown();
                liveData.removeObserver(this);
            }
        };
        liveData.observeForever(observer);
        latch.await(2, TimeUnit.SECONDS);
        return data.get(0);
    }

    @NonNull
    public static Date getDate(int date, int month, int year) {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(year, month - 1, date);
        return calendar.getTime();
    }
}
