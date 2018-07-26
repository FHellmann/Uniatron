package com.edu.uni.augsburg.uniatron.ui.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edu.uni.augsburg.uniatron.R;

/**
 * An empty card.
 *
 * @author Fabio Hellmann
 */
public class EmptyCard implements CardViewHolder {
    @Override
    public void update(@NonNull final CardViewHolder cardViewHolder) {
        // Empty
    }

    @Override
    public void onBindView(@NonNull final Context context,
                           @NonNull final RecyclerView.ViewHolder viewHolder) {
        // Empty
    }

    @Override
    public int getType() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final Context context,
                                                      @NonNull final ViewGroup viewGroup) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.card_empty, viewGroup, false));
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(@NonNull final View itemView) {
            super(itemView);
        }
    }
}
