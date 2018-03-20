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

    static final float GEOFENCE_RADIUS_IN_METERS = 300; // i meter


    static final HashMap<String, LatLng> HOLDEPLASSER = new HashMap<>();

    static {
        // Nonnester bybanestopp
        HOLDEPLASSER.put("NONNESETER", new LatLng(60.39013, 5.332753));

        // Florida bybanestopp
        HOLDEPLASSER.put("FLORIDA", new LatLng(60.381609,5.333092));

        // Kronstad bybanestopp
        HOLDEPLASSER.put("KRONSTAD", new LatLng(60.370616,5.348073));
    }
}