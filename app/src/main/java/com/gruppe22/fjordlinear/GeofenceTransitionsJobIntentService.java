package com.gruppe22.fjordlinear;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Handler;
import android.support.v4.app.JobIntentService;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * Created by Schnappi on 16.03.2018.
 */

public class GeofenceTransitionsJobIntentService extends JobIntentService {

    Handler mHandler;

    String sted = "Hø?";

    public GeofenceTransitionsJobIntentService(){
        mHandler = new Handler();
    }

    private static final String TAG = "GeoTraJobIntentService";

    private static final int JOB_ID = 573;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, GeofenceTransitionsJobIntentService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(Intent intent)
    {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError())
        {
            Log.e(TAG, "Feil i onHandleWork: geofencingEvent.hasError() ");
            return;
        }

        // henter transitiontype
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // tester om transitiontypen er av interesse
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
        {

            // finner avløst geofence i en liste.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();


            for (int i = 0; i < triggeringGeofences.size(); i++) {
                sted = triggeringGeofences.get(i).getRequestId();
                Log.i(TAG, "TriggeringGeofences: " + sted + geofenceTransition);
            }



            if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
                mHandler.post(new DisplayToast(this, "Hei " + sted));
                setInformajson(sted);

                Log.i(TAG, "onHandleWork: enter" + sted + geofenceTransition);
            } else {
                mHandler.post(new DisplayToast(this, "Hade " + sted));
                setInformajson("");
                Log.i(TAG, "onHandleWork: exit" + sted + geofenceTransition);
            }


        } else
            Log.e(TAG, "Geofence Transition Type er feil" + geofenceTransition);
    }


    // metode for å endre plaseringen i SharedPreferances
    public void setInformajson(String text) {
        SharedPreferences sharedPreferences = getSharedPreferences("informasjon", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("plasering", text);
        editor.apply();
    }

}

