package com.shivaraj.friendz.shivaraj.asynctasks;/*
package com.pokemongo.friendz.shivaraj.asynctasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.auth.GoogleAuthJson;
import com.pokegoapi.auth.GoogleAuthTokenJson;
import com.pokegoapi.auth.GoogleCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokemongo.friendz.shivaraj.activities.LivePokemonGoMap;
import com.pokemongo.friendz.shivaraj.models.Post;

import org.greenrobot.eventbus.EventBus;

import okhttp3.OkHttpClient;

import static com.pokemongo.friendz.shivaraj.activities.LivePokemonGoMap.*;

*/
/**
 * Created by SYS on 23-Jul-2016.
 *//*


public class AsyncGoogleCredentialprovider extends AsyncTask<String, PokemonGo, PokemonGo> {

    PokemonGo go;

    @Override
    protected PokemonGo doInBackground(String... params) {
        OkHttpClient httpClient = new OkHttpClient();

        try {
            go = new PokemonGo(new GoogleCredentialProvider(httpClient, new GoogleLoginListener()), httpClient);
            return go;
        } catch (LoginFailedException e) {
            e.printStackTrace();
            Log.d("AsyncGoogleCre", "error" + e);
        } catch (RemoteServerException e) {
            e.printStackTrace();
            Log.d("AsyncGoogleCre", "error" + e);
        }


        return null;
    }

    public class GoogleLoginListener implements GoogleCredentialProvider.OnGoogleLoginOAuthCompleteListener {

        @Override
        public void onInitialOAuthComplete(GoogleAuthJson auth) {
            Log.d("asyncgoog", "authinit" + auth);
        }

        @Override
        public void onTokenIdReceived(GoogleAuthTokenJson tokens) {
            Log.d("asyncgoog", "authrcvd" + tokens);
            // refresh_token is accessible here if you want to store it.
        }
    }


    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(PokemonGo pokemonGo) {
        EventBus.getDefault().post(pokemonGo);
    }
}
*/
