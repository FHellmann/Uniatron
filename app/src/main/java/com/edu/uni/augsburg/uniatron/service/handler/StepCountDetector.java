package com.edu.uni.augsburg.uniatron.service.handler;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;

import com.annimon.stream.IntStream;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.orhanobut.logger.Logger;

import static android.content.Context.SENSOR_SERVICE;

/**
 * The step detection will be handled by this class.
 *
 * @author Fabio Hellmann
 */
public final class StepCountDetector {
    private final SensorManager mSensorManager;
    private final StepDetector mStepDetector;

    /**
     * Ctr.
     *
     * @param context The context.
     */
    private StepCountDetector(@NonNull final Context context) {
        final StepCountModel model = new StepCountModel(context);
        mStepDetector = new StepDetector(model::addSteps);

        // grab step detector and register the listener
        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);

        if (mSensorManager == null) {
            Logger.w("The sensor manager is not available!");
        } else {
            // not all phones have a step sensor. fallback to accelerometer when necessary
            final Sensor stepDetectorSensor =
                    mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) == null
                            ? mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                            : mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

            mSensorManager.registerListener(
                    mStepDetector,
                    stepDetectorSensor,
                    SensorManager.SENSOR_DELAY_FASTEST
            );
        }
    }

    /**
     * Destroys the handler.
     */
    public void destroy() {
        mSensorManager.unregisterListener(mStepDetector);
    }

    /**
     * Starts the handler.
     *
     * @param context The context.
     * @return The handler.
     */
    public static StepCountDetector start(@NonNull final Context context) {
        return new StepCountDetector(context);
    }

    private static final class StepDetector implements SensorEventListener {
        private static final float LIMIT = 8f;
        private static final float HALF = 0.5f;
        private static final float MULTIPLIER = 480f;
        private static final float Y_OFFSET = MULTIPLIER * HALF;
        private static final float SCALE = -(MULTIPLIER * HALF
                * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
        private float mLastValue;
        private float mLastDirection;
        private float mLastDiff;
        private Match mLastMatch = Match.NONE;
        private final Consumer<Integer> mStepConsumer;

        StepDetector(@NonNull final Consumer<Integer> stepConsumer) {
            mStepConsumer = stepConsumer;
        }

        @Override
        public void onSensorChanged(@NonNull final SensorEvent event) {
            final Sensor sensor = event.sensor;
            synchronized (this) {
                // detects every single step
                if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                    final int detectedSteps = (int) event.values[0];
                    mStepConsumer.accept(detectedSteps);
                } else if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    detectPeak(event.values);
                }
            }
        }

        @Override
        public void onAccuracyChanged(@NonNull final Sensor sensor, final int accuracy) {
            // ok
        }

        private void detectPeak(final float... values) {
            final float velocityAvg = getVelocityAvg(values);
            if (isChangedDirection(Float.compare(velocityAvg, mLastValue))) {
                // Direction changed
                final float diff = Math.abs(mLastValue - velocityAvg);
                if (diff > LIMIT) {
                    final Match minOrMax = Match.getMinOrMax(mLastDirection);
                    if (isApplicable(minOrMax, diff, values)) {
                        mStepConsumer.accept(1);
                    }
                    mLastMatch = minOrMax;
                } else {
                    mLastMatch = Match.NONE;
                }
                mLastDiff = diff;
            }
            mLastValue = velocityAvg;
        }

        private boolean isApplicable(@NonNull final Match minOrMax, final float diff, final float... values) {
            final boolean isNotContra = mLastMatch != minOrMax.invert();
            final boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff * 2 / values.length);
            final boolean isPreviousLargeEnough = mLastDiff > (diff / values.length);
            return isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra;
        }

        private boolean isChangedDirection(final float direction) {
            final boolean changedDirection = direction == -mLastDirection;
            mLastDirection = direction;
            return changedDirection;
        }

        private float getVelocityAvg(final float... values) {
            return (float) ((IntStream.range(0, values.length)
                    .mapToDouble(index -> values[index])
                    .sum() + Y_OFFSET * values.length * SCALE) / values.length);
        }

        private enum Match {
            NONE(-1), MINIMUM(0), MAXIMUM(1);

            private final int mCompareValue;

            Match(final int compareValue) {
                mCompareValue = compareValue;
            }

            Match invert() {
                return Stream.of(values())
                        .filter(match -> match.mCompareValue == 1 - mCompareValue)
                        .findFirst()
                        .orElseThrow(IllegalStateException::new);
            }

            static Match getMinOrMax(@NonNull final Number number) {
                return number.floatValue() > 0 ? MINIMUM : MAXIMUM;
            }
        }
    }
}
