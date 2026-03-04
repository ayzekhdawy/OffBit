package com.offbit.offbit.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.offbit.offbit.model.UserProfile;

public class UserPreferences {
    private static final String PREF_NAME = "OffBitPrefs";
    private static final String KEY_USER_PROFILE = "user_profile";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_FIRST_LAUNCH = "first_launch";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public UserPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveUserProfile(UserProfile userProfile) {
        String json = gson.toJson(userProfile);
        sharedPreferences.edit().putString(KEY_USER_PROFILE, json).apply();
    }

    public UserProfile getUserProfile() {
        String json = sharedPreferences.getString(KEY_USER_PROFILE, null);
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, UserProfile.class);
    }

    public void setDarkMode(boolean isDarkMode) {
        sharedPreferences.edit().putBoolean(KEY_DARK_MODE, isDarkMode).apply();
    }

    public boolean isDarkMode() {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false);
    }

    public void setFirstLaunch(boolean isFirstLaunch) {
        sharedPreferences.edit().putBoolean(KEY_FIRST_LAUNCH, isFirstLaunch).apply();
    }

    public boolean isFirstLaunch() {
        return sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true);
    }

    public void clearUserProfile() {
        sharedPreferences.edit().remove(KEY_USER_PROFILE).apply();
    }
}
