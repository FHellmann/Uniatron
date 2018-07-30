package com.edu.uni.augsburg.uniatron.ui.card.coinbag;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.domain.DataSource;
import com.edu.uni.augsburg.uniatron.domain.util.DateConverterImpl;
import com.edu.uni.augsburg.uniatron.ui.card.CardViewModel;
import com.edu.uni.augsburg.uniatron.ui.card.DateCache;

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
    private final DataSource mRepository;
    private boolean mIsVisible;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public CoinBagViewModel(@NonNull final Application application) {
        super(application);

        mRepository = MainApplication.getDataSource(application);
        mDateCache = new DateCache<>();
        mRemainingCoins = new MediatorLiveData<>();
    }

    @Override
    public void setup(final Date date, final int calendarType) {
        mIsVisible = DateConverterImpl.DATE_MIN_TIME.convert(date)
                .equals(DateConverterImpl.DATE_MIN_TIME.convert(new Date()));
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
