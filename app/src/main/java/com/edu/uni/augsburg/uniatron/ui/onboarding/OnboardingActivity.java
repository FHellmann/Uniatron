package com.edu.uni.augsburg.uniatron.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.ui.setting.SettingActivity;
import com.edu.uni.augsburg.uniatron.ui.util.PermissionUtil;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

/**
 * Handles the creation of the App onboarding.
 *
 * @author Leon Wöhrl
 */
public class OnboardingActivity extends IntroActivity {

    private static final int STEPBONUS = 500;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        setFullscreen(true);
        super.onCreate(savedInstanceState);

        addSlideIntro();
        addSlideAppUsage();
        addSlideCoinBag();
        addSlideShop();
        addSlideBlacklist();
    }

    private void addSlideIntro() {
        addSlide(new SimpleSlide.Builder()
                .title(R.string.app_name)
                .description(R.string.onboarding_intro_description)
                .image(R.drawable.ic_emoticon_neutral_selected)
                .canGoBackward(false)
                .background(R.color.onboardingBackground1)
                .backgroundDark(R.color.onboardingBackground1Dark)
                .scrollable(true)
                .build());
    }

    // MOST USED APPS & USAGE STATS ACCESS
    private void addSlideAppUsage() {
        final SimpleSlide.Builder userAccessSlideBuilder = new SimpleSlide.Builder();

        userAccessSlideBuilder
                .title(R.string.onboarding_app_usage_title)
                .description(R.string.onboarding_app_usage_granted_description)
                .image(R.drawable.ic_onboarding_app_usage)
                .background(R.color.onboardingBackground2)
                .backgroundDark(R.color.onboardingBackground2Dark)
                .scrollable(true);

        if (PermissionUtil.needUsageAccessPermission(this)) {
            userAccessSlideBuilder
                    .description(R.string.onboarding_app_usage_description)
                    .buttonCtaLabel(R.string.onboarding_btn_grant)
                    .buttonCtaClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View vew) {
                            // the user gets a sample entry on first onboarding so it's not empty
                            final SharedPreferencesHandler sharedPrefsHandler =
                                    MainApplication.getSharedPreferencesHandler(getApplicationContext());

                            if (sharedPrefsHandler.onboardingAppUsageEntryEligible()) {
                                sharedPrefsHandler.enterOnboardingAppUsage();

                                final DataRepository dataRepository = MainApplication.getRepository(
                                        getApplicationContext());
                                dataRepository.addAppUsage(getPackageName(), 60);
                            }

                            PermissionUtil.requestUsageAccess(getApplicationContext());
                        }
                    });
        }
        addSlide(userAccessSlideBuilder.build());
    }

    // COIN BAG & BATTERY OPTIMIZATION
    private void addSlideCoinBag() {
        final SimpleSlide.Builder coinbagSlideBuilder = new SimpleSlide.Builder();
        coinbagSlideBuilder
                .title(R.string.onboarding_coinbag)
                .description(R.string.onboarding_coinbag_granted_description)
                .image(R.drawable.ic_onboarding_coin)
                .background(R.color.onboardingBackground6)
                .backgroundDark(R.color.onboardingBackground6Dark)
                .scrollable(true);

        if (PermissionUtil.needBatteryWhitelistPermission(this)) {
            coinbagSlideBuilder
                    .description(R.string.onboarding_coinbag_description)
                    .buttonCtaLabel(R.string.onboarding_btn_whitelist)
                    .buttonCtaClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            PermissionUtil.requestIgnoreBatterOptimization(getApplicationContext());
                        }
                    });
        }
        addSlide(coinbagSlideBuilder.build());
    }

    private void addSlideShop() {

        addSlide(new SimpleSlide.Builder()
                .title(R.string.onboarding_shop_title)
                .description(R.string.onboarding_shop_description)
                .image(R.drawable.ic_onboarding_shopping_cart)
                .background(R.color.onboardingBackground4)
                .backgroundDark(R.color.onboardingBackground4Dark)
                .buttonCtaLabel(R.string.onboarding_btn_shop)
                .buttonCtaClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {

                        // the user gets a step bonus on first onboarding so it's not empty
                        final SharedPreferencesHandler sharedPrefsHandler =
                                MainApplication.getSharedPreferencesHandler(getApplicationContext());

                        if (sharedPrefsHandler.onboardingStepBonusEligible()) {
                            sharedPrefsHandler.setOnboardingStepBonusGranted();

                            final DataRepository dataRepository = MainApplication.getRepository(
                                    getApplicationContext());
                            dataRepository.addStepCount(STEPBONUS);
                        }

                        nextSlide();
                    }
                })
                .build());
    }


    private void addSlideBlacklist() {

        addSlide(new SimpleSlide.Builder()
                .title(R.string.onboarding_blacklist_title)
                .description(R.string.onboarding_blacklist_description)
                .image(R.drawable.ic_onboarding_blacklist)
                .background(R.color.onboardingBackground5)
                .backgroundDark(R.color.onboardingBackground5Dark)
                .scrollable(true)
                .buttonCtaLabel(R.string.onboarding_btn_blacklist)
                .buttonCtaClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        startActivity(new Intent(OnboardingActivity.this,
                                SettingActivity.class));
                    }
                })
                .build());
    }
}
