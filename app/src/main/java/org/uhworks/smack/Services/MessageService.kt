package org.uhworks.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import org.json.JSONException
import org.uhworks.smack.Controller.App
import org.uhworks.smack.Model.Channel
import org.uhworks.smack.Utilities.URL_GET_CHANNELS

object MessageService {

    val channels = arrayListOf<Channel>()

    fun getChannels(context: Context, complete: (Boolean) -> Unit) {

        val channelsRequest = object : JsonArrayRequest(Method.GET,
            URL_GET_CHANNELS,
            null,
            Response.Listener { response ->
                try {

                    for (x in 0 until response.length()) {

                        val channel = response.getJSONObject(x)

                        val name = channel.getString("name")
                        val description = channel.getString("description")
                        val id = channel.getString("_id")

                        val newChannel = Channel(name, description, id)

                        this.channels.add(newChannel)
                    }

                    complete(true)

                } catch (e: JSONException) {

                    Log.d("JSON", "EXC: ${e.localizedMessage}")
                    complete(false)
                }
            },
            Response.ErrorListener { error ->
                Log.d("ERROR", "Could not retrieve channels: $error")
                complete(false)
            }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = hashMapOf<String, String>()

                headers["Authorization"] = "Bearer ${App.prefs.authToken}"

                return headers
            }
        }

        App.prefs.requestQueue.add(channelsRequest)
    }
}