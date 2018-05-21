package com.edu.uni.augsburg.uniatron.service.tasks;

import android.app.Service;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.service.StickyServiceTask;
import com.orhanobut.logger.Logger;

import static android.content.Context.SENSOR_SERVICE;

/**
 * The task listens to taken steps.
 *
 * @author Fabio Hellmann
 */
public class StepCounterTask implements StickyServiceTask, SensorEventListener {
    private final SensorManager mSensorManager;
    private final DataRepository mRepository;

    /**
     * Ctr.
     *
     * @param service The service.
     */
    public StepCounterTask(@NonNull final Service service) {
        mSensorManager = (SensorManager) service.getSystemService(SENSOR_SERVICE);
        mRepository = ((MainApplication) service.getApplicationContext()).getRepository();
    }

    @Override
    public void onCreate() {
        // this could return null if the app has
        // no permissions for that sensor, or it doesn't exist
        if (mSensorManager == null) {
            Logger.e("step detector sensor was not started");
        } else {
            final Sensor stepDetectorSensor = mSensorManager.
                    getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            mSensorManager.registerListener(this,
                    stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);

            Logger.d("step detector sensor was successful started");
        }
    }

    @Override
    public void onDestroy() {
        // ignore
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        // detects every single step
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            final int detectedSteps = (int) event.values[0];
            mRepository.addStepCount(detectedSteps);
        }
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
        // ok
    }
}
