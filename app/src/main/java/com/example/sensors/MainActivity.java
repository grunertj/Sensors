package com.example.sensors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener , LocationListener {
    TextView textView,textViewX,textViewY,textViewZ,textViewT;
    Button buttonStart, buttonStop;
    SensorManager sensorManager;
    Sensor accelerometer, gyroscope;

    void sensor(boolean enabled) {
        if (enabled) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            sensorManager.unregisterListener(this,accelerometer);
            sensorManager.unregisterListener(this,gyroscope);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        textViewX = (TextView) findViewById(R.id.textViewX);
        textViewY = (TextView) findViewById(R.id.textViewY);
        textViewZ = (TextView) findViewById(R.id.textViewZ);
        textViewT = (TextView) findViewById(R.id.textViewT);
        buttonStart = (Button) findViewById(R.id.start);
        buttonStop = (Button) findViewById(R.id.stop);
        textView.setText("Jens der aller Beste!");
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION); // 10
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE); // 4

        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                textView.setText("Start");
                buttonStart.setEnabled(false);
                buttonStop.setEnabled(true);
                sensor(true);
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                textView.setText("Stop");
                buttonStop.setEnabled(false);
                buttonStart.setEnabled(true);
                sensor(false);
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float value[];
        long timestamp;
        int sensor_type;
        String event_type;

        sensor_type = sensorEvent.sensor.getType();

        timestamp = sensorEvent.timestamp;
        value = sensorEvent.values.clone();
        //event_type = String.format("%d,%ld,%.8f,%.8f,%.8f",sensor_type,timestamp,value[0],value[1],value[2]);
        event_type = String.format(Locale.US,"%d,%d",sensor_type,timestamp);


        textViewT.setText(event_type);

        switch (sensor_type) {
            case Sensor.TYPE_LINEAR_ACCELERATION:
                textViewX.setText(String.format(Locale.US,"%.8f",value[0]));
                textViewY.setText(String.format(Locale.US,"%.8f",value[0]));
                textViewZ.setText(String.format(Locale.US,"%.8f",value[2]));
                textView.setText("Accelerometer");
                break;
            case Sensor.TYPE_GYROSCOPE:
                textView.setText("TYPE_GYROSCOPE");
                break;
            default:
                textView.setText("None");
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }
}