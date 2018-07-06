package com.edu.uni.augsburg.uniatron.ui;

import java.util.Date;

/**
 * The card view model is implemented from all view models which
 * belong to a card.
 *
 * @author Fabio Hellmann
 */
public interface CardViewModel {
    /**
     * Setup the initial state of the card.
     *
     * @param date The date.
     * @param calendarType The calendar type (date/month/year).
     */
    void setup(Date date, int calendarType);
}
