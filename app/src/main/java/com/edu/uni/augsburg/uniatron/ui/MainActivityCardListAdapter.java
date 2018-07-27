package com.edu.uni.augsburg.uniatron.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.annimon.stream.IntStream;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.ui.card.CardViewHolder;
import com.edu.uni.augsburg.uniatron.ui.card.EmptyCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The adapter for the card list.
 *
 * @author Fabio Hellmann
 */
public class MainActivityCardListAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<CardViewHolder> mCardViewHolderList = new ArrayList<>();
    private final Context mContext;

    /**
     * Ctr
     *
     * @param context The context.
     */
    MainActivityCardListAdapter(@NonNull final Context context) {
        super();
        mContext = context;
    }

    void addOrUpdateCard(@Nullable final CardViewHolder cardViewHolder) {
        if (cardViewHolder != null) {
            final Optional<CardViewHolder> cardOptional = getCardViewHolder(cardViewHolder.getType());
            if (cardOptional.isPresent()) {
                // Update the card if it does already exists
                cardOptional.get().update(cardViewHolder);
                notifyItemChanged(mCardViewHolderList.indexOf(cardOptional.get()));
            } else {
                // Add the not existing card
                mCardViewHolderList.add(cardViewHolder);
                Collections.sort(
                        mCardViewHolderList,
                        (card1, card2) -> card1.getPriority().compareTo(card2.getPriority())
                );
                notifyItemInserted(mCardViewHolderList.indexOf(cardViewHolder));
            }
            notifyDataSetChanged();
        }
    }

    void clear() {
        notifyItemRangeRemoved(0, mCardViewHolderList.size());
        mCardViewHolderList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup,
                                                      final int type) {
        return getCardViewHolder(type)
                .orElse(new EmptyCard())
                .onCreateViewHolder(mContext, viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder,
                                 final int position) {
        if (mCardViewHolderList.size() > position) {
            findFirstCardMatch(position).onBindView(mContext, viewHolder);
        }
    }

    @Override
    public int getItemViewType(final int position) {
        return mCardViewHolderList.size() == position ? Integer.MIN_VALUE : findFirstCardMatch(position).getType();
    }

    @Override
    public int getItemCount() {
        return (int) Stream.of(mCardViewHolderList).filter(CardViewHolder::isVisible).count() + 1;
    }

    private CardViewHolder findFirstCardMatch(final int position) {
        return IntStream.range(0, mCardViewHolderList.size())
                .filter(index -> index >= position)
                .mapToObj(mCardViewHolderList::get)
                .filter(CardViewHolder::isVisible)
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    private Optional<CardViewHolder> getCardViewHolder(final int type) {
        return Stream.of(mCardViewHolderList)
                .filter(card -> card.getType() == type)
                .findFirst();
    }
}
