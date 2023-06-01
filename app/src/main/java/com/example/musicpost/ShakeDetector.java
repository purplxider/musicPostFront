package com.example.musicpost;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener {
    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F; // Adjust this value as per your requirement
    private static final int SHAKE_TIMEOUT = 500; // Adjust this value as per your requirement
    private static final int SHAKE_DURATION = 1000; // Adjust this value as per your requirement
    private SensorManager sensorManager;
    private OnShakeListener shakeListener;
    private long lastShakeTime;
    private long lastShakeTimestamp;
    private long lastForce;
    private boolean isShakeStarted;
    private float lastX, lastY, lastZ;

    public ShakeDetector(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void start() {
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    public void setOnShakeListener(OnShakeListener listener) {
        shakeListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float acceleration = (float) Math.sqrt(x * x + y * y + z * z);
        float delta = acceleration - lastForce;
        lastForce = (long) acceleration;

        if (!isShakeStarted) {
            lastX = x;
            lastY = y;
            lastZ = z;
            isShakeStarted = true;
        } else {
            float deltaX = Math.abs(x - lastX);
            float deltaY = Math.abs(y - lastY);
            float deltaZ = Math.abs(z - lastZ);

            if (deltaX > deltaY && deltaX > deltaZ) {
                if (delta > SHAKE_THRESHOLD_GRAVITY) {
                    long currentTime = System.currentTimeMillis();
                    if (lastShakeTimestamp + SHAKE_TIMEOUT > currentTime) {
                        return;
                    }

                    if (lastShakeTime + SHAKE_DURATION < currentTime) {
                        shakeListener.onShake();
                        lastShakeTime = currentTime;
                    }

                    lastShakeTimestamp = currentTime;
                }
            }

            isShakeStarted = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public interface OnShakeListener {
        void onShake();
    }
}
