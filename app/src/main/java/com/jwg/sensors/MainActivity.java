package com.jwg.sensors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.MimeTypeFilter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.jwg.sensors.R;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPOutputStream;

public class MainActivity extends AppCompatActivity implements SensorEventListener, LocationListener {
    static final long MIN_TIME_IN_MILLISECONDS = 1000;
    static final float MIN_DISTANCE_IN_METERS = 1;
    TextView textView, textViewX, textViewY, textViewZ, textViewT, textViewF,textViewLabel;
    Chronometer chronometer;
    Button buttonStart, buttonStop, buttonShare, buttonSend;
    SensorManager sensorManager;
    LocationManager locationManager;
    Sensor accelerometer, gyroscope;
    String sensor_filename = null;
    String currentTimeStamp = null;
    long previous_timestamp = 0;

    static BufferedWriter sensor_file = null;

    static final String directory_name = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
            + File.separator + "PaddleSensorBis";

    public String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
    }

    public String getAbsoluteFileName(String filename) {
        File path = new File(directory_name);
        if (filename.matches(path.toString())) {
            return filename;
        } else {
            return (path.toString() + File.separator + filename);
        }
    }

    void location(boolean enabled) {
        if (enabled) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.HIGH_SAMPLING_RATE_SENSORS
                }, 1);
            }
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_IN_MILLISECONDS, MIN_DISTANCE_IN_METERS, this);
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME_IN_MILLISECONDS, MIN_DISTANCE_IN_METERS, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_IN_MILLISECONDS, MIN_DISTANCE_IN_METERS, this);
        } else {
            locationManager.removeUpdates(this);
            locationManager = null;
        }
    }

    void sensor(boolean enabled) {
        if (enabled) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
        } else {
            sensorManager.unregisterListener(this,accelerometer);
            sensorManager.unregisterListener(this,gyroscope);
        }
    }

    void sensor_logging(boolean enabled, String currentTimeStamp) {
        if (enabled) {
            sensor_filename = String.format("%s-%s.csv.gz", "sensor", currentTimeStamp);
            try {
                sensor_file = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(getAbsoluteFileName(sensor_filename)))));
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Could not open file: "+ "" + e + " " + getAbsoluteFileName(sensor_filename), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            try {
                sensor_file.append("Time,Timestamp,Sensor,X,Y,Z\n");
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Could not append to file: "+ "" + e + " " + getAbsoluteFileName(sensor_filename), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else {
            if (sensor_file != null) {
                try {
                    sensor_file.flush();
                    sensor_file.close();
                    sensor_file = null;
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Could not close file: "+ "" + e + " " + getAbsoluteFileName(sensor_filename), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
    }

    public void openFolder() {
        //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath() +  File.separator + "" + File.separator);
       // Uri uri = Uri.parse(getAbsoluteFileName(sensor_filename));
        //intent.setDataAndType(uri, "text/csv");
        //startActivity(Intent.createChooser(intent, "Open folder"));

        String path = Environment.getExternalStorageDirectory() + "/" + "Download" + "/PaddleSensorBis/";
        path = getAbsoluteFileName(sensor_filename);
        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{getAbsoluteFileName(sensor_filename)}, null, null);
//        Uri uri = Uri.parse(path);
        Uri uri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider",new File(getAbsoluteFileName(sensor_filename)));

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "*/*");
        //Intent j = Intent.createChooser(intent, "Choose an application to open with:");
        //startActivity(j);
        startActivity(intent);
    }

    public void sendFile() {
        //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath() +  File.separator + "" + File.separator);
        // Uri uri = Uri.parse(getAbsoluteFileName(sensor_filename));
        //intent.setDataAndType(uri, "text/csv");
        //startActivity(Intent.createChooser(intent, "Open folder"));

        String path = Environment.getExternalStorageDirectory() + "/" + "Download" + "/PaddleSensorBis/";
        path = getAbsoluteFileName(sensor_filename);
        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{getAbsoluteFileName(sensor_filename)}, null, null);
        // Uri uri = Uri.parse(path);
        // Uri uri = Uri.fromFile(new File(getAbsoluteFileName(sensor_filename)));
        Uri uri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider",new File(getAbsoluteFileName(sensor_filename)));
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setDataAndType(uri, "application/zip");
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        textView = (TextView) findViewById(R.id.textView);
        textViewX = (TextView) findViewById(R.id.textViewX);
        textViewY = (TextView) findViewById(R.id.textViewY);
        textViewZ = (TextView) findViewById(R.id.textViewZ);
        textViewT = (TextView) findViewById(R.id.textViewT);
        textViewF = (TextView) findViewById(R.id.textViewF);
        textViewLabel = (TextView) findViewById(R.id.textViewLabel);
        buttonStart = (Button) findViewById(R.id.start);
        buttonStop = (Button) findViewById(R.id.stop);
        buttonShare = (Button) findViewById(R.id.share);
        buttonSend = (Button) findViewById(R.id.send);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        textView.setText("Jens der aller Beste!");
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION); // 10
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE); // 4

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.HIGH_SAMPLING_RATE_SENSORS
            }, 1);
        }

        File directory = new File(directory_name);
        if (directory.exists() == false) {
            boolean success = directory.mkdir();
            if (!success) {
                Toast.makeText(getApplicationContext(), "Could not create directory: "+ directory_name, Toast.LENGTH_LONG).show();
            }
        }

        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                File directory = new File(directory_name);
                if (directory.exists() == false) {
                    boolean success = directory.mkdir();
                    if (!success) {
                        Toast.makeText(getApplicationContext(), "Could not create directory: "+ directory_name, Toast.LENGTH_LONG).show();
                    }
                }
                currentTimeStamp = getCurrentTimeStamp();
                textView.setText("Start");
                textViewLabel.setText("Sensor:");
                buttonStart.setEnabled(false);
                buttonShare.setEnabled(false);
                buttonSend.setEnabled(false);
                buttonStop.setEnabled(true);
                sensor_logging(true,currentTimeStamp);
                sensor(true);
                textViewF.setText(sensor_filename);
                location(true);
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                textView.setText(getAbsoluteFileName(sensor_filename));
                textViewLabel.setText("Filename:");
                buttonStop.setEnabled(false);
                buttonStart.setEnabled(true);
                buttonShare.setEnabled(true);
                buttonSend.setEnabled(true);
                sensor(false);
                sensor_logging(false,null);
                location(false);
                chronometer.stop();
            }
        });

        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFolder();
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendFile();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float value[];
        long timestamp;
        long  system_time;
        int sensor_type;
        String event_type;

        sensor_type = sensorEvent.sensor.getType();

        timestamp = sensorEvent.timestamp;
        system_time = System.currentTimeMillis();
        value = sensorEvent.values.clone();
        event_type = String.format(Locale.US,"%d,%d,%d,%.8f,%.8f,%.8f\n",system_time,timestamp,sensor_type,value[0],value[1],value[2]);
        try {
            if (event_type!=null) {
                sensor_file.append(event_type);
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Could append to file: "+ "" + e + " " + getAbsoluteFileName(sensor_filename), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        textViewT.setText(String.format(Locale.US,"%d",timestamp));

        switch (sensor_type) {
            case Sensor.TYPE_LINEAR_ACCELERATION:
                textViewX.setText(String.format(Locale.US,"%.8f",value[0]));
                textViewY.setText(String.format(Locale.US,"%.8f",value[1]));
                textViewZ.setText(String.format(Locale.US,"%.8f",value[2]));
                textView.setText("TYPE_LINEAR_ACCELERATION");
                break;
            case Sensor.TYPE_GYROSCOPE:
                textViewX.setText(String.format(Locale.US,"%.8f",value[0]));
                textViewY.setText(String.format(Locale.US,"%.8f",value[1]));
                textViewZ.setText(String.format(Locale.US,"%.8f",value[2]));
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
        long timestamp;
        long  system_time;
        float speed,latitude,longitude;
        String event_type;
        if ((location.hasSpeed() && location.getAccuracy() < 20 && location.hasBearing() && location.hasAccuracy())
                || (location.hasSpeed() && location.getAccuracy() < 10 && location.hasAccuracy())) {
            timestamp = location.getTime();
            system_time = System.currentTimeMillis();
            speed = (float) location.getSpeed();
            latitude = (float) location.getLatitude();
            longitude = (float) location.getLongitude();
            textView.setText("TYPE_LOCATION");
            event_type = String.format(Locale.US,"%d,%d,%d,%.8f,%.8f,%.8f\n",system_time,timestamp,100,speed,latitude,longitude);
            try {
                if (event_type!=null) {
                    if (previous_timestamp != timestamp) {
                        sensor_file.append(event_type);
                    }
                    previous_timestamp = timestamp;
                }
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Could append to file: "+ "" + e + " " + getAbsoluteFileName(sensor_filename), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
        long timestamp;
        long  system_time;
        float speed,latitude,longitude;
        String event_type;

        for (Location location : locations) {
            if ((location.hasSpeed() && location.getAccuracy() < 20 && location.hasBearing() && location.hasAccuracy())
                    || (location.hasSpeed() && location.getAccuracy() < 10 && location.hasAccuracy())) {
                timestamp = location.getTime();
                system_time = System.currentTimeMillis();
                speed = (float) location.getSpeed();
                latitude = (float) location.getLatitude();
                longitude = (float) location.getLongitude();
                textView.setText("TYPE_LOCATION");
                event_type = String.format(Locale.US,"%d,%d,%d,%.8f,%.8f,%.8f\n",system_time,timestamp,100,speed,latitude,longitude);
                try {
                    if (event_type!=null) {
                        if (previous_timestamp != timestamp) {
                            sensor_file.append(event_type);
                        }
                        previous_timestamp = timestamp;
                    }
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Could append to file: "+ "" + e + " " + getAbsoluteFileName(sensor_filename), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
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