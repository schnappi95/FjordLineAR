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
    private ImageButton cameraButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        mGeofenceList = new ArrayList<>();

        createGeoFence();


        mGeofencePendingIntent = null;

        mGeofencingClient = LocationServices.getGeofencingClient(this);

        fuckingsLydlosPermission();


        addGeofenceButton = findViewById(R.id.activateButton);
        removeGeofenceButton = findViewById(R.id.deactivateButton);
        cameraButton = findViewById(R.id.cameraButton);
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
                //skrur av removeButtonm
                removeGeofenceButton.setEnabled(false);
                addGeofenceButton.setEnabled(true);
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(
                            MainActivity.this, new String[]{Manifest.permission.CAMERA},
                            REQUEST_PERMISSIONS_REQUEST_CODE_CAMERA);
                }

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(
                            MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_PERMISSIONS_REQUEST_CODE_WRITE);
                }

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED) {

                        Intent i = new Intent(getApplicationContext(), CameraActivity.class);
                        startActivity(i);
                }
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
        for (Map.Entry<String, LatLng> entry : Values.HOLDEPLASSER.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this geofence?????????????
                    .setRequestId(entry.getKey())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Values.GEOFENCE_RADIUS_IN_METERS
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

    private void fuckingsLydlosPermission(){
        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            startActivity(intent);
        }
    }

    private void skruPåLyd(){

        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        Toast toast = Toast.makeText(this, "Unmute", Toast.LENGTH_SHORT);
        toast.show();

    }



    // endrer informajsonen i fragmentet til den nye plaseringen (håper jeg :))
    public void endreText() {

        String text = "";

        TextView tv1 = (TextView)findViewById(R.id.textView2);


        switch (hentInformasjon()){

            case "NONNESETER":
                text = "Nonneseter kloster var et kloster i Bergen i middelalderen. " +
                        "Det finnes fremdeles to rester av klosteret mellom Lille og Store Lungegårdsvann. " +
                        "Tårnfoten og søndre korkapell ligger gjemt mellom flere nyere bygg langs Kaigaten.";
                break;

            case "FLORIDA":
                text = "Florida er én av syv grunnkretser i strøket Nygård i Bergen sentrum. " +
                        "Her ligger St. Paul gymnas, tidligere Florida sykehus, og bybanestoppet Florida.\n" +
                        "Navnet Florida knyttes til eiendommen som huser Geofysisk Institutt ved Universitetet i Bergen. " +
                        "Her finnes den offisielle værstasjonen (målepunkt) for Bergen.\n";
                break;

            case "KRONSTAD":
                text = "Kronstad er et boligområde i Årstad bydel i Bergen like sør for byens sentrum, " +
                        "som strekker seg fra Store Lungegårdsvann i nord og over Kronstadhøyden til Nymark ved Brann Stadion i sør, " +
                        "og fra Haukeland universitetssykehus og Møllendal gravplass i øst til Danmarks plass (tidligere Kronstadtorget) " +
                        "og Solheimsvannet i vest.";
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
        String name = sharedPreferences.getString("plasering", "");

        return name;
    }

}
