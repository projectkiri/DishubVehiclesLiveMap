package travel.kiri.dishubvehicleslivemap;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import travel.kiri.dishubvehicleslivemap.models.VehicleInfo;
import travel.kiri.dishubvehicleslivemap.protocols.DataIdRetriever;

public class LiveMapActivity extends FragmentActivity implements DataIdRetriever.DataIdReadyHandler {

    private static final LatLng MAP_CENTER = new LatLng(-6.91474,107.60981);
    private static final int MAP_ZOOM = 12;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Map<String, Marker> mMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_map);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(MAP_CENTER, MAP_ZOOM)));
        mMarkers = new HashMap<>();
        DataIdRetriever retriever = new DataIdRetriever();
        retriever.retrieveVehiclesInfo(this);
    }

    @Override
    public void dataIdReady(List<VehicleInfo> vehicles) {
        if (mMap == null) {
            return;
        }
        if (vehicles == null) {
            Toast.makeText(this, "Connection error!", Toast.LENGTH_SHORT).show();
            return;
        }
        for (VehicleInfo vehicle: vehicles) {
            Marker marker = mMarkers.get(vehicle.getUniqueId());
            if (marker == null) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(vehicle.getLatitude(), vehicle.getLongitude()));
                markerOptions.title(vehicle.getName());
                markerOptions.rotation((float) vehicle.getDirection());
                mMap.addMarker(markerOptions);
            } else {
                marker.setPosition(new LatLng(vehicle.getLatitude(), vehicle.getLongitude()));
                marker.setTitle(vehicle.getName());
                marker.setRotation((float)vehicle.getDirection());
            }
        }
    }
}
