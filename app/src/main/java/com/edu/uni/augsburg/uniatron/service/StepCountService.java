package com.edu.uni.augsburg.uniatron.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;

// uncomment after merge wth master
//import com.edu.uni.augsburg.uniatron.MainApplication;

/**
 * The step count service collects steps and commits them to the database.
 *
 * @author Leon Wöhrl
 */
public class StepCountService extends Service implements SensorEventListener {

    private static final int COMMIT_SIZE = 10;
    private int currentSteps;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        currentSteps = 0;
    }

    @Override
    public int onStartCommand(Intent intent,   int flags, int startId) {
        // grab step detector and register the listener
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // this could return null if the app has no permissions for that sensor, or it doesn't exist
        if (sensorManager != null) {
            Sensor stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
        // this causes the OS to restart the service if it has been force stopped
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // detects every single step
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            int detectedSteps = (int) event.values[0];
            currentSteps += detectedSteps;

            if (currentSteps >= COMMIT_SIZE) {

                // subtract steps here and always commit exactly <COMMIT_SIZE> steps to prevent async issues
                // async could happen when the sensor delivers new data before the async task is completed
                currentSteps -= COMMIT_SIZE;
                commitSteps(COMMIT_SIZE);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ok
    }

    @Override
    public void onDestroy() {
        // commit the steps that are due
        int tempSteps = currentSteps;
        // set to zero before commit in case of async issue
        currentSteps = 0;
        commitSteps(tempSteps);

        super.onDestroy();
    }

    /**
     * The function to commit exactly <COMMIT_SIZE> to the DataRepository
     */
    private void commitSteps(int numberOfSteps) {
        //execute this on a separate thread as it may some time
        // java.lang.IllegalStateException: Cannot access database on the main thread since it may potentially lock the UI for a long period of time.
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // uncomment after merge with master
                //MainApplication.getRepository().addStepCount(numberOfSteps);
            }
        });
    }

}
