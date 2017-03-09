package com.example.a21753725a.geogallery;
import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;

public class MyInfoWindow extends BasicInfoWindow {
    private Context mContext;
    private String imagePath;

    public MyInfoWindow(Context context, MapView mapView, String image) {
        super(R.layout.info_window, mapView);
        mContext = context;
        imagePath = image;
    }

    @Override
    public void onOpen(Object item) {
        //super.onOpen(item);
        ImageView imageView = (ImageView) mView.findViewById(R.id.infoImg);

            Glide.with(mContext)
                    .load(imagePath)
                    .centerCrop()
                    .into(imageView);

    }
}
