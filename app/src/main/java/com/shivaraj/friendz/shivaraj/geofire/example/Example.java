package com.shivaraj.friendz.shivaraj.geofire.example;/*
package com.firebase.com.pokemongo.friendz.shivaraj.geofire.example;

import GeoFire;
import GeoLocation;
import GeoQuery;
import GeoQueryEventListener;

public class Example {

    public static void main(String[] args) throws InterruptedException {
        DatabaseReference firebase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://geofire-v3.firebaseio.com/geofire");
        GeoFire geoFire = new GeoFire(firebase);
        GeoQuery query = geoFire.queryAtLocation(new GeoLocation(37.7, -122.4), 10);
        query.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                System.out.println(String.format("%s entered at [%f, %f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onKeyExited(String key) {
                System.out.println(String.format("%s exited", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.println(String.format("%s moved to [%f, %f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {
                System.out.println("All initial key entered events have been fired!");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error querying locations: " + error.getMessage());
            }
        });
        // run for another 60 seconds
        Thread.sleep(60000);
    }
}
*/
