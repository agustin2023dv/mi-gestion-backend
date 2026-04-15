package com.migestion.shared.util;

/**
 * Utility class for geolocation calculations.
 * Provides methods for distance calculation using the Haversine formula
 * and radius-based proximity checks.
 */
public final class GeolocationUtils {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double DEFAULT_RADIUS_KM = 0.1; // 100 meters

    private GeolocationUtils() {
        // Utility class
    }

    /**
     * Calculates the great-circle distance between two points on Earth
     * using the Haversine formula.
     *
     * @param lat1 Latitude of the first point in degrees
     * @param lon1 Longitude of the first point in degrees
     * @param lat2 Latitude of the second point in degrees
     * @param lon2 Longitude of the second point in degrees
     * @return Distance in kilometers
     */
    public static double calcularDistanciaKm(double lat1, double lon1, double lat2, double lon2) {
        double latRad1 = Math.toRadians(lat1);
        double latRad2 = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(latRad1) * Math.cos(latRad2)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Checks if the distance between two points is within the default radius threshold (0.1 km = 100m).
     *
     * @param lat1 Latitude of the first point in degrees
     * @param lon1 Longitude of the first point in degrees
     * @param lat2 Latitude of the second point in degrees
     * @param lon2 Longitude of the second point in degrees
     * @return true if distance is <= 0.1 km, false otherwise
     */
    public static boolean isWithinRadius(double lat1, double lon1, double lat2, double lon2) {
        return isWithinRadius(lat1, lon1, lat2, lon2, DEFAULT_RADIUS_KM);
    }

    /**
     * Checks if the distance between two points is within a specified radius threshold.
     *
     * @param lat1       Latitude of the first point in degrees
     * @param lon1       Longitude of the first point in degrees
     * @param lat2       Latitude of the second point in degrees
     * @param lon2       Longitude of the second point in degrees
     * @param radiusKm   Threshold radius in kilometers
     * @return true if distance is <= radiusKm, false otherwise
     */
    public static boolean isWithinRadius(double lat1, double lon1, double lat2, double lon2, double radiusKm) {
        double distanceKm = calcularDistanciaKm(lat1, lon1, lat2, lon2);
        return distanceKm <= radiusKm;
    }
}
