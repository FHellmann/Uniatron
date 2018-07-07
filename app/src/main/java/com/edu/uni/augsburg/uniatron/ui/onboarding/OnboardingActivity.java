package com.edu.uni.augsburg.uniatron.ui.onboarding;

import android.Manifest;
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
import com.heinrichreimersoftware.materialintro.slide.Slide;
import com.rvalerio.fgchecker.Utils;

public class OnboardingActivity extends IntroActivity {

    static int currentSlideCallback = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setFullscreen(true);
        super.onCreate(savedInstanceState);






        // App usage screenshot und permission
        // time account
        // money bag screenshot + store und whitelist





        currentSlideCallback = 1;
        addSlide(new SimpleSlide.Builder()
                .title("Uniatron")
                .description("Keep an eye on your health and productivity")
                .image(R.drawable.ic_emoticon_neutral_selected)
                .canGoBackward(false)
                .background(R.color.onboardingBackground1)
                .backgroundDark(R.color.onboardingBackground1Dark)
                .scrollable(true)
                .build());


        // intro
        currentSlideCallback = 1;
        addSlide(new SimpleSlide.Builder()
                .background(R.color.onboardingBackground1)
                .backgroundDark(R.color.onboardingBackground1Dark)
                .layout(R.layout.activity_onboarding_app_overview).build());


        // explanation1
        currentSlideCallback = 2;
        addSlide(new SimpleSlide.Builder()
                .title("Time Account")
                .description("To limit excessive smartphone usage, your time account manages how long you can use selected apps")
                .image(R.drawable.ic_emoticon_happy_selected)
                .background(R.color.onboardingBackground1)
                .backgroundDark(R.color.onboardingBackground1Dark)
                .scrollable(true)
                .build());

        // explanation1
        currentSlideCallback = 3;
        addSlide(new SimpleSlide.Builder()
                .title("Count your daily Steps")
                .description("Your Coin Bag fills up with every step you take.\nVisit the shop to trade them for additional app time")
                .image(R.drawable.ic_emoticon_happy_selected)
                .canGoBackward(false)
                .background(R.color.onboardingBackground1)
                .backgroundDark(R.color.onboardingBackground1Dark)
                .scrollable(true)
                .build());



        // usage accesss
        if (!usageAccessGranted()) {
            currentSlideCallback = 4;
            addSlide(new SimpleSlide.Builder()
                    //.title("Get an overview of your most used apps")
                    .title("App usage")
                    .description("We need your permission to detect and show your most used apps")
                    .image(R.drawable.onboarding_app_usage)
                    .background(R.color.onboardingBackground2)
                    .backgroundDark(R.color.onboardingBackground2Dark)
                    .scrollable(true)
                    .buttonCtaLabel("Grant")
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
        }

        // battery optimization
        if(checkBatteryOptimized()) {
            currentSlideCallback = 5;
            addSlide(new SimpleSlide.Builder()
                    .title("Count your steps and get rewards")
                    .description("To make sure we don't miss any of your steps, please whitelist"
                            + " our app on the system power management\nSelect 'All Apps' to "
                            + "find UNIAtron and disable the optimization")
                    .image(R.drawable.ic_emoticon_fantastic_selected)
                    .background(R.color.onboardingBackground3)
                    .backgroundDark(R.color.onboardingBackground3Dark)
                    .scrollable(true)
                    .buttonCtaLabel("Grant")
                    .buttonCtaClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
                                startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));
                            }
                        }
                    })
                    .build());

        }

        // blacklist entries
        addSlide(new SimpleSlide.Builder()
                .title("Final Step")
                .description("Select your fitness level and add apps to you blacklist. These apps will be blocked when your time runs out\n\nYou can "
                        + "always change these settings later")
                .image(R.drawable.ic_emoticon_fantastic_selected)
                .background(R.color.onboardingBackground3)
                .backgroundDark(R.color.onboardingBackground3Dark)
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
