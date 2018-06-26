package com.edu.uni.augsburg.uniatron.ui.home.dialogs;

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
import com.edu.uni.augsburg.uniatron.model.TimeCredits;

import java.util.ArrayList;
import java.util.List;

/**
 * The model is the connection between the {@link DataRepository}
 * and the {@link ShopTimeCreditDialogFragment}.
 *
 * @author Fabio Hellmann
 */
public class ShopTimeCreditViewModel extends AndroidViewModel {

    private final SharedPreferencesHandler mPrefHandler;
    private final DataRepository mRepository;
    private final List<TimeCredits> mShoppingCart;
    private final MediatorLiveData<Integer> mRemainingStepCount;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public ShopTimeCreditViewModel(@NonNull final Application application) {
        super(application);

        mPrefHandler = MainApplication.getSharedPreferencesHandler(application);

        mRepository = MainApplication.getRepository(application);

        mShoppingCart = new ArrayList<>();

        mRemainingStepCount = new MediatorLiveData<>();
        mRemainingStepCount.addSource(
                mRepository.getRemainingStepCountsToday(),
                mRemainingStepCount::setValue
        );
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
    }

    /**
     * Remove the time credit to the shopping cart.
     *
     * @param timeCredits The time credit to remove.
     */
    public void removeFromShoppingCart(@NonNull final TimeCredits timeCredits) {
        mShoppingCart.remove(timeCredits);
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
     * Check whether the shopping cart is empty or not.
     *
     * @return <code>true</code> if the shopping cart is not empty.
     */
    public boolean isShoppingCartNotEmpty() {
        return !mShoppingCart.isEmpty();
    }

    /**
     * Stores the saved time credits of the shopping cart in the database.
     */
    public void buy() {
        Stream.of(mShoppingCart).forEach(credit -> {
            final double stepsFactor = mPrefHandler.getStepsFactor();
            mRepository.addTimeCredit(credit, stepsFactor);
        });
    }
}
