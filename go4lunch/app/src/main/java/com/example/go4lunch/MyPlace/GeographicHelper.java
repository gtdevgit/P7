package com.example.go4lunch.MyPlace;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.RectangularBounds;

public class GeographicHelper {
    private static final double EARTHRADIUS = 6366198;

    public static RectangularBounds createRectangularBounds(LatLng origine, double halfside) {
        LatLng northEast = GeographicHelper.move(origine, halfside, halfside);
        LatLng southWest = GeographicHelper.move(origine, -halfside, -halfside);
        // Create a RectangularBounds object.
        return  RectangularBounds.newInstance(southWest, northEast);
    }
    /**
     * Create a new LatLng which lies toNorth meters north and toEast meters
     * east of startLL
     */
    private static LatLng move(LatLng startLL, double toNorth, double toEast) {
        double lonDiff = meterToLongitude(toEast, startLL.latitude);
        double latDiff = meterToLatitude(toNorth);
        return new LatLng(startLL.latitude + latDiff, startLL.longitude
                + lonDiff);
    }

    private static double meterToLongitude(double meterToEast, double latitude) {
        double latArc = Math.toRadians(latitude);
        double radius = Math.cos(latArc) * EARTHRADIUS;
        double rad = meterToEast / radius;
        return Math.toDegrees(rad);
    }

    private static double meterToLatitude(double meterToNorth) {
        double rad = meterToNorth / EARTHRADIUS;
        return Math.toDegrees(rad);
    }
}
