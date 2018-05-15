package com.gruppe22.fjordlinear;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity /*implements OnCompleteListener<Void>*/ {

    //final TextView textViewToChange = (TextView) findViewById(R.id.textView2);

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE_LOCATION = 99;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE_CAMERA = 100;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE_WRITE = 101;

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

                setInformajson("");
                endreText();
                //skrur av removeButton
                removeGeofenceButton.setEnabled(false);
                addGeofenceButton.setEnabled(true);
            }
        });

        // oppdaterer teksten i informajsonstavlen gjevnlig
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                endreText();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, "InterruptedException i informasjonstavle thread");
                }
            }
        };

        t.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeGeofence();
        setInformajson("");
        endreText();

    }


    /*
    @Override
    protected void onStart() {
        super.onStart();
        endreText();
    }
    */


    /*
    @Override
    protected void onStop() {
        super.onStop();
        endreText();
    } */

    private void createGeoFence() {
        for (Map.Entry<String, LatLng> entry1 : Values.RADIUS1000.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this geofence
                    .setRequestId(entry1.getKey())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry1.getValue().latitude,
                            entry1.getValue().longitude,
                            Values.GEOFENCE_RADIUS_IN_METERS_1000
                    )

                    .setExpirationDuration(Geofence.NEVER_EXPIRE)


                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)

                    // Create the geofence.
                    .build());
        }

        for (Map.Entry<String, LatLng> entry5 : Values.RADIUS750.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this geofence
                    .setRequestId(entry5.getKey())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry5.getValue().latitude,
                            entry5.getValue().longitude,
                            Values.GEOFENCE_RADIUS_IN_METERS_750
                    )

                    .setExpirationDuration(Geofence.NEVER_EXPIRE)


                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)

                    // Create the geofence.
                    .build());
        }

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
                        Log.i(TAG, "removeGeofence: successe");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "removeGeofence: fail");
                    }
                });
    }


    // endrer informajsonen i fragmentet til den nye plaseringen (håper jeg :))
    public void endreText() {

        String text = "";

        TextView tv1 = (TextView)findViewById(R.id.textView2);


        switch (hentInformasjon()){

            case "DJUPASTO750":
                text = "Her bor schnappien, 1500m";
                break;

            case "OLAVSKIRKEN1000":
                text = "Detta kalles fødestedet til Norge, 2500m";
                break;

            case "FOYNO2000":
                text = "Detta e bare kreft, 4000m";
                break;

            case "LEIRVIK2000":
                text = "Leirvik, 4000m";
                break;

            case "VESTVIK2000":
                text = "Vestvik fiskeoppdrett, 4000m";
                break;

            case "SOTRABRO1000":
                text = "Sotrabroen, 2500m";
                break;

            case "SANDVIKVAAG1000":
                text = "Sandvikvåg ferjekai, 2500m";
                break;

            case "FYRTAARN1000":
                text = "Fyrtårn, 2500m";
                break;

            case "JEKTEVIK750":
                text = "Jektevik ferjekai, 1500m";
                break;
        }

        tv1.setText(text);
    }

    public void setInformajson(String text)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("informasjon", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("plasering", text);
        editor.apply();
    }

    public String hentInformasjon()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("informasjon", Context.MODE_PRIVATE);

        return sharedPreferences.getString("plasering", "");

    }

}
