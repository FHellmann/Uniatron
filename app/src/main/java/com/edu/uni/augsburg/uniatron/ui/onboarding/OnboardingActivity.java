package com.edu.uni.augsburg.uniatron.ui.onboarding;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.ui.setting.SettingActivity;
import com.edu.uni.augsburg.uniatron.ui.util.PermissionUtil;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.rvalerio.fgchecker.Utils;

/**
 * Handles the creation of the App onboarding.
 *
 * @author Leon Wöhrl
 */
public class OnboardingActivity extends IntroActivity {

    boolean bonusGranted = false;
    boolean sampleEntryAdded = false;


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
                            PermissionUtil.requestUsageAccess(
                                    OnboardingActivity.super.getApplicationContext());
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
                            PermissionUtil.requestIgnoreBatterOptimization(
                                    OnboardingActivity.super.getApplicationContext());
                        }
                    });
        }
        addSlide(coinbagSlideBuilder.build());
    }

    private void addSlideShop() {

        final SharedPreferencesHandler sharedPrefsHandler =
                MainApplication.getSharedPreferencesHandler(getApplicationContext());

        if (!bonusGranted && sharedPrefsHandler.isFirstStart()) {
            bonusGranted = true;
            DataRepository dataRepository = MainApplication.getRepository(
                    OnboardingActivity.super.getApplicationContext());
            dataRepository.addStepCount(500);
        }

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
                        nextSlide();
                    }
                })
                .build());
    }


    private void addSlideBlacklist() {

        final SharedPreferencesHandler sharedPrefsHandler =
                MainApplication.getSharedPreferencesHandler(getApplicationContext());

        if (!sampleEntryAdded && sharedPrefsHandler.isFirstStart()) {
            sampleEntryAdded =  true;
            DataRepository dataRepository = MainApplication.getRepository(
                    OnboardingActivity.super.getApplicationContext());
            dataRepository.addAppUsage(OnboardingActivity.super.getPackageName(), 60);
        }

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
                        OnboardingActivity.this.startActivity(new Intent(OnboardingActivity.this,
                                SettingActivity.class));
                    }
                })
                .build());
    }
}
