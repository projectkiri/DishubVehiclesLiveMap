package travel.kiri.dishubvehicleslivemap.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by PascalAlfadian on 30/6/2015.
 */
public class VehicleInfo {
    private String uniqueId;
    private String name;
    private double latitude;
    private double longitude;
    private double direction;
    private boolean gpsActive;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isGpsActive() {
        return gpsActive;
    }

    public void setGpsActive(boolean gpsActive) {
        this.gpsActive = gpsActive;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDirection() {
        return direction;
    }

    public void setDirection(double direction) {
        this.direction = direction;
    }
}
