package com.edu.uni.augsburg.uniatron.ui.splash;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;

import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.ui.MainActivity;
import com.edu.uni.augsburg.uniatron.ui.onboarding.OnBoardingActivity;

/**
 * A splash screen which appears when the app is started.
 *
 * @author Fabio Hellmann
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 500;
    private final Handler mHandler = new Handler();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final SplashViewModel model = ViewModelProviders.of(this).get(SplashViewModel.class);
        mHandler.postDelayed(() -> {
            final boolean introNeeded = model.isIntroNeeded(this);
            final TaskStackBuilder stackBuilder = getTaskStack(introNeeded);
            stackBuilder.startActivities();
        }, SPLASH_DELAY);
    }

    @NonNull
    private TaskStackBuilder getTaskStack(final boolean introNeeded) {
        if (introNeeded) {
            return TaskStackBuilder.create(this)
                    .addNextIntentWithParentStack(new Intent(this, MainActivity.class))
                    .addNextIntent(new Intent(this, OnBoardingActivity.class));
        } else {
            return TaskStackBuilder.create(this)
                    .addNextIntent(new Intent(this, MainActivity.class));
        }
    }
}
