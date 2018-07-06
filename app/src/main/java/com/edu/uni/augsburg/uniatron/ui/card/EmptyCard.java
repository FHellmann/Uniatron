package com.edu.uni.augsburg.uniatron.ui.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.ui.CardView;

public class EmptyCard implements CardView {
    @Override
    public void update(CardView cardView) {
        // Ignore
    }

    @Override
    public void onBindView(final Context context, final RecyclerView.ViewHolder viewHolder) {

    }

    @Override
    public int getType() {
        return Integer.MAX_VALUE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final Context context,
                                                      final ViewGroup viewGroup) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.card_empty, viewGroup, false));
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
