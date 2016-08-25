package com.shivaraj.friendz.shivaraj.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.shivaraj.friendz.R;

import static com.shivaraj.friendz.shivaraj.utils.Constants.ANALYTICS_MAP;

public class ShowPostMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static final String EXTRA_LOC_KEY = "loc_key";
    public static final String EXTRA_TITLE_KEY = "title_key";
    String mLocString;
    String mTitle;
    private LatLng latLng;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_post_map);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mLocString = getIntent().getStringExtra(EXTRA_LOC_KEY);
        mTitle = getIntent().getStringExtra(EXTRA_TITLE_KEY);

        if (mLocString != null) {
            String[] latlonString = mLocString.split(",");
            latLng = new LatLng(Double.parseDouble(latlonString[0]),
                    Double.parseDouble(latlonString[1]));
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Bundle bundle = new Bundle();
        bundle.putString(ANALYTICS_MAP,"Map loaded");
        mFirebaseAnalytics.logEvent(ANALYTICS_MAP,bundle);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);

        if (latLng != null) {
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(mTitle);
            Marker marker = mMap.addMarker(markerOptions);

            try {
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(latLng, latLng), 5));
            } catch (IllegalStateException exception) {
                exception.printStackTrace();
                try {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(latLng, latLng), 5));
                } catch (IllegalStateException exc) {
                    exc.printStackTrace();
                }
            }

            /*mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    try {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(latLng, latLng), 30));
                    } catch (IllegalStateException exception) {
                        exception.printStackTrace();
                    }
                }
            });*/
            //map.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(end, start), 20));
            //animateMarkerTo(marker, latLng.latitude, latLng.longitude);
        } else {
            mMap.addMarker(new MarkerOptions().position(sydney).title("Pokemon Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void animateMarkerTo(final Marker marker, final double lat, final double lng) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long DURATION_MS = 3000;
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final LatLng startPosition = marker.getPosition();
        handler.post(new Runnable() {
            @Override
            public void run() {
                float elapsed = SystemClock.uptimeMillis() - start;
                float t = elapsed / DURATION_MS;
                float v = interpolator.getInterpolation(t);

                double currentLat = (lat - startPosition.latitude) * v + startPosition.latitude;
                double currentLng = (lng - startPosition.longitude) * v + startPosition.longitude;
                marker.setPosition(new LatLng(currentLat, currentLng));

                // if animation is not finished yet, repeat
                if (t < 1) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }
}
