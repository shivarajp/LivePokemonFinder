package com.shivaraj.friendz.shivaraj.activities;/*
package com.pokemongo.friendz.shivaraj.activities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.EncounterResult;
import com.pokegoapi.auth.GoogleAuthJson;
import com.pokegoapi.auth.GoogleAuthTokenJson;
import com.pokegoapi.auth.GoogleCredentialProvider;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.examples.ExampleLoginDetails;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.util.Log;
import com.pokemongo.friendz.R;
import com.pokemongo.friendz.shivaraj.asynctasks.AsyncGoogleCredentialprovider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Iterator;
import java.util.List;

import okhttp3.OkHttpClient;

public class LivePokemonGoMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_pokemon_go_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

       */
/* AsyncGoogleCredentialprovider googleCredentialprovider = new AsyncGoogleCredentialprovider();
        googleCredentialprovider.execute("");*//*


        OkHttpClient httpClient = new OkHttpClient();

        GoogleUserCredentialProvider provider = new GoogleUserCredentialProvider(httpClient);

        */
/*

        try {
            PokemonGo go = new PokemonGo(new GoogleCredentialProvider(httpClient, new GoogleLoginListener()), httpClient);
        } catch (LoginFailedException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "login exception" + e, Toast.LENGTH_LONG).show();
        } catch (RemoteServerException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "remote exc" + e, Toast.LENGTH_LONG).show();
        }*//*



    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void gotPokemonGo(PokemonGo go) {
        Toast.makeText(getApplicationContext(), "go " + go.getPlayerProfile(), Toast.LENGTH_LONG).show();
    }

    */
/**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     *//*

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
*/
