package org.uhworks.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import org.uhworks.smack.Utilities.URL_LOGIN
import org.uhworks.smack.Utilities.URL_REGISTER

// Contains all functions that deal with authorisation
object AuthService {

    var isLoggedIn = false
    var userEmail = ""
    var authToken = ""

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

        Volley.newRequestQueue(context).add(registerRequest)
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

                    authToken = response.getString("token")
                    userEmail = response.getString("user")
                    isLoggedIn = true
                } catch (e: JSONException) {

                    Log.d("JSON", "EXC: ${e.localizedMessage}")
                    isLoggedIn = false
                    complete(false)
                }
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

        Volley.newRequestQueue(context).add(loginRequest)
    }
}