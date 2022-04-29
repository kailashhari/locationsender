package com.example.locationsender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText number;
    private EditText delay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button track = (Button) findViewById(R.id.track);
        number = (EditText) findViewById(R.id.number);
        delay = (EditText) findViewById(R.id.delay);
        delay.setText("10");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Track.class);
                SharedPreferences sharedPreferences =getSharedPreferences("parent's phone", MODE_PRIVATE);
                SharedPreferences.Editor editor =sharedPreferences.edit();
                String phnum = number.getText().toString();
                if(!validatePh(phnum)) {
                    Toast.makeText(MainActivity.this, "Phone number is not valid!", Toast.LENGTH_LONG).show();
                }
                editor.putString("parent'sphone", phnum);
                editor.putInt("delaybeforenext", Integer.parseInt(delay.getText().toString()));
                editor.apply();
                startActivity(intent);
            }
        });
    }
    private boolean validatePh(String s) {
        if(s.length() != 10 && s.length() != 8) {
            return false;
        }
        return true;
    }
}