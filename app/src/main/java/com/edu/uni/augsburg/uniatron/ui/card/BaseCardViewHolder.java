package com.edu.uni.augsburg.uniatron.ui.card;

/**
 * Sets the default method values.
 *
 * @author Fabio Hellmann
 */
public abstract class BaseCardViewHolder implements CardViewHolder {

    @Override
    public int getType() {
        return getClass().getName().hashCode();
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public CardPriority getPriority() {
        return CardPriority.NORMAL;
    }
}
