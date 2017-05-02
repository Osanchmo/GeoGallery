package com.example.a21753725a.geogallery;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    View view;
    MapView map;
    GpsTracker gps;
    String mCurrentPhotoPath;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference photoRef = database.getReference("photos");
    DatabaseReference profPicRef = database.getReference("profilePic");
    DatabaseReference bannerRef = database.getReference("bannerPic");
    RadiusMarkerClusterer poiMarkers;
    ImageView profilePic;
    ImageView bannerPic;
    Boolean isProf;

    private static final int ACTIVITAT_SELECCIONAR_IMATGE = 2;
    static final int REQUEST_TAKE_PHOTO = 1;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        gps  = new GpsTracker(this.getContext());
        map = (MapView) view.findViewById(R.id.map);
        profilePic = (ImageView) view.findViewById(R.id.profileImg);
        bannerPic = (ImageView) view.findViewById(R.id.bgProfile);
        poiMarkers = new RadiusMarkerClusterer(getActivity());
        ImageView bannerBorder = (ImageView) view.findViewById(R.id.bannerBG);
        ImageView profileBorder = (ImageView) view.findViewById(R.id.profileBG);
        Glide.with(getActivity()).load("http://i.imgur.com/ErNyFNv.jpg").bitmapTransform(
                (new CropCircleTransformation(getContext()))).into(profileBorder);
        Glide.with(getActivity()).load("http://i.imgur.com/ErNyFNv.jpg").into(bannerBorder);

        initializeMap();
        setCurrentLocation();
        LoadPhotoMarkers();

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        profilePic.setClickable(true);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isProf = true;
                selectImage();
            }
        });

        bannerPic.setClickable(true);
        bannerPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isProf = false;
                selectImage();
            }
        });
        return view;
    }

    private void dispatchTakePictureIntent() {
        setCurrentLocation();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                Photo photo = new Photo();
                photo.setLat(gps.getLatitude());
                photo.setLon(gps.getLongitude());
                photo.setPath(mCurrentPhotoPath);
                addPhoto(photo);
            }
        }
    }

    private File createImageFile() throws Exception {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();

        return image;
    }

    private void initializeMap() {
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setTilesScaledToDpi(true);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
    }

    private void setCurrentLocation() {


        IMapController mapController = map.getController();
        double lat;
        double lon;
        try {
            lat = gps.getLatitude();
            lon = gps.getLongitude();
        } catch (Exception e) {
            lat = -1.0;
            lon = -1.0;
        }
        mapController.setZoom(20);
        mapController.setCenter(new GeoPoint(lat,lon));
    }
    public void addPhoto(Photo photo){

        photoRef.child("pictures").push().setValue(photo);
    }

    public void  LoadPhotoMarkers(){
        // Read from the database
        photoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Drawable d = getResources().getDrawable(R.drawable.ic_photo_camera);
                for (DataSnapshot postSnapshot: dataSnapshot.child("pictures").getChildren()) {
                 Photo photo = postSnapshot.getValue(Photo.class);
                    Marker startMarker = new Marker(map);
                    startMarker.setIcon(d);
                    startMarker.setPosition(new GeoPoint(photo.getLat(),photo.getLon()));
                    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    startMarker.setInfoWindow(new MyInfoWindow(getContext(),map,photo.getPath()));
                    poiMarkers.add(startMarker);
                }

                map.getOverlays().add(poiMarkers);
                map.invalidate();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                error.toException().printStackTrace();
            }
        });
        profPicRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    ProfilePic pic = dataSnapshot.getValue(ProfilePic.class);
                try {
                    Glide.with(getActivity()).load(pic.getPath()).centerCrop().bitmapTransform(
                            (new CropCircleTransformation(getContext()))).into(profilePic);
                }catch (Exception e){
                    Glide.with(getActivity()).load("https://x1.xingassets.com/assets/frontend_minified/img/users/nobody_m.original.jpg").centerCrop().bitmapTransform(
                            (new CropCircleTransformation(getContext()))).into(profilePic);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        bannerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Banner banner  = dataSnapshot.getValue(Banner.class);
                try {
                    Glide.with(getActivity()).load(banner.getPath()).centerCrop().into(bannerPic);
                }catch (Exception e){
                    Glide.with(getActivity()).load("https://x1.xingassets.com/assets/frontend_minified/img/users/nobody_m.original.jpg").centerCrop().bitmapTransform(
                            (new CropCircleTransformation(getContext()))).into(profilePic);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void selectImage(){
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
        startActivityForResult(i, ACTIVITAT_SELECCIONAR_IMATGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case ACTIVITAT_SELECCIONAR_IMATGE:
                if (resultCode == RESULT_OK) {
                    Uri seleccio = intent.getData();
                    String[] columna = {MediaStore.Images.Media.DATA};

                    Cursor cursor = this.getActivity().getContentResolver().query(
                            seleccio, columna, null, null, null);
                    cursor.moveToFirst();

                    int indexColumna = cursor.getColumnIndex(columna[0]);
                    if(isProf){
                        ProfilePic pic = new ProfilePic();
                        pic.setPath(cursor.getString(indexColumna));
                        pic.setName("test");
                        profPicRef.setValue(pic);
                    }else{
                        Banner pic = new Banner();
                        pic.setPath(cursor.getString(indexColumna));
                        bannerRef.setValue(pic);
                    }
                    cursor.close();
                }
        }
    }
}
