package com.edu.uni.augsburg.uniatron.ui.home.shop;

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
import com.edu.uni.augsburg.uniatron.model.Emotions;
import com.edu.uni.augsburg.uniatron.model.TimeCredits;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The model is the connection between the {@link DataRepository}
 * and the {@link TimeCreditShopActivity}.
 *
 * @author Fabio Hellmann
 */
public class TimeCreditShopViewModel extends AndroidViewModel {
    private final SharedPreferencesHandler mPrefHandler;
    private final DataRepository mRepository;
    private final List<TimeCredits> mShoppingCart;
    private final MediatorLiveData<Integer> mRemainingStepCount;
    private final MediatorLiveData<Long> mLearningAid;
    private OnShopChangedListener mListener;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public TimeCreditShopViewModel(@NonNull final Application application) {
        super(application);

        mPrefHandler = MainApplication.getSharedPreferencesHandler(application);

        mRepository = MainApplication.getRepository(application);

        mShoppingCart = new ArrayList<>();

        mRemainingStepCount = new MediatorLiveData<>();
        mRemainingStepCount.addSource(
                mRepository.getRemainingStepCountsToday(),
                mRemainingStepCount::setValue
        );

        mLearningAid = new MediatorLiveData<>();
        mLearningAid.addSource(
                mRepository.getLatestLearningAidDiff(),
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
        return Transformations.map(
                mLearningAid,
                timePassed -> {
                    final long timeLeft = TimeCredits.CREDIT_LEARNING.getBlockedMinutes()
                            - TimeUnit.MINUTES.convert(timePassed, TimeUnit.MILLISECONDS);
                    if (timeLeft > 0
                            && timeLeft <= TimeCredits.CREDIT_LEARNING.getBlockedMinutes()) {
                        return new LearningAid(timePassed > 0, timeLeft);
                    }
                    return new LearningAid(false, 0);
                });
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
        mShoppingCart.clear(); // We only want one entry
        mShoppingCart.add(timeCredits);
        if (mListener != null) {
            mListener.onChange(mShoppingCart.isEmpty());
        }
    }

    /**
     * Remove the time credit to the shopping cart.
     *
     * @param timeCredits The time credit to remove.
     */
    public void removeFromShoppingCart(@NonNull final TimeCredits timeCredits) {
        mShoppingCart.remove(timeCredits);
        if (mListener != null) {
            mListener.onChange(mShoppingCart.isEmpty());
        }
    }

    /**
     * Check whether the time credit is already in the shopping cart or not.
     *
     * @param timeCredits The time credit to check.
     * @return <code>true</code> if the time credit is in the shopping cart.
     */
    public boolean isInShoppingCart(@NonNull final TimeCredits timeCredits) {
        return mShoppingCart.contains(timeCredits);
    }

    /**
     * Stores the saved time credits of the shopping cart in the database.
     *
     * @param emotion The emotion.
     */
    public void buy(@NonNull final Emotions emotion) {
        if (!mShoppingCart.isEmpty()) {
            Stream.of(mShoppingCart).forEach(credit -> {
                final double stepsFactor = mPrefHandler.getStepsFactor();
                mRepository.addTimeCredit(credit, stepsFactor);
            });
            mRepository.addEmotion(emotion);
        }
    }

    /**
     * Set the listener.
     *
     * @param listener The listener.
     */
    public void setShopChangeListener(@NonNull final OnShopChangedListener listener) {
        mListener = listener;
    }

    /**
     * A listener which will be notified when the shop state changed.
     *
     * @author Fabio Hellmann
     */
    public interface OnShopChangedListener {
        /**
         * Is called when the shop state changed.
         *
         * @param empty If the shop is empty or not.
         */
        void onChange(boolean empty);
    }
}
