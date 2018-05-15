package com.edu.uni.augsburg.uniatron.ui.home.dialogs;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.model.Emotions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * The shop dialog is a chooser for a
 * {@link com.edu.uni.augsburg.uniatron.model.Emotions}.
 *
 * @author Fabio Hellmann
 */
public class EmotionDialogFragment extends DialogFragment {
    /** The name of this dialog. */
    public static final String NAME = EmotionDialogFragment.class.getSimpleName();

    @BindView(R.id.radioGroupEmotion)
    RadioGroup mRadioGroupEmotion;
    @BindView(R.id.okButton)
    Button mOkButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_emotion, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view,
                              @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setAllowReturnTransitionOverlap(false);
        setAllowEnterTransitionOverlap(false);
        setCancelable(false);
    }

    /**
     * The on click event of the ok button is registered here.
     */
    @OnClick(R.id.okButton)
    public void onOkButtonClicked() {
        mOkButton.setEnabled(false);

        final EmotionDialogViewModel model = ViewModelProviders.of(this)
                .get(EmotionDialogViewModel.class);

        final int checkedRadioButtonId = mRadioGroupEmotion.getCheckedRadioButtonId();
        final View checkedRadioButton = mRadioGroupEmotion.findViewById(checkedRadioButtonId);
        final int checkedIndex = mRadioGroupEmotion.indexOfChild(checkedRadioButton);

        model.addEmotion(Emotions.values()[checkedIndex]).observe(this, data -> {
            dismiss();
        });
    }
}
