package com.example.onwatertest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // UI elements
    Button activityButton;
    TextView activityStatus;
    TextView elapsedTime;
    TextView speed;
    TextView distance;

    // Functionality elements
    BatteryManager batteryManager;
    Backend w;
    private static boolean isActivityRunning = false;
    private static boolean isOnWater = false;
    int seconds;
    boolean runTimer;
    Timer timer;


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
        w = new Backend(this);

        seconds = 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void clickButton(View view) {
        w.onWater(this.activityStatus, this.elapsedTime, this.speed, this.distance, this.batteryManager);
        if (isActivityRunning){
            isActivityRunning = false;
            activityButton.setText("Start Tracking");
            Toast.makeText(this, "Activity stopped", Toast.LENGTH_LONG).show();
            runTimer = false;
            seconds = 0;
            speed.setText(0 + " km/h");
            distance.setText(0 + " meters");
        } else {
            isActivityRunning = true;
            activityButton.setText("Stop Tracking");
            Toast.makeText(this, "Happy kayaking!", Toast.LENGTH_LONG).show();
            runTimer = true;
            updateElapsedTime();
        }
    }

    private void updateElapsedTime(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Check if activity is running and if we are not on water we stop it in the WaterAPI class.
                        if(!isActivityRunning) {
                            activityButton.setText("Start Tracking");
                            runTimer = false;
                            seconds = 0;
                            speed.setText(0 + " km/h");
                            distance.setText(0 + " meters");
                        }
                        if(runTimer){
                            elapsedTime.setText(seconds + " seconds");
                            seconds++;
                        }else{
                            timer.cancel();
                        }
                    }
                });
            }
        },0,1000);//Update text every second
    }

    public static boolean getIsActivityRunning(){
        return isActivityRunning;
    }
    public static void setIsActivityRunning(boolean activityRunning){
        isActivityRunning = activityRunning;
    }
}