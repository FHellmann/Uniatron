package com.edu.uni.augsburg.uniatron.ui.card;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.domain.util.DateUtil;
import com.edu.uni.augsburg.uniatron.ui.CardViewModel;

import java.util.Date;

/**
 * The model is the connection between the {@link DataRepository}
 * and the {@link CoinBagCard}.
 *
 * @author Fabio Hellmann
 */
public class CoinBagViewModel extends AndroidViewModel implements CardViewModel {
    private final MediatorLiveData<Integer> mRemainingCoins;
    private final DateCache<Integer> mDateCache;
    private final DataRepository mRepository;
    private boolean mIsVisible;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public CoinBagViewModel(@NonNull final Application application) {
        super(application);

        mRepository = MainApplication.getRepository(application);
        mDateCache = new DateCache<>();
        mRemainingCoins = new MediatorLiveData<>();
    }

    @Override
    public void setup(final Date date, final int calendarType) {
        mIsVisible = DateUtil.isSameDate(date, new Date());
        final LiveData<Integer> liveData = mRepository.getRemainingStepCountsToday();
        mDateCache.clearAndRegister(mRemainingCoins, liveData);
        mRemainingCoins.addSource(
                liveData,
                mRemainingCoins::setValue
        );
    }

    /**
     * Get the remaining step count for today.
     *
     * @return The remaining step count.
     */
    @NonNull
    public LiveData<CoinBagCard> getRemainingCoins() {
        return Transformations.map(mRemainingCoins,
                data -> {
                    if (data != null) {
                        final CoinBagCard coinBagCard = new CoinBagCard();
                        coinBagCard.setCoins(data);
                        coinBagCard.setVisible(mIsVisible);
                        return coinBagCard;
                    }
                    return null;
                });
    }
}
