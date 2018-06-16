package com.edu.uni.augsburg.uniatron.domain.util;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

/**
 * This is a helper class to create an {@link AsyncTask} with lambda expressions.
 *
 * @author Fabio Hellmann
 * @param <T> The type of the return value.
 */
public class AsyncTaskWrapper<T> extends AsyncTask<Void, Void, T> {
    private final DoInBackground<T> mBackgroundWorker;
    private final OnPostExecute<T> mForegroundWorker;

    /**
     * Ctr.
     *
     * @param backgroundWorker The statement for the background-thread.
     * @param foregroundWorker The statment for the ui-thread.
     */
    public AsyncTaskWrapper(@NonNull final DoInBackground<T> backgroundWorker,
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
     * A helper interface for lambda usage.
     *
     * @author Fabio Hellmann
     * @param <T> The type of the return value.
     */
    public interface DoInBackground<T> {
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
    public interface OnPostExecute<T> {
        /**
         * Will be executed in the ui-thread.
         *
         * @param result The result of the background statement.
         */
        void onPostExecute(T result);
    }
}
