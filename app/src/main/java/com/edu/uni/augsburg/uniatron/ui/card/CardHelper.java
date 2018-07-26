package com.edu.uni.augsburg.uniatron.ui.card;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * A helper class for cards.
 *
 * @author Fabio Hellmann
 */
public final class CardHelper {
    private CardHelper() {
        // Ignore
    }

    /**
     * Sets the card to the full span.
     *
     * @param viewHolder The view holder to set full span.
     */
    public static void setFullSpan(@NonNull final RecyclerView.ViewHolder viewHolder) {
        final StaggeredGridLayoutManager.LayoutParams layoutParams =
                (StaggeredGridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams();
        layoutParams.setFullSpan(true);
    }
}
