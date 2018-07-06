package com.edu.uni.augsburg.uniatron.ui.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.edu.uni.augsburg.uniatron.ui.CardView;

/**
 * The card which displays the remaining coins.
 *
 * @author Fabio Hellmann
 */
public class CoinBagCard implements CardView {
    @Override
    public void update(@NonNull final CardView cardView) {
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
}
