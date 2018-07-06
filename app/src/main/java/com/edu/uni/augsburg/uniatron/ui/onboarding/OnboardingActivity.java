package com.edu.uni.augsburg.uniatron.ui.onboarding;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;

import com.edu.uni.augsburg.uniatron.R;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.heinrichreimersoftware.materialintro.slide.Slide;

public class OnboardingActivity extends IntroActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        //setFullscreen(true);
        super.onCreate(savedInstanceState);

        final Slide permissionsSlide;

        permissionsSlide = new SimpleSlide.Builder()
                .title("Uniatron")
                .description("You should whitelist our App to make sure your Steps and App are properly tracked")
                .background(R.color.onboardingBackground1)
                .backgroundDark(R.color.onboardingBackground1Dark)
                .scrollable(true)
                .permissions(new String[]{Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS})
                .build();
        addSlide(permissionsSlide);

        addSlide(new SimpleSlide.Builder()
                .title("Uniatron2")
                .description("You should whitelist our App to make sure your Steps and App are properly tracked")
                .image(R.drawable.ic_access_time_black_24dp)
                .background(R.color.onboardingBackground1)
                .backgroundDark(R.color.onboardingBackground1Dark)
                .scrollable(false)
                .permission(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                .build());

    }
}
