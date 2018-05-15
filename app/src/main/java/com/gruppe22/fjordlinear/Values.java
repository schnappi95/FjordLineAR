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

    static final float GEOFENCE_RADIUS_IN_METERS_2000 = 4000; // i meter
    static final float GEOFENCE_RADIUS_IN_METERS_1000 = 2500; // i meter
    static final float GEOFENCE_RADIUS_IN_METERS_750 = 1500; // i meter

    static final HashMap<String, LatLng> RADIUS2000 = new HashMap<>();
    static final HashMap<String, LatLng> RADIUS1000 = new HashMap<>();
    static final HashMap<String, LatLng> RADIUS750 = new HashMap<>();


    static {
        // Nonnester bybanestopp
        RADIUS2000.put("LEIRVIK2000", new LatLng(59.781213, 5.502699));

        // Florida bybanestopp
        RADIUS2000.put("VESTVIK2000", new LatLng(59.654590,5.406608));

        // Kronstad bybanestopp
        RADIUS2000.put("FOYNO2000", new LatLng(59.295175,5.308190));

        // Nonnester bybanestopp
        RADIUS1000.put("SOTRABRO1000", new LatLng(60.363586, 5.167666));

        // Florida bybanestopp
        RADIUS1000.put("SANDVIKVAAG1000", new LatLng(59.969372,5.335704));

        // Kronstad bybanestopp
        RADIUS1000.put("FYRTAARN1000", new LatLng(59.528925,5.221361));

        // Kronstad bybanestopp
        RADIUS750.put("JEKTEVIK750", new LatLng(59.886395,5.521448));

        // Kronstad bybanestopp
        RADIUS750.put("DJUPASTO750", new LatLng(59.387447,5.295499));

        // Kronstad bybanestopp
        RADIUS1000.put("OLAVSKIRKEN1000", new LatLng(59.356540,5.290198));


    }
}