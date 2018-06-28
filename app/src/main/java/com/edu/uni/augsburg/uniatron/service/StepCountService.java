package com.edu.uni.augsburg.uniatron.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;

/**
 * The step count service collects steps and commits them to the database.
 *
 * @author Leon Wöhrl
 */
public class StepCountService extends Service implements SensorEventListener {

    private static final float LIMIT = 8;
    private static final float HALF = 0.5f;
    private static final float MULTIPLIER = 480;
    private final float[] mLastValues = new float[3 * 2];
    private final float[] mScale = new float[2];
    private final float[] mLastDirections = new float[3 * 2];
    private final float[][] mLastExtremes = {new float[3 * 2], new float[3 * 2]};
    private final float[] mLastDiff = new float[3 * 2];
    private float mYOffset;
    private int mLastMatch = -1;

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        commitSteps(100);
        // grab step detector and register the listener
        final SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // this could return null if the app has
        // no permissions for that sensor, or it doesn't exist
        final Sensor stepDetectorSensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        // not all phones have a step sensor. fallback to accelerometer when necessary
        if (stepDetectorSensor == null) {
            final Sensor accelerometerSensor = sensorManager
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this,
                    accelerometerSensor, sensorManager.SENSOR_DELAY_FASTEST);
        } else {
            sensorManager.registerListener(this, stepDetectorSensor,
                    SensorManager.SENSOR_DELAY_FASTEST);
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mYOffset = MULTIPLIER * HALF;
        mScale[0] = -(MULTIPLIER * HALF * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = -(MULTIPLIER * HALF * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
        // ok
    }

    private void commitSteps(final int detectedSteps) {
        final DataRepository dataRepository = MainApplication.getRepository(getBaseContext());

        dataRepository.addStepCount(detectedSteps);
    }


    @Override
    public void onSensorChanged(final SensorEvent event) {
        final Sensor sensor = event.sensor;
        synchronized (this) {
            // detects every single step
            if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                final int detectedSteps = (int) event.values[0];
                commitSteps(detectedSteps);

            } else if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                detectPeak(event.values);
            }
        }
    }

    // https://github.com/bagilevi/android-pedometer/blob
    // /master/src/name/bagi/levente/pedometer/StepDetector.java
    private void detectPeak(final float... args) {
        float vSum = 0;
        for (int i = 0; i < 3; i++) {
            final float velocity = mYOffset + args[i] * mScale[1];
            vSum += velocity;
        }
        final float velocity = vSum / 3;

        final float direction = Float.compare(velocity, mLastValues[0]);
        if (direction == -mLastDirections[0]) {
            // Direction changed
            final int extType = direction > 0 ? 0 : 1; // minumum or maximum?
            mLastExtremes[extType][0] = mLastValues[0];
            final float diff = Math.abs(mLastExtremes[extType][0] - mLastExtremes[1 - extType][0]);

            if (diff > LIMIT) {

                final boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[0] * 2 / 3);
                final boolean isPreviousLargeEnough = mLastDiff[0] > (diff / 3);
                final boolean isNotContra = mLastMatch != 1 - extType;

                if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                    commitSteps(1);
                }
                mLastMatch = extType;
            } else {
                mLastMatch = -1;
            }
            mLastDiff[0] = diff;
        }
        mLastDirections[0] = direction;
        mLastValues[0] = velocity;
    }
}
