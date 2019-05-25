package org.uhworks.smack.Controller

import android.content.*
import android.graphics.Color
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import org.uhworks.smack.Model.Channel
import org.uhworks.smack.R
import org.uhworks.smack.Services.AuthService
import org.uhworks.smack.Services.MessageService
import org.uhworks.smack.Services.UserDataService
import org.uhworks.smack.Utilities.BROADCAST_USER_DATA_CHANGE
import org.uhworks.smack.Utilities.SOCKET_URL

class MainActivity : AppCompatActivity() {

    private val socket: Socket = IO.socket(SOCKET_URL)

    private var selectedChannel: Channel? = null
    private lateinit var channelAdapter: ArrayAdapter<Channel>

    private fun setupAdapters() {

        channelAdapter = ArrayAdapter<Channel>(this, android.R.layout.simple_list_item_1, MessageService.channels)

        channel_list.adapter = channelAdapter
    }

    private val userDataChangeReceived = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent?) {

            if (App.prefs.isLoggedIn) {

                userNameNavHeaderTxt.text = UserDataService.name
                userMailNavHeaderTxt.text = UserDataService.email

                val resourceId = resources.getIdentifier(
                    UserDataService.avatarName,
                    "drawable",
                    packageName
                )
                userImageNavHeaderImg.setImageResource(resourceId)

                val backgroundColor = UserDataService.returnAvatarColor(UserDataService.avatarColor)
                userImageNavHeaderImg.setBackgroundColor(backgroundColor)

                loginNavHeaderBtn.text = "Logout"

                MessageService.getChannels { complete ->

                    if (complete) {

                        if (MessageService.channels.count() > 0) {

                            selectedChannel = MessageService.channels[0]

                            // reload the list view once the data has changed.
                            channelAdapter.notifyDataSetChanged()

                            updateWithChannel()
                        }
                    }
                }
            }
        }
    }

    fun updateWithChannel() {

        mainChannelName.text = "#${selectedChannel?.name}"

        // download messages for channel
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // List view for channels
        setupAdapters()

        // Create sockets only once
        socket.connect()
        socket.on("channelCreated", onNewChannel)

        // Check if already logged in
        if (App.prefs.isLoggedIn) {
            AuthService.findUserByEmail(this) {}
        }

        channel_list.setOnItemClickListener { _, _, i, _ ->

            // Select the clicked channel
            selectedChannel = MessageService.channels[i]

            // Close drawer and update the channel name
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }
    }


    override fun onResume() {

        // Broadcast receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(
            userDataChangeReceived,
            IntentFilter(BROADCAST_USER_DATA_CHANGE)
        )

        super.onResume()
    }

    override fun onDestroy() {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceived)
        socket.disconnect()
        super.onDestroy()
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun loginNavHeaderBtnClicked(view: View) {

        if (App.prefs.isLoggedIn) {

            UserDataService.logout()

            userNameNavHeaderTxt.text = ""
            userMailNavHeaderTxt.text = ""

            userImageNavHeaderImg.setImageResource(R.drawable.profiledefault)
            userImageNavHeaderImg.setBackgroundColor(Color.TRANSPARENT)

            loginNavHeaderBtn.text = "Login"

        } else {

            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    fun addChannelBtnClicked(view: View) {

        if (App.prefs.isLoggedIn) {

            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

            builder.setView(dialogView)
                .setPositiveButton("Add") { _, _ ->

                    val nameTextField = dialogView.findViewById<EditText>(R.id.addChannelNameTxt)
                    val descTextField = dialogView.findViewById<EditText>(R.id.addChannelDescTxt)

                    val channelName = nameTextField.text.toString()
                    val channelDescription = descTextField.text.toString()

                    // Create channel with channel name and description
                    socket.emit("newChannel", channelName, channelDescription)
                }
                .setNegativeButton("Cancel") { _, _ ->

                    // Cancel and close the dialog
                }
                .show()
        }
    }

    private val onNewChannel = Emitter.Listener { args ->

        runOnUiThread {

            val channelName = args[0] as String
            val channelDescription = args[1] as String
            val channelId = args[2] as String

            val newChannel = Channel(channelName, channelDescription, channelId)

            MessageService.channels.add(newChannel)

            // Add the channel to the UI
            channelAdapter.notifyDataSetChanged()

        }
    }

    fun sendMessageBtnClicked(view: View) {

        hideKeyboard()
    }

    private fun hideKeyboard() {

        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText) {

            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }
}
