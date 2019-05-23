package org.uhworks.smack.Controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.service.autofill.UserData
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlinx.android.synthetic.main.nav_header_main.*
import org.uhworks.smack.R
import org.uhworks.smack.Services.AuthService
import org.uhworks.smack.Services.UserDataService
import org.uhworks.smack.Utilities.BROADCAST_USER_DATA_CHANGE

class MainActivity : AppCompatActivity() {

    private val userDataChangeReceived = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {

            if (AuthService.isLoggedIn) {

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
            }
        }
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

        // Broadcast receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(
            userDataChangeReceived,
            IntentFilter(BROADCAST_USER_DATA_CHANGE)
        )
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

        if (AuthService.isLoggedIn) {
            // log out
            UserDataService.logout()

            userNameNavHeaderTxt.text = "Login"
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

    }

    fun sendMessageBtnClicked(view: View) {

    }
}
