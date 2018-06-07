package com.edu.uni.augsburg.uniatron.ui.home.shop;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;
import com.edu.uni.augsburg.uniatron.model.Emotions;
import com.edu.uni.augsburg.uniatron.model.TimeCredits;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * The time credit shop to select the time credit and emotion.
 *
 * @author Fabio Hellmann
 */
public class TimeCreditShopActivity extends AppCompatActivity {
    private static final int ANIMATION_DURATION = 500;

    @BindView(R.id.recyclerViewCreditlist)
    RecyclerView mRecyclerViewCreditList;
    @BindView(R.id.radioGroupEmotion)
    RadioGroup mRadioGroupEmotion;
    @BindView(R.id.buttonTrade)
    Button mButtonTrade;

    private TimeCreditShopViewModel mModel;
    private SharedPreferencesHandler mPrefHandler;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_credit_shop);

        ButterKnife.bind(this);

        // TODO-fh replace with MainApplication.getSharedPreferences
        mPrefHandler = new SharedPreferencesHandler(this);

        mModel = ViewModelProviders.of(this)
                .get(TimeCreditShopViewModel.class);

        final LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setOrientation(LinearLayoutManager.HORIZONTAL);

        mRecyclerViewCreditList.setHasFixedSize(true);
        mRecyclerViewCreditList.setLayoutFrozen(true);
        mRecyclerViewCreditList.setLayoutManager(layout);
        mRecyclerViewCreditList.setItemAnimator(new SlideInUpAnimator());
        mRecyclerViewCreditList.getItemAnimator().setAddDuration(ANIMATION_DURATION);
        mRecyclerViewCreditList.getItemAnimator().setChangeDuration(ANIMATION_DURATION);
        mRecyclerViewCreditList.getItemAnimator().setMoveDuration(ANIMATION_DURATION);
        mRecyclerViewCreditList.getItemAnimator().setRemoveDuration(ANIMATION_DURATION);

        final TimeCreditListAdapter adapter = new TimeCreditListAdapter();
        mModel.getRemainingStepCountToday().observe(this, stepCount -> {
            adapter.setStepCount(stepCount);
            adapter.notifyDataSetChanged();
        });
        mRecyclerViewCreditList.setAdapter(adapter);
    }

    /**
     * Click on the trade button is detected.
     */
    @OnClick(R.id.buttonTrade)
    public void onButtonTradeClicked() {
        // Get selected emotion
        final int checkedRadioButtonId = mRadioGroupEmotion.getCheckedRadioButtonId();
        final View checkedRadioButton = mRadioGroupEmotion.findViewById(checkedRadioButtonId);
        final int checkedIndex = mRadioGroupEmotion.indexOfChild(checkedRadioButton);

        // Buy the time credit
        mModel.buy(Emotions.values()[checkedIndex]);
        finish();
    }

    /**
     * Click on the cacnel button is detected.
     */
    @OnClick(R.id.buttonCancel)
    public void onButtonCancelClicked() {
        finish();
    }

    final class TimeCreditListAdapter extends
            RecyclerView.Adapter<TimeCreditListAdapter.ViewHolder> {
        private int mStepCount;

        @NonNull
        @Override
        public TimeCreditListAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                                   final int viewType) {
            final View view = LayoutInflater.from(getBaseContext())
                    .inflate(R.layout.activity_time_credit_shop_card_credit, parent, false);
            return new TimeCreditListAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final TimeCreditListAdapter.ViewHolder holder,
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

            mButtonTrade.setEnabled(mModel.isShoppingCartNotEmpty());
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
