package com.edu.uni.augsburg.uniatron.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * A custom view pager to disable the user swipe action.
 *
 * @author Fabio Hellmann
 */
public class NonSwipeableViewPager extends ViewPager {
    /**
     * Ctr.
     *
     * @param context The context.
     */
    public NonSwipeableViewPager(final Context context) {
        super(context);
    }

    /**
     * Ctr.
     *
     * @param context The context.
     * @param attrs   The attributes.
     */
    public NonSwipeableViewPager(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent event) {
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        return false;
    }
}
