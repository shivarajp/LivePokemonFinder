package com.shivaraj.friendz.shivaraj.utils;

import android.content.Context;
import android.content.res.TypedArray;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.shivaraj.friendz.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by SYS on 23-Jul-2016.
 */

public class utils {

    public static String DAILY_POSTS = "/dailyposts";
    private static BitmapDescriptor randomPokemonImg;


    /**
     * Returns todays date in dd-MMM-yyyy format
     *
     * @return
     */
    private static String getTodaysDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String date = df.format(c.getTime());
        return date;
    }

    /**
     * FOrms a key for today's posts all posts on this date will be added under this key around the world
     *
     * @param context
     * @return
     */
    public static String getTodaysKey(Context context) {
        String country = context.getResources().getConfiguration().locale.getCountry();
        //String key = "/" + country.trim() + "-" + getTodaysDate() + "/";
        String key = "/" + getTodaysDate() + "/";
        return key;
    }

    /**
     * Returns key for number of days back from today
     *
     * @param daysBack
     * @return
     */
    public static String getPreviousDateKey(Context context, int daysBack) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, daysBack);
        String date = dateFormat.format(cal.getTime());
        String country = context.getResources().getConfiguration().locale.getCountry();
        //String key = "/" + country.trim() + "-" + date + "/";
        String key = "/" + date;
        return key;
    }

    public static String getRandomImg() {
        String[] images = {"https://firebasestorage.googleapis.com/v0/b/pokemon-friendz-backend.appspot.com/o/pokeimgs%2F119152-min.jpg?alt=media&token=3d13f585-ef45-4602-9218-ad3463f536a4", "https://firebasestorage.googleapis.com/v0/b/pokemon-friendz-backend.appspot.com/o/pokeimgs%2F272679-min.jpg?alt=media&token=72ae63cc-52ee-41fa-abfa-1acf6c42a696",
                "https://firebasestorage.googleapis.com/v0/b/pokemon-friendz-backend.appspot.com/o/pokeimgs%2F276504-min.jpg?alt=media&token=64e474a6-06ef-47b7-9784-04ab89c07b20",
                "https://firebasestorage.googleapis.com/v0/b/pokemon-friendz-backend.appspot.com/o/pokeimgs%2F301254-min.jpg?alt=media&token=99faeadf-9ffd-4e21-ad77-5cd58232f0e3",
                "https://firebasestorage.googleapis.com/v0/b/pokemon-friendz-backend.appspot.com/o/pokeimgs%2F312832-min.jpg?alt=media&token=2382a71f-73eb-4b38-885f-dcd5790bf387",
                "https://firebasestorage.googleapis.com/v0/b/pokemon-friendz-backend.appspot.com/o/pokeimgs%2F443730-min.jpg?alt=media&token=7469e2cb-615c-47b6-aa6c-16b205392c0e",
                "https://firebasestorage.googleapis.com/v0/b/pokemon-friendz-backend.appspot.com/o/pokeimgs%2F444131-min.jpg?alt=media&token=8a1d92af-1900-4104-b9ef-eed42a4dd698",
                "https://firebasestorage.googleapis.com/v0/b/pokemon-friendz-backend.appspot.com/o/pokeimgs%2F446274-min.jpg?alt=media&token=65a38a01-ed92-4945-887b-9900f4644720",
                "https://firebasestorage.googleapis.com/v0/b/pokemon-friendz-backend.appspot.com/o/pokeimgs%2F475764-min.jpg?alt=media&token=a11c0389-3bcb-4b21-9a74-fd01bd9698f8",
                "https://firebasestorage.googleapis.com/v0/b/pokemon-friendz-backend.appspot.com/o/pokeimgs%2F481904-min.jpg?alt=media&token=cabd376c-af5b-4f1e-8cd2-d6f2b51e05b2",
                "https://firebasestorage.googleapis.com/v0/b/pokemon-friendz-backend.appspot.com/o/pokeimgs%2F481914-min.jpg?alt=media&token=62b90640-e45a-497f-8d70-50534c77e9e6",
                "https://firebasestorage.googleapis.com/v0/b/pokemon-friendz-backend.appspot.com/o/pokeimgs%2F502378-min.jpg?alt=media&token=42c0d0d1-a0cc-4c56-8f8d-f0afca5eaf3d",
                "https://firebasestorage.googleapis.com/v0/b/pokemon-friendz-backend.appspot.com/o/pokeimgs%2F502383-min.jpg?alt=media&token=e4f14b6f-918a-4f3a-a2aa-fc5c7fb9b83d",
                "https://firebasestorage.googleapis.com/v0/b/pokemon-friendz-backend.appspot.com/o/pokeimgs%2F603466-min.jpg?alt=media&token=18b4de8f-8d27-4412-b4cf-39c00d46b68d",
                "https://firebasestorage.googleapis.com/v0/b/pokemon-friendz-backend.appspot.com/o/pokeimgs%2F613924-min.jpg?alt=media&token=6f746baf-a84c-419f-b04f-2cd64a6423d0",
                "https://firebasestorage.googleapis.com/v0/b/pokemon-friendz-backend.appspot.com/o/pokeimgs%2F613926-min.jpg?alt=media&token=6dd66898-c26b-4ee9-b717-bdbe4bb5740a",
                "https://firebasestorage.googleapis.com/v0/b/pokemon-friendz-backend.appspot.com/o/pokeimgs%2F641958-min.jpg?alt=media&token=9457f8ba-b08b-4f0c-8380-84f9e43a6ee2",
                "https://firebasestorage.googleapis.com/v0/b/pokemon-friendz-backend.appspot.com/o/pokeimgs%2F641968-min.jpg?alt=media&token=81701aca-cdd4-4141-9ab4-e49db3fed0c2",
                "https://firebasestorage.googleapis.com/v0/b/pokemon-friendz-backend.appspot.com/o/pokeimgs%2F641969-min.jpg?alt=media&token=51b749d8-8112-44bc-8063-9877784a1447"};
        Random r = new Random();
        int n = r.nextInt(18);
        return images[n];
    }

    public static BitmapDescriptor getRandomPokemonImg(Context context) {
        TypedArray images = context.getResources().obtainTypedArray(R.array.pokemons_imgs);
        int choice = (int) (Math.random() * images.length());
        randomPokemonImg = BitmapDescriptorFactory.fromResource(images.getResourceId(choice, R.drawable.b));
        return randomPokemonImg;
    }
}
