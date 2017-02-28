package com.example.a21753725a.geogallery;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
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

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileFragment extends Fragment {
    View view;
    MapView map;

    private final int PERMISSION_LOCATION_REQUEST_CODE = 8;
    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);



        map = (MapView) view.findViewById(R.id.map);
        initializeMap();
        return view;
    }

    private void initializeMap() {
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setTilesScaledToDpi(true);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();



        LocationManager lm = (LocationManager) this.getContext().getSystemService(this.getContext().LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE},  PERMISSION_LOCATION_REQUEST_CODE);

            return;
        }
        try {
            mapController.setZoom(20);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            mapController.setCenter(new GeoPoint(latitude,longitude));
        }catch (Exception e){

        }
    }
}
