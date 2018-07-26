package com.edu.uni.augsburg.uniatron.ui.card.coinbag;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.ui.card.CardPriority;
import com.edu.uni.augsburg.uniatron.ui.card.CardViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The card which displays the remaining coins.
 *
 * @author Fabio Hellmann
 */
public class CoinBagCard implements CardViewHolder {

    private int mCoins;
    private boolean mVisible;

    /**
     * Set the coins.
     *
     * @param coins The coins to set.
     */
    public void setCoins(final int coins) {
        mCoins = coins;
    }

    /**
     * Set the visibility.
     *
     * @param visible The visibility.
     */
    public void setVisible(final boolean visible) {
        this.mVisible = visible;
    }

    @Override
    public void update(@NonNull final CardViewHolder cardViewHolder) {
        final CoinBagCard holder = (CoinBagCard) cardViewHolder;
        setCoins(holder.mCoins);
        setVisible(holder.mVisible);
    }

    @Override
    public void onBindView(@NonNull final Context context,
                           @NonNull final RecyclerView.ViewHolder viewHolder) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        holder.mTextCoins.setText(String.valueOf(mCoins));
    }

    @Override
    public boolean isVisible() {
        return mVisible;
    }

    @Override
    public CardPriority getPriority() {
        return CardPriority.VERY_HIGH;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final Context context,
                                                      @NonNull final ViewGroup viewGroup) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_coins, viewGroup, false));
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textCoins)
        TextView mTextCoins;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
