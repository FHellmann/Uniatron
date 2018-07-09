package com.edu.uni.augsburg.uniatron.ui.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.ui.CardViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The card which displays the remaining coins.
 *
 * @author Fabio Hellmann
 */
public class TimeAccountCard implements CardViewHolder {

    private static final int TYPE = 1;
    private int mTimeLeft;
    private boolean mVisible;

    /**
     * Set the time left.
     *
     * @param timeLeft The time left.
     */
    public void setTimeLeft(final int timeLeft) {
        this.mTimeLeft = timeLeft;
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
        final TimeAccountCard holder = (TimeAccountCard) cardViewHolder;
        setTimeLeft(holder.mTimeLeft);
        setVisible(holder.mVisible);
    }

    @Override
    public void onBindView(@NonNull final Context context,
                           @NonNull final RecyclerView.ViewHolder viewHolder) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        holder.mTextTimeLeft.setText(context
                .getString(R.string.time_account_desc, mTimeLeft / 60, mTimeLeft % 60));
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public boolean isVisible() {
        return mVisible;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final Context context,
                                                      @NonNull final ViewGroup viewGroup) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.card_time_account, viewGroup, false));
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textTimeLeft)
        TextView mTextTimeLeft;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
