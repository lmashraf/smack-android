package org.uhworks.smack.Utilities

// App URLs
const val BASE_URL = "https://smack-android.herokuapp.com/v1/"
const val SOCKET_URL = "https://smack-android.herokuapp.com/"

// API URLs
const val URL_REGISTER = "${BASE_URL}account/register"
const val URL_LOGIN = "${BASE_URL}account/login"
const val URL_CREATE_USER = "${BASE_URL}user/add"
const val URL_GET_USER = "${BASE_URL}user/byEmail/"
const val URL_GET_CHANNELS = "${BASE_URL}channel/"
const val URL_GET_MESSAGES = "${BASE_URL}message/byChannel/"

// Shared preferences
const val PREFS_FILENAME = "prefs"
const val IS_LOGGED_IN = "isLoggedIn"
const val AUTH_TOKEN = "authToken"
const val USER_EMAIL = "userEmail"

// Broadcast
const val BROADCAST_USER_DATA_CHANGE = "BROADCAST_USER_DATA_CHANGE"
