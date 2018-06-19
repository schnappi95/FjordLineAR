package com.gruppe22.fjordlinear;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by Schnappi on 16.03.2018.
 */

public class Values {


    private Values() {
    }

    /*
    private static final String PACKAGE_NAME = "com.gruppe22.fjordlinear";
    static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    */

    // Radiusene vi har brukt.
    // Etter masse testing, anbefaler vi dere å bruke minst 5000 meter ute på sjøen
    static final float GEOFENCE_RADIUS_IN_METERS_2000 = 2000;
    //static final float GEOFENCE_RADIUS_IN_METERS_1000 = 1000;
    //static final float GEOFENCE_RADIUS_IN_METERS_750 = 750;

    static final HashMap<String, LatLng> RADIUS2000 = new HashMap<>();
    //static final HashMap<String, LatLng> RADIUS1000 = new HashMap<>();
    //static final HashMap<String, LatLng> RADIUS750 = new HashMap<>();


    static {

        // Definerer koordinatene til geofence

        //RADIUS2000.put("kronstad", new LatLng(60.369128, 5.350908));

        RADIUS2000.put("Fjord Line", new LatLng(60.392436, 5.309189));

        //RADIUS2000.put("kiwi", new LatLng(60.371376,5.347332));



    }
}