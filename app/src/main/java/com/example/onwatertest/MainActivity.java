package com.example.onwatertest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button waterButton;
    TextView waterStatus;
    TextView elapsedTime;
    BatteryManager batteryManager;
    WaterAPI w;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        waterButton = (Button) findViewById(R.id.waterButton);
        waterStatus = (TextView) findViewById(R.id.trackingStatus);
        elapsedTime = (TextView) findViewById(R.id.elapsedTime);
        batteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
        w = new WaterAPI(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void clickButton(View view) {
        w.onWater(this.waterStatus, this.elapsedTime, this.batteryManager);
    }



}