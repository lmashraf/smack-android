package org.uhworks.smack.Services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import org.uhworks.smack.Controller.App
import org.uhworks.smack.Utilities.*

// Contains all functions that deal with authorisation
object AuthService {

    fun registerUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {

        // Create JSON object and request
        val jsonBody = JSONObject()

        jsonBody.put("email", email)
        jsonBody.put("password", password)

        val requestBody = jsonBody.toString()

        // Create web POST request
        val registerRequest = object : StringRequest(Method.POST,
            URL_REGISTER,
            Response.Listener { response ->
                Log.d("SUCCESS", "User is registered: $response")
                complete(true)
            },
            Response.ErrorListener { error ->
                Log.d("ERROR", "Could not register user: $error")
                complete(false)
            }) {
            // Content type body
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        App.prefs.requestQueue.add(registerRequest)
    }

    fun loginUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val loginRequest = object : JsonObjectRequest(Method.POST,
            URL_LOGIN,
            null,
            Response.Listener { response ->

                try {

                    App.prefs.authToken = response.getString("token")
                    App.prefs.userEmail = response.getString("user")
                    App.prefs.isLoggedIn = true
                } catch (e: JSONException) {

                    Log.d("JSON", "EXC: ${e.localizedMessage}")
                    App.prefs.isLoggedIn = false
                    complete(false)
                }
                complete(true)
            },
            Response.ErrorListener { error ->
                Log.d("ERROR", "Could not login user: $error")
                complete(false)
            }) {
            // Content type body
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        App.prefs.requestQueue.add(loginRequest)
    }

    fun createUser(
        context: Context,
        email: String,
        name: String,
        avatarName: String,
        avatarColor: String,
        complete: (Boolean) -> Unit
    ) {

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("name", name)
        jsonBody.put("avatarColor", avatarColor)
        jsonBody.put("avatarName", avatarName)
        val requestBody = jsonBody.toString()

        val createUserRequest = object : JsonObjectRequest(Method.POST,
            URL_CREATE_USER,
            null,
            Response.Listener { response ->

                try {

                    UserDataService.id = response.getString("_id")
                    UserDataService.name = response.getString("name")
                    UserDataService.email = response.getString("email")
                    UserDataService.avatarColor = response.getString("avatarColor")
                    UserDataService.avatarName = response.getString("avatarName")

                    complete(true)

                } catch (e: JSONException) {

                    Log.d("JSON", "EXC: ${e.localizedMessage}")
                    complete(false)
                }

            }, Response.ErrorListener { error ->
                Log.d("ERROR", "Could not register user: $error")
                complete(false)
            }) {
            // Content type body
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = hashMapOf<String, String>()

                headers["Authorization"] = "Bearer ${App.prefs.authToken}"

                return headers
            }
        }

        App.prefs.requestQueue.add(createUserRequest)
    }

    fun findUserByEmail(context: Context, complete: (Boolean) -> Unit) {

        val findUserRequest = object : JsonObjectRequest(Method.GET,
            "$URL_GET_USER${App.prefs.userEmail}",
            null,
            Response.Listener { response ->

                try {

                    UserDataService.id = response.getString("_id")
                    UserDataService.name = response.getString("name")
                    UserDataService.email = response.getString("email")
                    UserDataService.avatarColor = response.getString("avatarColor")
                    UserDataService.avatarName = response.getString("avatarName")

                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                    LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)

                    complete(true)

                } catch (e: JSONException) {

                    Log.d("JSON", "EXC: ${e.localizedMessage}")
                    complete(false)
                }
            },
            Response.ErrorListener { error ->
                Log.d("ERROR", "Could not register user: $error")
                complete(false)

            }) {
            // Content type body
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = hashMapOf<String, String>()

                headers["Authorization"] = "Bearer ${App.prefs.authToken}"

                return headers
            }
        }

        App.prefs.requestQueue.add(findUserRequest)
    }
}
