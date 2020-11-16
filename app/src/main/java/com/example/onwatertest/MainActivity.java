package com.example.onwatertest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button waterButton;
    TextView waterStatus;
    EditText latText;
    EditText longText;
    WaterAPI w;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        waterButton = (Button) findViewById(R.id.waterButton);
        waterStatus = (TextView) findViewById(R.id.trackingStatus);
        latText = (EditText) findViewById(R.id.latitudeInput);
        longText = (EditText) findViewById(R.id.longtitudeInput);
        w = new WaterAPI(this);
    }

    public void clickButton(View view) {
        w.onWater(this.waterStatus, this.latText, this.longText);
    }



}