package com.gruppe22.fjordlinear;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

/**
 * Created by Schnappi on 16.03.2018.
 */

public class GeofenceTransitionsJobIntentService extends JobIntentService {

    Handler mHandler;

    public GeofenceTransitionsJobIntentService() {
        mHandler = new Handler();
    }

    private static final String TAG = "GeoTraJobIntentService";

    private static final int JOB_ID = 573;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, GeofenceTransitionsJobIntentService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "Feil i onHandleWork: geofencingEvent.hasError() ");
            return;
        }

        // henter transitiontype
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // tester om transitiontypen er av interesse
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // finner avløst geofence. Går det ann å hente en uten List??
            //List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                mutePhone(this);
                mHandler.post(new DisplayToast(this, "Mute"));
                Log.e(TAG, "onHandleWork: enter" + geofenceTransition);
            } else {
                unmutePhone(this);
                mHandler.post(new DisplayToast(this, "Unmute"));
                Log.e(TAG, "onHandleWork: exit" + geofenceTransition);
            }

        } else
            Log.e(TAG, "Geofence Transition Type er feil" + geofenceTransition);
    }

    private void mutePhone(Context context) {

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }

    private void unmutePhone(Context context) {

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

    }


}