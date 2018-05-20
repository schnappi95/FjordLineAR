package com.gruppe22.fjordlinear;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by Schnappi on 16.03.2018.
 */

public class Values {

    /*
    // plaseringen til HIB
    static final double lengdegrad = 60.369128;
    static final double breddegrad = 5.350908;

    // fence radius i meter
    static final float radius = 500;
    */

    private Values() {
    }

    /*
    private static final String PACKAGE_NAME = "com.gruppe22.fjordlinear";
    static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    */

    static final float GEOFENCE_RADIUS_IN_METERS_2000 = 150; // i meter
    //static final float GEOFENCE_RADIUS_IN_METERS_1000 = 2500; // i meter
    //static final float GEOFENCE_RADIUS_IN_METERS_750 = 1500; // i meter

    static final HashMap<String, LatLng> RADIUS2000 = new HashMap<>();
    //static final HashMap<String, LatLng> RADIUS1000 = new HashMap<>();
    //static final HashMap<String, LatLng> RADIUS750 = new HashMap<>();


    static {
        // Nonnester bybanestopp
        RADIUS2000.put("kronstad", new LatLng(60.367993, 5.350484));

        // Florida bybanestopp
        RADIUS2000.put("kiwi", new LatLng(60.371376,5.347332));



    }
}