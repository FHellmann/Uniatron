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

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // grab step detector and register the listener
        final SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // this could return null if the app has
        // no permissions for that sensor, or it doesn't exist
        if (sensorManager != null) {
            final Sensor stepDetectorSensor = sensorManager.
                    getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            sensorManager.registerListener(this,
                    stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        // detects every single step
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            final int detectedSteps = (int) event.values[0];

            commitSteps(detectedSteps);
        }
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
        // ok
    }

    private void commitSteps(final int detectedSteps) {
        final MainApplication application = (MainApplication) getApplicationContext();
        final DataRepository dataRepository = application.getRepository();
        dataRepository.addStepCount(detectedSteps);
    }
}
