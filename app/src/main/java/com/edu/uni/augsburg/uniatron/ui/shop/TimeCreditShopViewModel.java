package com.edu.uni.augsburg.uniatron.ui.shop;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.domain.DataSource;
import com.edu.uni.augsburg.uniatron.model.Emotions;
import com.edu.uni.augsburg.uniatron.model.LearningAid;
import com.edu.uni.augsburg.uniatron.model.TimeCredits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The model is the connection between the {@link DataRepository}
 * and the {@link TimeCreditShopActivity}.
 *
 * @author Fabio Hellmann
 */
public class TimeCreditShopViewModel extends AndroidViewModel {
    private final SharedPreferencesHandler mPrefHandler;
    private final DataSource mRepository;
    private final List<TimeCredits> mShoppingCart;
    private final List<Emotions> mEmotionCart;
    private final MediatorLiveData<Integer> mRemainingStepCount;
    private final MediatorLiveData<LearningAid> mLearningAid;
    private final Map<TimeCredits, OnShopChangedListener> mChangeListener;
    private OnShopValidListener mListener;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public TimeCreditShopViewModel(@NonNull final Application application) {
        super(application);

        mPrefHandler = MainApplication.getSharedPreferencesHandler(application);

        mRepository = MainApplication.getDataSource(application);

        mShoppingCart = new ArrayList<>();
        mEmotionCart = new ArrayList<>();
        mChangeListener = new HashMap<>();

        mRemainingStepCount = new MediatorLiveData<>();
        mRemainingStepCount.addSource(
                mRepository.getRemainingStepCountsToday(),
                mRemainingStepCount::setValue
        );

        mLearningAid = new MediatorLiveData<>();
        mLearningAid.addSource(
                mRepository.getLatestLearningAid(),
                mLearningAid::setValue
        );
    }

    /**
     * Check whether the learning aid is active or not.
     *
     * @return {@code true} if the learning aid is active {@code false} otherwise.
     */
    @NonNull
    public LiveData<LearningAid> getLatestLearningAidTimePassed() {
        return mLearningAid;
    }

    /**
     * Get the remaining step count for today.
     *
     * @return The remaining step count.
     */
    @NonNull
    public LiveData<Integer> getRemainingStepCountToday() {
        return Transformations.map(mRemainingStepCount,
                data -> data != null && data > 0 ? data : 0);
    }

    /**
     * Add the time credit to the shopping cart.
     *
     * @param timeCredits The time credit to add.
     */
    public void addToShoppingCart(@NonNull final TimeCredits timeCredits) {
        Stream.of(mShoppingCart)
                .map(mChangeListener::get)
                .forEach(OnShopChangedListener::onChanged);
        mShoppingCart.clear(); // We only want one entry
        mShoppingCart.add(timeCredits);
        if (mListener != null) {
            mListener.isValid(!mShoppingCart.isEmpty() && !mEmotionCart.isEmpty());
        }
    }

    /**
     * Set the emotion of the user.
     *
     * @param emotion the user emotion.
     */
    public void setEmotion(@NonNull final Emotions emotion) {
        mEmotionCart.clear();
        mEmotionCart.add(emotion);
        if (mListener != null) {
            mListener.isValid(!mShoppingCart.isEmpty() && !mEmotionCart.isEmpty());
        }
    }

    /**
     * Clear the shopping cart.
     */
    public void clear() {
        mShoppingCart.clear();
        mEmotionCart.clear();
    }

    /**
     * Stores the saved time credits of the shopping cart in the database.
     */
    public void buy() {
        if (!mShoppingCart.isEmpty() && !mEmotionCart.isEmpty()) {
            Stream.of(mShoppingCart).forEach(credit -> {
                final double stepsFactor = mPrefHandler.getStepsFactor();
                mRepository.addTimeCredit(credit, stepsFactor);
            });
            Stream.of(mEmotionCart).forEach(mRepository::addEmotion);
            clear();
        }
    }

    /**
     * Set the listener.
     *
     * @param listener The listener.
     */
    public void setShopValidListener(@NonNull final OnShopValidListener listener) {
        mListener = listener;
    }

    /**
     * Add a listener for shop change events.
     *
     * @param timeCredits The time credit to listen on.
     * @param listener    The listener.
     */
    public void addShopChangedListener(@NonNull final TimeCredits timeCredits,
                                       @NonNull final OnShopChangedListener listener) {
        mChangeListener.put(timeCredits, listener);
    }

    /**
     * A listener which will be notified when the shop state changed.
     *
     * @author Fabio Hellmann
     */
    public interface OnShopValidListener {
        /**
         * Is called when the shop state changed.
         *
         * @param valid If the shop is valid.
         */
        void isValid(boolean valid);
    }

    /**
     * A listener which will be notified when the selection of the shop changed.
     *
     * @author Fabio Hellmann
     */
    public interface OnShopChangedListener {
        /**
         * Is called when the shop selection changed.
         */
        void onChanged();
    }
}
