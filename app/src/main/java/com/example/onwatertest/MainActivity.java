package com.example.onwatertest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // UI elements
    Button activityButton;
    TextView activityStatus;
    TextView elapsedTime;
    TextView speed;
    TextView distance;

    // Functionality elements
    BatteryManager batteryManager;
    WaterAPI w;
    private static boolean isActivityRunning = false;

    Runnable updateElapsedTime;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI elements
        activityButton = (Button) findViewById(R.id.waterButton);
        activityStatus = (TextView) findViewById(R.id.trackingStatus);
        elapsedTime = (TextView) findViewById(R.id.elapsedTime);
        speed = (TextView) findViewById(R.id.speed);
        distance = (TextView) findViewById(R.id.distance);

        // Functionality elements
        batteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
        w = new WaterAPI(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void clickButton(View view) {
        w.onWater(this.activityStatus, this.elapsedTime, this.speed, this.distance, this.batteryManager);
        if (isActivityRunning){
            isActivityRunning = false;
            activityButton.setText("Start Tracking");
            Toast.makeText(this, "Activity stopped", Toast.LENGTH_LONG).show();

        } else {
            isActivityRunning = true;
            activityButton.setText("Stop Tracking");
            Toast.makeText(this, "Happy kayaking!", Toast.LENGTH_LONG).show();
        }

    }

    public static boolean getIsActivityRunning(){
        return isActivityRunning;
    }




}