package com.carebuddy;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME    = "CareBuddySession";
    private static final String KEY_EMAIL    = "email";
    private static final String KEY_NAME     = "name";
    private static final String KEY_ROLE     = "role";
    private static final String KEY_LOGGED   = "isLoggedIn";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs  = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(String email, String name, String role) {
        editor.putBoolean(KEY_LOGGED, true);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    public boolean isLoggedIn()      { return prefs.getBoolean(KEY_LOGGED, false); }
    public String  getEmail()        { return prefs.getString(KEY_EMAIL,   ""); }
    public String  getName()         { return prefs.getString(KEY_NAME,    ""); }
    public String  getRole()         { return prefs.getString(KEY_ROLE,    ""); }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
