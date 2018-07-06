package com.edu.uni.augsburg.uniatron.ui.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.edu.uni.augsburg.uniatron.ui.CardViewHolder;

import butterknife.ButterKnife;

/**
 * The card which displays the remaining coins.
 *
 * @author Fabio Hellmann
 */
public class CoinBagCard implements CardViewHolder {
    @Override
    public void update(@NonNull final CardViewHolder cardViewHolder) {
        // Needs some content to do...
    }

    @Override
    public void onBindView(@NonNull final Context context,
                           @NonNull final RecyclerView.ViewHolder viewHolder) {
        // Needs some content to do...
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final Context context,
                                                      @NonNull final ViewGroup viewGroup) {
        return null;
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
