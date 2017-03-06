package com.example.a21753725a.geogallery;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileFragment extends Fragment {
    View view;
    MapView map;

    private final int STORAGE_PERM = 9;
    private final int PERMISSION_LOCATION_REQUEST_CODE = 8;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        //check storage permission
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERM);
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_LOCATION_REQUEST_CODE);
        }

        map = (MapView) view.findViewById(R.id.map);
        initializeMap();
        setCurrentLocation();
        return view;
    }

    private void initializeMap() {
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setTilesScaledToDpi(true);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
    }

    private void setCurrentLocation() {
        // Get the location manager
        LocationManager locationManager = (LocationManager)
                getContext().getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_LOCATION_REQUEST_CODE);
            return;
        }
        Location location = locationManager.getLastKnownLocation(bestProvider);
        LocationListener loc_listener = new LocationListener() {

            public void onLocationChanged(Location l) {}

            public void onProviderEnabled(String p) {}

            public void onProviderDisabled(String p) {}

            public void onStatusChanged(String p, int status, Bundle extras) {}
        };
        locationManager
                .requestLocationUpdates(bestProvider, 0, 0, loc_listener);
        location = locationManager.getLastKnownLocation(bestProvider);
        IMapController mapController = map.getController();
        double lat;
        double lon;
        try {
            lat = location.getLatitude();
            lon = location.getLongitude();

        } catch (NullPointerException e) {
            lat = -1.0;
            lon = -1.0;
        }
        mapController.setZoom(20);
        mapController.setCenter(new GeoPoint(lat,lon));
    }
}
