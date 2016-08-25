package com.shivaraj.friendz.shivaraj;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.shivaraj.friendz.shivaraj.geofire.GeoLocation;

/**
 * Created by SYS on 17-Jul-2016.
 */

public class PokemongoApplication extends Application {

    private static GeoLocation mCurrentLoc;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public static GeoLocation getCurrentLocation() {
        if (mCurrentLoc != null) {
            return mCurrentLoc;
        } else {
            mCurrentLoc = new GeoLocation(37.7789,-122.4017);
            return mCurrentLoc;
        }
    }

    public static void setmCurrentLoc(GeoLocation mCurrentLoc) {
        PokemongoApplication.mCurrentLoc = mCurrentLoc;
    }
}
