package com.edu.uni.augsburg.uniatron.ui.shop;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;
import com.edu.uni.augsburg.uniatron.model.TimeCredits;
import com.lid.lib.LabelTextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The adapter for the time credit list of the shop activity.
 *
 * @author Fabio Hellmann
 */
public class TimeCreditShopListAdapter extends
        RecyclerView.Adapter<TimeCreditShopListAdapter.ViewHolder> {
    private static final int VIEW_TYPE_LEARNING_AID = 1;
    private final Context mContext;
    private final TimeCreditShopViewModel mModel;
    private final SharedPreferencesHandler mPrefHandler;
    private int mStepCount;

    TimeCreditShopListAdapter(@NonNull final Context context,
                              @NonNull final TimeCreditShopViewModel model) {
        super();
        mContext = context;
        mModel = model;
        mPrefHandler = new SharedPreferencesHandler(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                         final int viewType) {
        return new ViewHolder(getViewByType(parent, viewType));
    }

    private View getViewByType(@NonNull final ViewGroup parent, final int viewType) {
        if (viewType == VIEW_TYPE_LEARNING_AID) {
            return LayoutInflater.from(mContext)
                    .inflate(R.layout.card_time_credit_shop_learning_aid, parent, false);
        } else {
            return LayoutInflater.from(mContext)
                    .inflate(R.layout.card_time_credit_shop_credit, parent, false);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder,
                                 final int position) {
        final TimeCredits timeCredits = getTimeCreditList().get(position);
        holder.mTextViewTradeOffer.setText(mContext.getString(
                R.string.minutes_short,
                TimeUnit.MINUTES.convert(timeCredits.getTimeBonus(), TimeUnit.MILLISECONDS)
        ));
        holder.mTextViewTradeOffer.setOnClickListener(view -> {
            mModel.addToShoppingCart(timeCredits);
            final int color = mContext.getResources().getColor(R.color.secondaryLightColor);
            holder.mTextViewTradeOffer.setBackgroundColor(color);
        });
        mModel.addShopChangedListener(timeCredits, () -> {
            final int color = mContext.getResources().getColor(android.R.color.transparent);
            holder.mTextViewTradeOffer.setBackgroundColor(color);
        });

        if (timeCredits == TimeCredits.CREDIT_LEARNING) {
            holder.mTextViewTradeOffer.setLabelText(mContext.getString(
                    R.string.minutes_short,
                    TimeUnit.MINUTES.convert(timeCredits.getBlockedTime(), TimeUnit.MILLISECONDS)
            ));
        } else {
            final int coins = (int) (mPrefHandler.getStepsFactor() * timeCredits.getStepCount());
            holder.mTextViewTradeOffer.setLabelText(mContext.getString(R.string.coin_offer, coins));
        }
    }

    private List<TimeCredits> getTimeCreditList() {
        return Stream.of(TimeCredits.values())
                .sortBy(TimeCredits::getStepCount)
                .collect(Collectors.toList());
    }

    @Override
    public int getItemViewType(final int position) {
        return position == 0 ? VIEW_TYPE_LEARNING_AID : 0;
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
        notifyDataSetChanged();
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textViewTradeOffer)
        LabelTextView mTextViewTradeOffer;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
