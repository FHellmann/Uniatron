package com.edu.uni.augsburg.uniatron.ui.onboarding;

import agency.tango.materialintroscreen.MaterialIntroActivity;

/**
 * The extended {@link MaterialIntroActivity} will refresh the view
 * after navigating back from external setting options.
 *
 * @author Fabio Hellmann
 */
public abstract class ExtendedMaterialIntroActivity extends MaterialIntroActivity {

    @Override
    protected void onResume() {
        super.onResume();
        onRequestPermissionsResult(0, new String[]{}, new int[]{});
    }
}
