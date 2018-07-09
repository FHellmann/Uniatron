package com.edu.uni.augsburg.uniatron.domain.util;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.annimon.stream.function.Supplier;

/**
 * This is a helper class to create an {@link AsyncTask} with lambda expressions.
 *
 * @author Fabio Hellmann
 * @param <T> The type of the return value.
 */
public final class LiveDataAsyncTask<T> extends AsyncTask<Void, Void, T> {
    private final DoInBackground<T> mBackgroundWorker;
    private final OnPostExecute<T> mForegroundWorker;

    /**
     * Ctr.
     *
     * @param backgroundWorker The statement for the background-thread.
     * @param foregroundWorker The statment for the ui-thread.
     */
    private LiveDataAsyncTask(@NonNull final DoInBackground<T> backgroundWorker,
                              @NonNull final OnPostExecute<T> foregroundWorker) {
        super();
        this.mBackgroundWorker = backgroundWorker;
        this.mForegroundWorker = foregroundWorker;
    }

    @Override
    protected T doInBackground(final Void... voids) {
        return mBackgroundWorker.doInBackground();
    }

    @Override
    protected void onPostExecute(final T result) {
        mForegroundWorker.onPostExecute(result);
    }

    /**
     * Executes an async task.
     *
     * @param supplier The data which should be processed in the background thread.
     * @param <T> The type of the data.
     * @return The live data which will be notified when the background thread is finished.
     */
    public static <T> LiveData<T> execute(@NonNull final Supplier<T> supplier) {
        final MutableLiveData<T> observable = new MutableLiveData<>();
        new LiveDataAsyncTask<>(supplier::get, observable::setValue).execute();
        return observable;
    }

    /**
     * A helper interface for lambda usage.
     *
     * @author Fabio Hellmann
     * @param <T> The type of the return value.
     */
    private interface DoInBackground<T> {
        /**
         * Will be executed in a background-thread.
         *
         * @return The result.
         */
        T doInBackground();
    }

    /**
     * A helper interface for lambda usage.
     *
     * @author Fabio Hellmann
     * @param <T> The type of the parameter.
     */
    private interface OnPostExecute<T> {
        /**
         * Will be executed in the ui-thread.
         *
         * @param result The result of the background statement.
         */
        void onPostExecute(T result);
    }
}
