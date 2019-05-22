package org.uhworks.smack.Controller

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_create_user.*
import org.uhworks.smack.R
import org.uhworks.smack.Services.AuthService
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profiledefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
    }

    fun generateUserAvatar(view: View) {

        val random = Random()
        val colour = random.nextInt(2)
        val avatar = random.nextInt(28)

        if (colour == 0) {
            userAvatar = "light$avatar"
        } else {
            userAvatar = "dark$avatar"
        }

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
        Toast.makeText(this, "Colour is: $avatarColor", Toast.LENGTH_SHORT).show()
    }

    fun createUserBtnClicked(view: View) {

        val email = createEmailTxt.text.toString()
        val password = createPasswordTxt.text.toString()

        AuthService.registerUser(this, email, password) { registerSuccess ->
            if (registerSuccess) {
                AuthService.loginUser(this, email, password) { loginSuccess ->
                    if (loginSuccess) {
                        println(AuthService.authToken)
                        println(AuthService.userEmail)
                    }
                }
            } else {

            }
        }
    }
}
