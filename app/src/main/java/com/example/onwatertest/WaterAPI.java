package com.example.onwatertest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Build;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class WaterAPI extends AppCompatActivity {
    Context c;
    JSONObject responseObject;
    FusedLocationProviderClient mLocationProvider;
    ArrayList<Location> locations = new ArrayList<Location>();
    Long startTime;
    Long endTime;
    Long timeElapsed;
    Double distanceTravelled = 0.0;
    BatteryManager batteryManager;
    int batteryLevel;
    float speed;
    float speedms;
    private LocationCallback locationCallback;

    public WaterAPI(Context context) {
        this.c = context;
    }

    public void onWater(TextView status, TextView elapsedTime, TextView speedo, TextView distance, BatteryManager batteryManager) {
        this.batteryManager = batteryManager;
        batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        startTime = System.currentTimeMillis();
        // Use current location.
        mLocationProvider = LocationServices.getFusedLocationProviderClient((Activity)this.c);
        if(ActivityCompat.checkSelfPermission(this.c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)this.c, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1000);
        }

        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //Make a request for the location manager
        LocationCallback mLocationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                endTime = System.currentTimeMillis();
                timeElapsed = (endTime - startTime) / 1000;
                elapsedTime.setText(timeElapsed.toString() + " seconds");
                speedo.setText((int) speed + " km/h");
                distance.setText(round(distanceTravelled,0) + " meters");
                batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                if (locationResult == null) {
                    return;
                }
                //size of locationResults.getLocations() will always be 1
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //if the app has just been started, add the current location to the arraylist
                        if(locations.size() == 0) {
                            Location l = new Location("Location");
                            l.setLatitude(round(location.getLatitude(), 5));
                            l.setLongitude(round(location.getLongitude(),5));
                            l.setTime(location.getTime());
                            locations.add(l);
                        }
                        // if locations array size is not 0 and the previous location is not the same, then we add it to the array.
                        if (locations.size() != 0 && round(locations.get(locations.size() - 1).getLatitude(),5) != round(location.getLatitude(),5) && round(locations.get(locations.size() - 1).getLongitude(),5) != round(location.getLongitude(),5)) {
                            Location l = new Location("Location");
                            l.setLatitude(round(location.getLatitude(),5));
                            l.setLongitude(round(location.getLongitude(),5));
                            l.setTime(location.getTime());
                            locations.add(l);

                            // Constantly check the distance between two last locations and add it to the total distance variable.
                            if(locations.size() >= 2) {
                                distanceTravelled += locations.get(locations.size() - 2).distanceTo(locations.get(locations.size() - 1));
                                // km/h
                                speed = (locations.get(locations.size() - 2).distanceTo(locations.get(locations.size() - 1)) / (locations.get(locations.size() - 1).getTime() - locations.get(locations.size() - 2).getTime()) * 3600);
                            }

                        }

                        System.out.println("Current Location: " + round(location.getLatitude(),5) + " " + round(location.getLongitude(),5));
                        System.out.println("Last Array Location: " + round(locations.get(locations.size() - 1).getLatitude(),5) + " " + round(locations.get(locations.size() - 1).getLongitude(),5));
                        System.out.println("Speed in kmh: " + speed);
                    }
                    System.out.println(batteryLevel);
                    System.out.println(locations.size());
                    // Here we implement the tactic of dynamic duty cycling.
                    // If the phone falls below 20% battery we do not use the API anymore and we rely on the GPS.
                    if(batteryLevel >= 20) {
                        isOnWaterRequest(status, location.getLongitude(), location.getLatitude());
                    }
                }
            }
        };

        mLocationProvider.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        /*
        mLocationProvider.getLastLocation().addOnSuccessListener((Activity) this.c, location -> {
            if(location != null) {
                //this.latitude = location.getLatitude();
                //this.longitude = location.getLongitude();
              //  System.out.println(latitude);
               // System.out.println(longitude);
                System.out.println("mLocationProvider addOnSuccessListener");
            }

        }).addOnFailureListener(e -> {
            System.out.println("Failed to retrieve location!");
        });
        */
    }

    private void isOnWaterRequest(TextView status, Double longitude, Double latitude){
        RequestQueue queue = Volley.newRequestQueue(this.c);
        String url = "";
        if(longitude != null && latitude != null) {
            url = "https://api.onwater.io/api/v1/results/" + latitude.toString() + "," + longitude.toString() + "?access_token=kZPaDPUNtsx_oTz6y8Mg";
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            try {
                                responseObject = new JSONObject(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                if (responseObject.getString("water").equals("true")) {
                                    status.setText("Status: You are on water!" /*+  "\nDistance Travelled(m): " + distanceTravelled + "\n Current Speed(m/s): " + speed*/);
                                } else if (responseObject.getString("water").equals("false")) {
                                    status.setText("Status: You are not water!" /*+  "\nDistance Travelled(m): " + distanceTravelled + "\n Current Speed(m/s): " + speed*/);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    status.setText("That didn't work!");
                    System.out.println(latitude);
                    System.out.println(longitude);
                }

            });

            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        } else {
            status.setText("Unable to retrieve location!");
        }
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
