package com.example.locationsender;

import static java.lang.Math.abs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.security.Permission;
import java.util.Timer;
import java.util.TimerTask;

public class Track extends AppCompatActivity {
    FusedLocationProviderClient fusedLocationProviderClient;
    String message;
    Timer timer;
    Button stop;
    int delay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.cancel();
                Intent intent = new Intent(Track.this, MainActivity.class);
                startActivity(intent);
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("parent's phone", MODE_PRIVATE);
        String num = sharedPreferences.getString("parent'sphone", "");
        delay = sharedPreferences.getInt("delaybeforenext", 10);
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Required Permissions not granted", Toast.LENGTH_LONG).show();
        } else if (num.length() != 0) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            startSending(num);
        }
    }

    private void startSending(String n) {
        timer = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(Track.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Track.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location != null) {
                            boolean north = false, east = false;
                            if (location.getLatitude() >= 0) {
                                north = true;
                            }
                            if (location.getLongitude() >= 0) {
                                east = true;
                            }
                            message = "Your child is at location : " + abs(location.getLatitude()) + " degrees " + (north ? "north, " : "south, ") + abs(location.getLongitude()) + " degrees " + (east ? "east" : "west") + ". Location: https://www.google.com/maps/search/?api=1&query=" + location.getLatitude() + "%2C" + location.getLongitude();

                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(n, null, message, null, null);
                        }


                    }
                });

            }
        };
        timer.scheduleAtFixedRate(tt, 0, delay * 1000L);
    }

}