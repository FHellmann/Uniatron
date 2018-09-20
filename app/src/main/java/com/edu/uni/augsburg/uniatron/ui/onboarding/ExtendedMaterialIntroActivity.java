package com.edu.uni.augsburg.uniatron.ui.onboarding;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.CustomViewPager;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.widgets.OverScrollViewPager;
import agency.tango.materialintroscreen.widgets.SwipeableViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The extended {@link MaterialIntroActivity} will refresh the view
 * after navigating back from external setting options.
 *
 * @author Fabio Hellmann
 */
public abstract class ExtendedMaterialIntroActivity extends MaterialIntroActivity {

    @BindView(agency.tango.materialintroscreen.R.id.view_pager_slides)
    OverScrollViewPager mOverScrollViewPager;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        final SwipeableViewPager overScrollView = mOverScrollViewPager.getOverScrollView();
        overScrollView.addOnPageChangeListener(new CustomViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
                // Ignore
            }

            @Override
            public void onPageSelected(final int position) {
                if (position == overScrollView.getAdapter().getCount()) {
                    onFinished();
                }
            }

            @Override
            public void onPageScrollStateChanged(final int state) {
                // Ignore
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        onRequestPermissionsResult(0, new String[]{}, new int[]{});
    }

    /**
     * Is called when the on boarding is finished.
     */
    protected abstract void onFinished();
}
