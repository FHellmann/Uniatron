package com.edu.uni.augsburg.uniatron.ui.home.dialogs;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.model.TimeCredits;
import com.edu.uni.augsburg.uniatron.ui.home.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * The shop dialog is a chooser for a
 * {@link com.edu.uni.augsburg.uniatron.model.TimeCredit}.
 *
 * @author Fabio Hellmann
 */
public class ShopTimeCreditDialogFragment extends DialogFragment {
    /**
     * The name of this dialog.
     */
    public static final String NAME = ShopTimeCreditDialogFragment.class.getSimpleName();

    private static final int ANIMATION_DURATION = 500;

    @BindView(R.id.textViewError)
    TextView mTextViewError;
    @BindView(R.id.timeCreditRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.tradeButton)
    Button mTradeButton;

    private TimeCreditListAdapter mAdapter;
    private OnBuyButtonClickedListener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_shop_time_credit, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view,
                              @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();

        mAdapter = new TimeCreditListAdapter();
        mRecyclerView.setAdapter(mAdapter);

        final HomeViewModel model = ViewModelProviders.of(this).get(HomeViewModel.class);
        model.getRemainingStepCountToday().observe(this, stepCount -> {
            if (stepCount != null && TimeCredits.CREDIT_100.isUsable(stepCount)) {
                mAdapter.setStepCount(stepCount);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.setVisibility(View.VISIBLE);
                mTradeButton.setVisibility(View.VISIBLE);
                mTextViewError.setVisibility(View.GONE);
            } else {
                mRecyclerView.setVisibility(View.GONE);
                mTradeButton.setVisibility(View.GONE);
                mTextViewError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupRecyclerView() {
        final LinearLayoutManager layout = new LinearLayoutManager(getContext());
        layout.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutFrozen(true);
        mRecyclerView.setLayoutManager(layout);
        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), layout.getOrientation())
        );
        mRecyclerView.setItemAnimator(new SlideInUpAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(ANIMATION_DURATION);
        mRecyclerView.getItemAnimator().setChangeDuration(ANIMATION_DURATION);
        mRecyclerView.getItemAnimator().setMoveDuration(ANIMATION_DURATION);
        mRecyclerView.getItemAnimator().setRemoveDuration(ANIMATION_DURATION);
    }

    /**
     * The on click event of the cancel button is registered here.
     */
    @OnClick(R.id.cancelButton)
    public void onCancelButtonClicked() {
        dismiss();
    }

    /**
     * The on click event of the buy button is registered here.
     */
    @OnClick(R.id.tradeButton)
    public void onBuyButtonClicked() {
        if (getContext() != null) {
            final MainApplication context = (MainApplication) getContext().getApplicationContext();
            context.getRepository().addTimeCredit(mAdapter.getSelection());
            dismiss();
            if (mListener != null) {
                mListener.onClicked();
            }
        }
    }

    /**
     * Register a listener which will be called after the buy button was pressed.
     *
     * @param listener The listener.
     */
    public void setOnBuyButtonClickedListener(@NonNull final OnBuyButtonClickedListener listener) {
        this.mListener = listener;
    }

    /**
     * The buy button event listener interface.
     *
     * @author Fabio Hellmann
     */
    public interface OnBuyButtonClickedListener {
        /**
         * This method is called after the button was clicked.
         */
        void onClicked();
    }

    final class TimeCreditListAdapter extends
            RecyclerView.Adapter<TimeCreditListAdapter.ViewHolder> {
        private final List<ViewHolder> viewHolders = new ArrayList<>();
        private int mStepCount;
        private int mSelectionIndex = -1;

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                             final int viewType) {
            final View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.dialog_shop_time_credit_item, parent, false);
            final ViewHolder viewHolder = new ViewHolder(view);
            viewHolders.add(viewHolder);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder,
                                     final int position) {
            final TimeCredits timeCredits = Stream.of(TimeCredits.values())
                    .sortBy(TimeCredits::getStepCount)
                    .collect(Collectors.toList())
                    .get(position);

            holder.mTextViewTradeOffer.setText(getString(
                    R.string.dialog_time_credit_item,
                    timeCredits.getStepCount(),
                    timeCredits.getTimeInMinutes())
            );
        }

        @Override
        public int getItemCount() {
            return (int) Stream.of(TimeCredits.values())
                    .filter(credit -> credit.isUsable(mStepCount))
                    .count();
        }

        void setStepCount(final int stepCount) {
            this.mStepCount = stepCount;
        }

        @NonNull
        TimeCredits getSelection() {
            return Stream.of(TimeCredits.values())
                    .sortBy(TimeCredits::getStepCount)
                    .collect(Collectors.toList())
                    .get(mSelectionIndex);
        }

        /**
         * The view holder for the time credit item.
         *
         * @author Fabio Hellmann
         */
        public final class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.textViewTradeOffer)
            TextView mTextViewTradeOffer;
            private int mDefaultBackgroundColor;

            ViewHolder(final View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            @OnClick(R.id.textViewTradeOffer)
            public void onClick() {
                if (mSelectionIndex == getAdapterPosition()) {
                    mTextViewTradeOffer.setBackgroundColor(mDefaultBackgroundColor);
                    mSelectionIndex = -1;

                    mTradeButton.setEnabled(false);
                } else {
                    mDefaultBackgroundColor = mTextViewTradeOffer.getDrawingCacheBackgroundColor();

                    // reset all items to default color
                    Stream.of(viewHolders).forEach(view -> view.mTextViewTradeOffer
                            .setBackgroundColor(mDefaultBackgroundColor));

                    // highlight the new selected item
                    final int color = getResources().getColor(R.color.secondaryLightColor);
                    mTextViewTradeOffer.setBackgroundColor(color);
                    mSelectionIndex = getAdapterPosition();

                    mTradeButton.setEnabled(true);
                }
            }
        }
    }
}
