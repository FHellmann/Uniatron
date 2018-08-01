package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Supplier;

/**
 * This is a helper class to create an {@link AsyncTask} with lambda expressions.
 *
 * @param <T> The type of the return value.
 * @author Fabio Hellmann
 */
final class LiveDataAsyncTask<T> extends AsyncTask<Void, Void, T> {
    private final Supplier<T> mBackgroundWorker;
    private final Consumer<T> mForegroundWorker;

    /**
     * Ctr.
     *
     * @param backgroundWorker The statement for the background-thread.
     * @param foregroundWorker The statment for the ui-thread.
     */
    private LiveDataAsyncTask(@NonNull final Supplier<T> backgroundWorker,
                              @NonNull final Consumer<T> foregroundWorker) {
        super();
        this.mBackgroundWorker = backgroundWorker;
        this.mForegroundWorker = foregroundWorker;
    }

    @Override
    protected T doInBackground(final Void... voids) {
        return mBackgroundWorker.get();
    }

    @Override
    protected void onPostExecute(final T result) {
        mForegroundWorker.accept(result);
    }

    /**
     * Executes an async task.
     *
     * @param supplier The data which should be processed in the background thread.
     * @param <T>      The type of the data.
     * @return The live data which will be notified when the background
     * thread is finished.
     */
    static <T> LiveData<T> execute(@NonNull final Supplier<T> supplier) {
        final MutableLiveData<T> observable = new MutableLiveData<>();
        new LiveDataAsyncTask<>(supplier, observable::setValue).execute();
        return observable;
    }
}
