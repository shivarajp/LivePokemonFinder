package com.shivaraj.friendz.shivaraj.utils;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by Shivam on 7/7/2015.
 */
public class PokemonSharedPreferencesManager {

    public static final String USER_REGISTERED = "user_registered";
    public static final String CONTACTS_LIST = "contacts_list";
    //public static final String APP_OPENED = "app_opened";
    public static final String FARMER_REGISTERED = "farmer_registered";
    public static final String BUYER_REGISTERED = "buyer_registered";
    public static final String USER_ACCOUNT_DATA = "user_login_data";
    public static final String WATCHME_APP_PREFERENCE = "com.pokemongo.friendz";
    public static final String USER_ID = "user_id";

    public static boolean getBoolean(Context context, String constant) {
        SharedPreferences preferences = getPreferenceObject(context);
        if (preferences != null) {
            return preferences.getBoolean(constant, false);
        } else {
            return false;
        }
    }

    public static int getInt(Context context, String constant) {
        SharedPreferences preferences = getPreferenceObject(context);
        if (preferences != null) {
            return preferences.getInt(constant, -1);
        } else {
            return -2;
        }
    }


    public static float getFloat(Context context, String constant) {
        SharedPreferences preferences = getPreferenceObject(context);
        if (preferences != null) {
            return preferences.getFloat(constant, -1);
        } else {
            return -2;
        }
    }

    public static String getString(Context context, String constant) {
        SharedPreferences preferences = getPreferenceObject(context);
        if (preferences != null) {
            return preferences.getString(constant, "false");
        } else {
            return "false";
        }
    }


    public static void putBoolean(Context context, String constant, Boolean data) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        if (editor != null) {
            editor.putBoolean(constant, data).commit();
        }
    }

    public static void putString(Context context, String constant, String data) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        if (editor != null) {
            editor.putString(constant, data).commit();
        }
    }

    public static void putInt(Context context, String constant, int data) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        if (editor != null) {
            editor.putInt(constant, data).commit();
        }
    }

    public static void putLong(Context context, String constant, long data) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        if (editor != null) {
            editor.putLong(constant, data).commit();
        }
    }

    public static void putFloat(Context context, String constant, float data) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        if (editor != null) {
            editor.putFloat(constant, data).commit();
        }
    }

    public static SharedPreferences getPreferenceObject(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                WATCHME_APP_PREFERENCE, Context.MODE_PRIVATE);
        return preferences;
    }

    public static SharedPreferences.Editor getPreferencesEditor(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(WATCHME_APP_PREFERENCE, Context.MODE_PRIVATE).edit();
        return editor;
    }

}
