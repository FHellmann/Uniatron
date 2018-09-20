package com.edu.uni.augsburg.uniatron.ui.card;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * The card view is implemented by every card.
 *
 * @author Fabio Hellmann
 */
public interface CardViewHolder {
    /**
     * Updates the card data.
     *
     * @param cardViewHolder The card to update with.
     */
    void update(CardViewHolder cardViewHolder);

    /**
     * Binds the view to the data.
     *
     * @param context    The context.
     * @param viewHolder The view holder.
     */
    void onBindView(Context context, RecyclerView.ViewHolder viewHolder);

    /**
     * Creates a view holder.
     *
     * @param context   The context.
     * @param viewGroup The parent view group.
     * @return The view holder.
     */
    RecyclerView.ViewHolder onCreateViewHolder(Context context, ViewGroup viewGroup);

    /**
     * Get the card type.
     *
     * @return The type.
     */
    int getType();

    /**
     * Check if the card should be displayed.
     *
     * @return {@code true} if the card should be displayed, {@code false} otherwise.
     */
    boolean isVisible();

    /**
     * Get the priority.
     *
     * @return The priority.
     */
    CardPriority getPriority();
}
