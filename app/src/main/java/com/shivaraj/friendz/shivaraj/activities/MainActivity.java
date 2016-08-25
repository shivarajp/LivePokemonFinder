package com.shivaraj.friendz.shivaraj.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.messaging.FirebaseMessaging;
import com.shivaraj.friendz.R;
import com.shivaraj.friendz.shivaraj.fragment.AllPostsAroundFragment;
import com.shivaraj.friendz.shivaraj.fragment.MyTopPostsFragment;
import com.shivaraj.friendz.shivaraj.fragment.RecentPostsFragment;
import com.shivaraj.friendz.shivaraj.utils.Constants;
import com.shivaraj.friendz.shivaraj.utils.PokemonSharedPreferencesManager;
import com.shivaraj.friendz.views.LoginActivity;
import com.shivaraj.friendz.views.settings.SettingsActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static com.shivaraj.friendz.shivaraj.utils.Constants.ADDRESS;
import static com.shivaraj.friendz.shivaraj.utils.Constants.ANALYTICS_MAINACTIVITY;
import static com.shivaraj.friendz.shivaraj.utils.Constants.ANALYTICS_MAP;
import static com.shivaraj.friendz.shivaraj.utils.Constants.DISTANCE;
import static com.shivaraj.friendz.shivaraj.utils.Constants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS;
import static com.shivaraj.friendz.shivaraj.utils.Constants.LATTITUDE;
import static com.shivaraj.friendz.shivaraj.utils.Constants.LONGITUDE;
import static com.shivaraj.friendz.shivaraj.utils.Constants.MY_PERMISSIONS_REQUEST_LOCATION;
import static com.shivaraj.friendz.shivaraj.utils.Constants.PLACE_PICKER_REQUEST;
import static com.shivaraj.friendz.shivaraj.utils.Constants.REQUEST_LOCATION;
import static com.shivaraj.friendz.shivaraj.utils.Constants.UPDATE_INTERVAL_IN_MILLISECONDS;

public class MainActivity extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener,
        NavigationView.OnNavigationItemSelectedListener {


    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private LocationRequest mLocationRequest;
    private PendingResult<LocationSettingsResult> result;
    private GoogleApiClient mGoogleApiClient;
    FirebaseAnalytics mFirebaseAnalytics;
    private InterstitialAd mInterstitialAd;

    /*private AdView mAdView;*/

    private TextView name;
    private TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String from = getIntent().getStringExtra("from");
        if (from != null && from.equals("signin")) {
            Intent intent = new Intent(this, com.shivaraj.friendz.views.LoginActivity.class);
            startActivity(intent);
        }

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                FirebaseCrash.log("Uncought Crash " + e.toString());
                e.printStackTrace();
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        setContentView(R.layout.activity_main_new);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setIcon(R.drawable.notification);
        buildGoogleApiClient();
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(Constants.INTERSTITIAL_NEWPOST_ID);
        requestNewInterstitial();
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                // beginSecondActivity();
            }
        });

        /*mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        FirebaseMessaging.getInstance().subscribeToTopic("posts");
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(ANALYTICS_MAINACTIVITY, "MainActivity opened");
        mFirebaseAnalytics.logEvent(ANALYTICS_MAP, bundle);

        /*toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChatActivity();
            }
        });*/

        // Create the adapter that will return a fragment for each section
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[]{
                    new RecentPostsFragment(),
                    new AllPostsAroundFragment(),
                    new MyTopPostsFragment(),
                    //new MapPostsFragment(),
            };
            private final String[] mFragmentNames = new String[]{
                    getResources().getString(R.string.today_lbl),
                    getResources().getString(R.string.all_posts_lbl),
                    getResources().getString(R.string.my_posts_lbl),
            };

            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        //tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        //tabLayout.setSelectedTabIndicatorHeight(5);
        tabLayout.setupWithViewPager(mViewPager);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);

        name = (TextView) hView.findViewById(R.id.userNamesTv);
        email = (TextView) hView.findViewById(R.id.userEmailTv);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            name.setText(user.getDisplayName());
            email.setText(user.getEmail());
        } else {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
        }

        // Button launches NewPostActivity
        findViewById(R.id.fab_new_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewPostActivity();
            }
        });

    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public void onPause() {
        /*if (mAdView != null) {
            mAdView.pause();
        }*/
        super.onPause();
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }

        mGoogleApiClient.connect();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                return true;
            case R.id.menu_refresh:
                startLocationUpdates();
                return true;
            /*case R.id.menu_map:
                startMapsActivity();
                return true;*/
            case R.id.action_chat:
                startChatActivity();
                return true;
            case R.id.action_changeloc:
                changeMyLocation();
                return true;
            case R.id.action_newpost:
                startNewPostActivity();
                return true;
            case R.id.menu_live_map:
                startLiveMapLoginActivity();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        PokemonSharedPreferencesManager.putBoolean(this, Constants.IS_FIRST_TIME, false);
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i("Building", "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
        mGoogleApiClient.connect();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        createLocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        startLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(MainActivity.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Toast.makeText(MainActivity.this, "Can't show settings", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });

    }

    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(getApplicationContext(), "Please allow permission to proceed", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    LocationServices.FusedLocationApi.requestLocationUpdates(
                            mGoogleApiClient, mLocationRequest, this);
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void changeMyLocation() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOCATION) {
            final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(getApplicationContext(), R.string.location_enabled, Toast.LENGTH_LONG).show();
                startLocationUpdates();
            } else {
                Toast.makeText(getApplicationContext(), "Loc is still off,", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                double lat = place.getLatLng().latitude;
                double lon = place.getLatLng().longitude;
                setMyLocationToSharePref((float) lat, (float) lon);
                setMyAddress(this, place.getAddress().toString());
                Location newLoc = new Location("");
                newLoc.setLatitude(lat);
                newLoc.setLongitude(lon);
                EventBus.getDefault().post(newLoc);

            }
        }
    }

    private void setMyLocationToSharePref(float lat, float lon) {
        PokemonSharedPreferencesManager.putFloat(this, LATTITUDE, lat);
        PokemonSharedPreferencesManager.putFloat(this, LONGITUDE, lon);
    }

    private String getMyAddress(Context context) {
        return PokemonSharedPreferencesManager.getString(this, ADDRESS);
    }

    private void setMyAddress(Context context, String address) {
        if (!address.equals(""))
            PokemonSharedPreferencesManager.putString(this, ADDRESS, address);
    }

    private LatLng getMyLocFromSharePref(Context context) {
        return new LatLng(PokemonSharedPreferencesManager.getFloat(context, LATTITUDE),
                PokemonSharedPreferencesManager.getFloat(context, LONGITUDE));
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        setMyLocationToSharePref((float) location.getLatitude(), (float) location.getLongitude());
        EventBus.getDefault().post(location);
    }

    @Subscribe
    public void updateLocationEvent(String isStartLoc) {
        Log.d("eventbuus", "stop event rcvd");
        if (isStartLoc.equals("start")) {
            startLocationUpdates();
        } else {
            stopLocationUpdates();
        }
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_chat) {
            Bundle bundle = new Bundle();
            bundle.putString("chat nav", "chat clicked");
            mFirebaseAnalytics.logEvent("chat clicked", bundle);

            //startActivity(new Intent(MainActivity.this, LivePokemonGoMap.class));
            startChatActivity();
        } else if (id == R.id.nav_change_loc) {
            Bundle bundle = new Bundle();
            bundle.putString("change loc", "change loc clicked");
            mFirebaseAnalytics.logEvent("change loc nav", bundle);
            changeMyLocation();
        } else if (id == R.id.nav_new_post) {
            Bundle bundle = new Bundle();
            bundle.putString("new post", "newpost clicked");
            mFirebaseAnalytics.logEvent("new post", bundle);
            startNewPostActivity();
        } else if (id == R.id.nav_share) {
            Bundle bundle = new Bundle();
            bundle.putString("Share app", "share app clicked");
            mFirebaseAnalytics.logEvent("Share app", bundle);
            shareApp();
        } else if (id == R.id.nav_contact_us) {
            emailUs();
        } else if (id == R.id.nav_post_distance) {

            //Show distance seek bar dialog
            chooseDistanceDialog();
        } else if (id == R.id.nav_post_map) {
            startMapsActivity();
        } else if (id == R.id.nav_logout) {
            logout();
        } else if (id == R.id.nav_live_map) {
            //this will check map & load accordingly
            startLiveMapLoginActivity();
        }

        /*else if (id == R.id.nav_) {
            startActivityForResult(new Intent(this, SettingsActivity.class), 0);
        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startLiveMapLoginActivity() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    private void emailUs() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL, "shivrajp130.apps@gmail.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Pokemon app Help");
        intent.putExtra(Intent.EXTRA_TEXT, "Write here..");
        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "https://play.google.com/store/apps/details?id=com.shivaraj.friendz");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void startMapsActivity() {
        startActivity(new Intent(MainActivity.this, MapsActivity.class));
    }

    private void chooseDistanceDialog() {

        Dialog dialog = new Dialog(this);

        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(5, 0, 0, 0);

        final TextView progressTv = new TextView(this);

        progressTv.setText(PokemonSharedPreferencesManager.getFloat(MainActivity.this, DISTANCE) + " Km's");

        final SeekBar seek = new SeekBar(this);
        linearLayout.addView(progressTv);
        linearLayout.addView(seek);
        seek.setMax(10000);
        seek.setKeyProgressIncrement(100);

        popDialog.setIcon(R.drawable.distance24);
        popDialog.setTitle(R.string.select_distance_title);
        popDialog.setView(linearLayout);

        popDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        popDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                dialog.cancel();
            }
        });

        seek.setProgress((int) (PokemonSharedPreferencesManager.getFloat(MainActivity.this, Constants.DISTANCE) * 1000));
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Do something when we get the progress
                if ((progress / 1000) > 1) {
                    float distanceKm = progress / 1000;
                    PokemonSharedPreferencesManager.putFloat(MainActivity.this, Constants.DISTANCE, distanceKm);
                    progressTv.setText(distanceKm + " Km's");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        AlertDialog alertDialog = popDialog.create();
        alertDialog.show();
    }

    private void startChatActivity() {
        startActivity(new Intent(MainActivity.this, ChatActivity.class));
    }

    private void startNewPostActivity() {
        startActivity(new Intent(MainActivity.this, NewPostActivity.class));
    }

    /**
     * Called when returning to the activity
     */
    @Override
    public void onResume() {
        super.onResume();
       /* if (mAdView != null) {
            mAdView.resume();
        }*/
    }

    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        /*if (mAdView != null) {
            mAdView.destroy();
        }*/
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        finish();
    }
}
