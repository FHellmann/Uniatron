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
import com.edu.uni.augsburg.uniatron.model.Emotions;
import com.edu.uni.augsburg.uniatron.model.LearningAid;
import com.edu.uni.augsburg.uniatron.model.TimeCredits;

import java.util.ArrayList;
import java.util.List;

/**
 * The model is the connection between the {@link DataRepository}
 * and the {@link TimeCreditShopActivity}.
 *
 * @author Fabio Hellmann
 */
class TimeCreditShopViewModel extends AndroidViewModel {
    private final SharedPreferencesHandler mPrefHandler;
    private final DataRepository mRepository;
    private final List<TimeCredits> mShoppingCart;
    private final List<Emotions> mEmotionCart;
    private final MediatorLiveData<Integer> mRemainingStepCount;
    private final MediatorLiveData<LearningAid> mLearningAid;
    private OnShopChangedListener mListener;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    TimeCreditShopViewModel(@NonNull final Application application) {
        super(application);

        mPrefHandler = MainApplication.getSharedPreferencesHandler(application);

        mRepository = MainApplication.getRepository(application);

        mShoppingCart = new ArrayList<>();
        mEmotionCart = new ArrayList<>();

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
        mShoppingCart.clear(); // We only want one entry
        mShoppingCart.add(timeCredits);
        if (mListener != null) {
            mListener.onChange(mShoppingCart.isEmpty() || mEmotionCart.isEmpty());
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
            mListener.onChange(mShoppingCart.isEmpty() || mEmotionCart.isEmpty());
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
     * Set the emotion of the user.
     *
     * @param emotion the user emotion.
     */
    public void setEmotion(@NonNull final Emotions emotion) {
        mEmotionCart.add(emotion);
        if (mListener != null) {
            mListener.onChange(mShoppingCart.isEmpty() || mEmotionCart.isEmpty());
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
            mRepository.addEmotion(mEmotionCart.get(0));
            clear();
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
