package com.edu.uni.augsburg.uniatron.ui.card;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.domain.DataRepository;

/**
 * The model is the connection between the {@link DataRepository}
 * and the {@link CoinBagCard}.
 *
 * @author Fabio Hellmann
 */
class CoinBagViewModel extends AndroidViewModel {
    /**
     * Ctr.
     *
     * @param application The application.
     */
    CoinBagViewModel(@NonNull final Application application) {
        super(application);
    }
}
