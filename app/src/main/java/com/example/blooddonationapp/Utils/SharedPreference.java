package com.example.blooddonationapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference {
    public static final String           sharedPrefName ="SHARED_PREFERENCES";
    public static String signup="signUp";
    public static String number="number";
    public static String currentUserId="currentUserId";
    public static String isLocationProvided="isLocationProvided";
    public static String permissionOfLocation = "permissionOfLocation";
    public static String userType="UserType";


    private static      SharedPreference single_instance=null;

    private SharedPreference() {
    }

    // static method to create instance of Singleton class
    public static SharedPreference getInstance() {
        if (single_instance == null)
            single_instance=new SharedPreference();
        return single_instance;
    }

    public boolean saveData(String key, String data, Context context) {
        SharedPreferences prefs=context.getSharedPreferences(sharedPrefName, 0);
        SharedPreferences.Editor prefsEditor=prefs.edit();
        prefsEditor.putString(key, data);
        //prefsEditor.
        prefsEditor.apply();
        return true;
    }

    public String getData(String key, Context context) {
        SharedPreferences prefs=context.getSharedPreferences(sharedPrefName, 0);
        return prefs.getString(key, null);
    }

}
