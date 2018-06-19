package com.gruppe22.fjordlinear;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE_LOCATION = 99;

    private ArrayList<Geofence> mGeofenceList;

    private static final String TAG = "MainActivity";

    private GeofencingClient mGeofencingClient;

    private PendingIntent mGeofencePendingIntent;

    private Button addGeofenceButton;
    private Button removeGeofenceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mGeofenceList = new ArrayList<>();

        createGeoFence();

        mGeofencePendingIntent = null;

        mGeofencingClient = LocationServices.getGeofencingClient(this);

        addGeofenceButton = findViewById(R.id.activateButton);
        removeGeofenceButton = findViewById(R.id.deactivateButton);

        removeGeofenceButton.setEnabled(false);

        // activate the geofence
        addGeofenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(
                            MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_PERMISSIONS_REQUEST_CODE_LOCATION);
                }

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    addGeofences();
                    Intent in = new Intent(getApplicationContext(), HelloArActivity.class);
                    startActivity(in);
                    removeGeofenceButton.setEnabled(true);
                    addGeofenceButton.setEnabled(false);
                }

            }
        });

        // deactivate the geofence
        removeGeofenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeGeofence();

                removeGeofenceButton.setEnabled(false);
                addGeofenceButton.setEnabled(true);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeGeofence();

    }


    /*
    @Override
    protected void onStart() {
        super.onStart();

    }
    */


    /*
    @Override
    protected void onStop() {
        super.onStop();

    } */

    private void createGeoFence() {

        // Viser hvordan vi legger til geofencene laget i Values.java
        // Skal man ha geofence med forskjellige størrelser, lager man en ny
        // HashMap (RADIUS2000) og ny float (GEOFENCE_RADIUS_IN_METERS_XXXX) i values.java
        // og kopierer HELE denne koden, helt fra FOR til BUILD, og endrer verdiene

        for (Map.Entry<String, LatLng> entry20 : Values.RADIUS2000.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this geofence
                    .setRequestId(entry20.getKey())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry20.getValue().latitude,
                            entry20.getValue().longitude,
                            Values.GEOFENCE_RADIUS_IN_METERS_2000
                    )

                    .setExpirationDuration(Geofence.NEVER_EXPIRE)


                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)

                    // Create the geofence.
                    .build());
        }

    }


    private GeofencingRequest getGeofencingRequest()
    {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);

        Log.i(TAG, "getGeofencingRequest");
        return builder.build();
    }


    private PendingIntent getGeofencePendingIntent()
    {
        if(mGeofencePendingIntent != null)
            return mGeofencePendingIntent;

        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        mGeofencePendingIntent = PendingIntent
                .getBroadcast(this,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.i(TAG, "getGeofencePendingIntent");
        return mGeofencePendingIntent;
    }


    @SuppressWarnings("MissingPermission")
    private void addGeofences()
    {
        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())

                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "addGeofence: success!");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "addGeofence: FAIL!");
                    }
                });
    }


    private void removeGeofence()
    {
        mGeofencingClient.removeGeofences(getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "removeGeofence: success");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "removeGeofence: fail");
                    }
                });
    }

}
