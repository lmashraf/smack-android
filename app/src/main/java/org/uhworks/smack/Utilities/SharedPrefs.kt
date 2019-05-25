package org.uhworks.smack.Utilities

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class SharedPrefs(context: Context) {

    val prefs = context.getSharedPreferences(PREFS_FILENAME, 0)

    // Custom getters and setters
    var isLoggedIn: Boolean
        get() = prefs.getBoolean(IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(IS_LOGGED_IN, value).apply()

    var authToken: String
        get() = prefs.getString(AUTH_TOKEN, "")
        set(value) = prefs.edit().putString(AUTH_TOKEN, value).apply()

    var userEmail: String
        get() = prefs.getString(USER_EMAIL, "")
        set(value) = prefs.edit().putString(USER_EMAIL, value).apply()

    var requestQueue: RequestQueue = Volley.newRequestQueue(context)
}