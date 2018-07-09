package com.edu.uni.augsburg.uniatron.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.ui.card.EmptyCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The adapter for the card list.
 *
 * @author Fabio Hellmann
 */
public class MainActivityCardListAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<CardViewHolder> mCardViewHolderList = new ArrayList<>();
    private final Map<Integer, CardViewHolder> mCardViewHolderMap = new LinkedHashMap<>();
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
        if (cardViewHolder == null || !cardViewHolder.isVisible()) {
            return;
        } else if (mCardViewHolderList.isEmpty()) {
            final EmptyCard card = new EmptyCard();
            mCardViewHolderMap.put(card.getType(), card);
            mCardViewHolderList.add(card);
        }
        // Update the card if it does already exists
        final Optional<CardViewHolder> cardOptional = Stream.of(mCardViewHolderList)
                .filter(card -> card.getType() == cardViewHolder.getType())
                .findFirst();
        if (cardOptional.isPresent()) {
            cardOptional.get().update(cardViewHolder);
            notifyItemChanged(mCardViewHolderList.indexOf(cardOptional.get()));
            notifyDataSetChanged();
        } else {
            mCardViewHolderList.add(cardViewHolder);
            mCardViewHolderMap.put(cardViewHolder.getType(), cardViewHolder);
            Collections.sort(
                    mCardViewHolderList,
                    (card1, card2) -> Integer.compare(card1.getType(), card2.getType())
            );
            notifyItemInserted(mCardViewHolderList.indexOf(cardViewHolder));
            notifyDataSetChanged();
        }
    }

    void clear() {
        final int size = mCardViewHolderList.size();
        mCardViewHolderList.clear();
        mCardViewHolderMap.clear();
        notifyItemRangeRemoved(0, size);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup,
                                                      final int type) {
        return mCardViewHolderMap.get(type).onCreateViewHolder(mContext, viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder,
                                 final int position) {
        mCardViewHolderList.get(position).onBindView(mContext, viewHolder);
    }

    @Override
    public int getItemViewType(final int position) {
        return mCardViewHolderList.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return mCardViewHolderList.size();
    }
}
