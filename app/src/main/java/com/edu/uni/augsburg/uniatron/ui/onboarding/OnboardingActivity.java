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

import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.ui.setting.SettingActivity;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.rvalerio.fgchecker.Utils;

public class OnboardingActivity extends IntroActivity {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        setFullscreen(true);
        super.onCreate(savedInstanceState);

        // INTRO
        addSlide(new SimpleSlide.Builder()
                .title(R.string.app_name)
                .description(R.string.onboarding_intro_description)
                .image(R.drawable.ic_emoticon_neutral_selected)
                .canGoBackward(false)
                .background(R.color.onboardingBackground1)
                .backgroundDark(R.color.onboardingBackground1Dark)
                .scrollable(true)
                .build());

        // MOST USED APPS & USAGE STATS ACCESS
        if (!usageAccessGranted()) {
            addSlide(new SimpleSlide.Builder()
                    //.title("Get an overview of your most used apps")
                    .title(R.string.onboarding_app_usage_title)
                    .description(R.string.onboarding_app_usage_description)
                    .image(R.drawable.ic_onboarding_app_usage)
                    .background(R.color.onboardingBackground2)
                    .backgroundDark(R.color.onboardingBackground2Dark)
                    .scrollable(true)
                    .buttonCtaLabel(R.string.onboarding_btn_grant)
                    .buttonCtaClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                                    && !Utils.hasUsageStatsPermission(getApplicationContext())) {
                                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                            }

                        }
                    })
                    .build());
        } else {
            addSlide(new SimpleSlide.Builder()
                    .title(R.string.onboarding_app_usage_title)
                    .description(R.string.onboarding_app_usage_granted_description)
                    .image(R.drawable.ic_onboarding_app_usage)
                    .background(R.color.onboardingBackground2)
                    .backgroundDark(R.color.onboardingBackground2Dark)
                    .scrollable(true)
                    .build());
        }

        // COIN BAG & BATTERY OPTIMIZATION
        if(checkBatteryOptimized()) {
            addSlide(new SimpleSlide.Builder()
                    .title(R.string.onboarding_coinbag)
                    .description(R.string.onboarding_coinbag_description)
                    .image(R.drawable.ic_onboarding_coinbag)
                    .background(R.color.onboardingBackground3)
                    .backgroundDark(R.color.onboardingBackground3Dark)
                    .scrollable(true)
                    .buttonCtaLabel(R.string.onboarding_btn_whitelist)
                    .buttonCtaClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
                                startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));

                            }
                        }
                    })
                    .build());
        } else {
            addSlide(new SimpleSlide.Builder()
                    .title(R.string.onboarding_coinbag)
                    .description(R.string.onboarding_coinbag_granted_description)
                    .image(R.drawable.ic_onboarding_coinbag)
                    .background(R.color.onboardingBackground3)
                    .backgroundDark(R.color.onboardingBackground3Dark)
                    .scrollable(true)
                    .build());
        }

        // SHOP
        addSlide(new SimpleSlide.Builder()
                .title("Shop: TODO shop image")
                .description(R.string.onboarding_shop_description)
                //.image(R.drawable.ic_onboarding_check)
                .background(R.color.onboardingBackground4)
                .backgroundDark(R.color.onboardingBackground4Dark)
                .scrollable(true)
                .build());

/*
        // BLACKLIST (todo: pretty way)
        addSlide(new SimpleSlide.Builder()
                .layout(R.layout.activity_onboarding_blacklist)
                .background(R.color.onboardingBackground5)
                .backgroundDark(R.color.onboardingBackground5Dark)
                .build());
*/

        // BLACKLIST
        addSlide(new SimpleSlide.Builder()
                .title("Almost done")
                .description("Add apps to you blacklist\nThey will be blocked when your time runs out\n\nYou can "
                        + "always change these settings later")
                .image(R.drawable.ic_emoticon_fantastic_selected)
                .background(R.color.onboardingBackground5)
                .backgroundDark(R.color.onboardingBackground5Dark)
                .scrollable(true)
                .buttonCtaLabel("Choose apps")
                .buttonCtaClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OnboardingActivity.this.startActivity(new Intent(OnboardingActivity.this, SettingActivity.class));
                    }
                })
                .build());


    }
/*
    @Override
    public void onResume() {
        super.onResume();
        if (currentSlideCallback == 2 && usageAccessGranted() ) {
            nextSlide();
        }

        if (currentSlideCallback == 3 && checkBatteryOptimized()) {
            nextSlide();
        }

    }
*/

    private boolean checkBatteryOptimized() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            return !powerManager
                    .isIgnoringBatteryOptimizations(getApplicationContext().getPackageName());
        }
        return true;
    }

    private boolean usageAccessGranted() {
        try {
            PackageManager packageManager = getApplicationContext().getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getApplicationContext().getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getApplicationContext().getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);

            return mode == AppOpsManager.MODE_ALLOWED;

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
