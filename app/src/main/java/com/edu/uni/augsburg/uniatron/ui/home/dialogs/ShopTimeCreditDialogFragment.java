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
import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;
import com.edu.uni.augsburg.uniatron.model.TimeCredits;

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

    private SharedPreferencesHandler mPrefHandler;
    private TimeCreditListAdapter mAdapter;
    private OnBuyButtonClickedListener mListener;
    private ShopTimeCreditViewModel mModel;

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

        mPrefHandler = new SharedPreferencesHandler(getContext());

        mModel = ViewModelProviders.of(this)
                .get(ShopTimeCreditViewModel.class);

        setupRecyclerView();

        mAdapter = new TimeCreditListAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mModel.getRemainingStepCountToday().observe(this, stepCount -> {
            mAdapter.setStepCount(stepCount);
            mAdapter.notifyDataSetChanged();
            if (mAdapter.getItemCount() > 0) {
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
        mModel.buy();
        dismiss();
        if (mListener != null) {
            mListener.onClicked();
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
        private int mStepCount;

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                             final int viewType) {
            final View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.dialog_shop_time_credit_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder,
                                     final int position) {
            final TimeCredits timeCredits = Stream.of(TimeCredits.values())
                    .sortBy(TimeCredits::getStepCount)
                    .collect(Collectors.toList())
                    .get(position);

            if (mModel.isInShoppingCart(timeCredits)) {
                final int color = getResources().getColor(R.color.secondaryLightColor);
                holder.mTextViewTradeOffer.setBackgroundColor(color);
            } else {
                final int color = getResources().getColor(android.R.color.transparent);
                holder.mTextViewTradeOffer.setBackgroundColor(color);
            }
            holder.mValue = timeCredits;
            holder.mTextViewTradeOffer.setText(getString(
                    R.string.dialog_time_credit_item,
                    (int) (mPrefHandler.getStepsFactor() * timeCredits.getStepCount()),
                    timeCredits.getTimeInMinutes())
            );

            mTradeButton.setEnabled(mModel.isShoppingCartNotEmpty());
        }

        @Override
        public int getItemCount() {
            return (int) Stream.of(TimeCredits.values())
                    .filter(credit -> mPrefHandler.getStepsFactor()
                            * credit.getStepCount() <= mStepCount)
                    .count();
        }

        void setStepCount(final int stepCount) {
            this.mStepCount = stepCount;
        }

        /**
         * The view holder for the time credit item.
         *
         * @author Fabio Hellmann
         */
        public final class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.textViewTradeOffer)
            TextView mTextViewTradeOffer;
            private TimeCredits mValue;

            ViewHolder(final View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            @OnClick(R.id.textViewTradeOffer)
            public void onClick() {
                if (mModel.isInShoppingCart(mValue)) {
                    mModel.removeFromShoppingCart(mValue);
                } else {
                    mModel.addToShoppingCart(mValue);
                }
                notifyDataSetChanged();
            }
        }
    }
}
