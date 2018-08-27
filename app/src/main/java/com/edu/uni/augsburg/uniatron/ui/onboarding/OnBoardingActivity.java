package com.edu.uni.augsburg.uniatron.ui.onboarding;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;

import com.edu.uni.augsburg.uniatron.R;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.SlideFragmentBuilder;

/**
 * Handles the creation of the App on-boarding.
 *
 * @author Leon Wöhrl
 */
public class OnBoardingActivity extends MaterialIntroActivity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        enableLastSlideAlphaExitTransition(true);

        addSlide(new SlideFragmentBuilder()
                .title(getString(R.string.app_name))
                .description(getString(R.string.onboarding_intro_description))
                .image(R.mipmap.ic_launcher_foreground)
                .backgroundColor(R.color.primaryColor)
                .buttonsColor(android.R.color.transparent)
                .build());

        addSlide(new PermissionFragmentBuilder()
                .title(getString(R.string.onboarding_app_usage_title))
                .description(getString(R.string.onboarding_app_usage_granted_description))
                .image(R.drawable.ic_onboarding_app_usage)
                .neededPermissions("android.permission.PACKAGE_USAGE_STATS")
                .backgroundColor(R.color.onboardingBackground1)
                .buttonsColor(R.color.onboardingBackground1Dark)
                .build());

        addSlide(new PermissionFragmentBuilder()
                .title(getString(R.string.onboarding_coinbag))
                .description(getString(R.string.onboarding_coinbag_granted_description))
                .image(R.drawable.ic_onboarding_coin)
                .neededPermissions("android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS")
                .backgroundColor(R.color.onboardingBackground2)
                .buttonsColor(R.color.onboardingBackground2Dark)
                .build());

        addSlide(new PermissionFragmentBuilder()
                .title(getString(R.string.onboarding_body_sensor))
                .description(getString(R.string.onboarding_body_sensor_description))
                .image(R.drawable.ic_directions_walk_black_24dp)
                .neededPermissions("android.permission.BODY_SENSORS")
                .backgroundColor(R.color.onboardingBackground3)
                .buttonsColor(R.color.onboardingBackground3Dark)
                .build());

        addSlide(new SlideFragmentBuilder()
                .title(getString(R.string.onboarding_shop_title))
                .description(getString(R.string.onboarding_shop_description))
                .image(R.drawable.ic_onboarding_shopping_cart)
                .backgroundColor(R.color.onboardingBackground4)
                .buttonsColor(R.color.onboardingBackground4Dark)
                .build());

        addSlide(new BlacklistSelectionFragment());

        // the user gets a sample entry on first on-boarding so it's not empty
        final OnBoardingViewModel mViewModel = ViewModelProviders.of(this)
                .get(OnBoardingViewModel.class);
        mViewModel.addIntroBonusIfAvailable(getPackageName());
    }
}
