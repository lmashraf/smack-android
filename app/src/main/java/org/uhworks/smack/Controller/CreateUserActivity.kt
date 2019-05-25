package org.uhworks.smack.Controller

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.activity_create_user.*
import org.uhworks.smack.R
import org.uhworks.smack.Services.AuthService
import org.uhworks.smack.Utilities.BROADCAST_USER_DATA_CHANGE
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    private var userAvatar = "profiledefault"
    private var avatarColor = "[0.5, 0.5, 0.5, 1}"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        createUserSpinner.visibility = View.INVISIBLE
    }

    fun generateUserAvatar(view: View) {

        val random = Random()
        val colour = random.nextInt(2)
        val avatar = random.nextInt(28)

        userAvatar = if (colour == 0) "light$avatar" else "dark$avatar"

        val resourceId = resources.getIdentifier(userAvatar, "drawable", packageName)

        createAvatarImg.setImageResource(resourceId)
    }

    fun generateBackgroundColourBtnClicked(view: View) {

        val random = Random()

        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)

        createAvatarImg.setBackgroundColor(Color.rgb(r, g, b))

        val savedR = r.toDouble() / 255
        val savedG = g.toDouble() / 255
        val savedB = b.toDouble() / 255

        avatarColor = "[$savedR, $savedG, $savedB, 1]"
    }

    fun createUserBtnClicked(view: View) {

        // Start showing the spinner
        enableSpinner(true)

        val username = createUsernameTxt.text.toString()
        val email = createEmailTxt.text.toString()
        val password = createPasswordTxt.text.toString()

        if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {

            AuthService.registerUser(email, password) { registerSuccess ->
                if (registerSuccess) {

                    AuthService.loginUser(email, password) { loginSuccess ->
                        if (loginSuccess) {

                            AuthService.createUser(email, username, userAvatar, avatarColor) { createSuccess ->
                                if (createSuccess) {

                                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)

                                    enableSpinner(false)
                                    finish()
                                } else {

                                    errorToast()
                                }
                            }
                        } else {

                            errorToast()
                        }
                    }
                } else {

                    errorToast()
                }
            }
        } else {

            Toast.makeText(this, "Make sure username, email and password as filled in.", Toast.LENGTH_SHORT).show()
            enableSpinner(false)
        }
    }

    private fun errorToast() {

        Toast.makeText(this, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show()
        enableSpinner(false)
    }

    private fun enableSpinner(enable: Boolean) {

        createUserSpinner.visibility = if (enable) View.VISIBLE else View.INVISIBLE

        createUserBtn.isEnabled = !enable
        createAvatarImg.isEnabled = !enable
        backgroundColourBtn.isEnabled = !enable
    }
}
