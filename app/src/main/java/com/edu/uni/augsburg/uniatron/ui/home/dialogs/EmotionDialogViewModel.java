package com.edu.uni.augsburg.uniatron.ui.home.dialogs;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.model.Emotion;
import com.edu.uni.augsburg.uniatron.model.Emotions;

/**
 * The model is the connection between the {@link DataRepository}
 * and the {@link EmotionDialogFragment}.
 *
 * @author Fabio Hellmann
 */
public class EmotionDialogViewModel extends AndroidViewModel {
    private final DataRepository mRepository;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public EmotionDialogViewModel(@NonNull final Application application) {
        super(application);

        mRepository = MainApplication.getRepository(application);
    }

    /**
     * Add the current users emotion to the database.
     *
     * @param emotion The emotion.
     * @return The added emotion.
     */
    @NonNull
    public LiveData<Emotion> addEmotion(@NonNull final Emotions emotion) {
        return mRepository.addEmotion(emotion);
    }
}
