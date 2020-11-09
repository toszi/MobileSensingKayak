package com.example.onwatertest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WaterAPI extends AppCompatActivity {
    Context c;
    JSONObject responseObject;
    FusedLocationProviderClient mLocationProvider;
    ArrayList<Location> locations = new ArrayList<Location>();

    private LocationCallback locationCallback;

    public WaterAPI(Context context) {
        this.c = context;
    }

    public void onWater(TextView status, EditText latInput, EditText longInput) {
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
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        if(locations.size() == 0) {
                            Location l = new Location("Location");
                            l.setLatitude(location.getLatitude());
                            l.setLongitude(location.getLongitude());
                            locations.add(l);
                        }
                        // if locations array size is not 0 and the previous location is not the same, then we add it to the array.
                        if (locations.size() != 0 && locations.get(locations.size() - 1).getLatitude() != location.getLatitude() && locations.get(locations.size() - 1).getLongitude() != location.getLongitude()) {
                            Location l = new Location("Location");
                            l.setLatitude(location.getLatitude());
                            l.setLongitude(location.getLongitude());
                            locations.add(l);
                        }
                        System.out.println("Current Location: " + location.getLatitude() + " " + location.getLongitude());
                        System.out.println("Last Array Location: " + locations.get(locations.size() - 1).getLatitude() + " " + locations.get(locations.size() - 1).getLongitude());
                    }
                 //  System.out.println("Longitude callback: " + location.getLongitude());
                   // System.out.println("Latitude callback: " + location.getLatitude());
                   // System.out.println("LocationResults.getLocations() length: " + locationResult.getLocations().size());
                    // System.out.println(locations.size());
                    System.out.println(locations.size());
                    isOnWaterRequest(status, location.getLongitude(), location.getLatitude());

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
                                    status.setText("Response is: You are on water!");
                                } else if (responseObject.getString("water").equals("false")) {
                                    status.setText("Response is: You are not on water!");
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

}
