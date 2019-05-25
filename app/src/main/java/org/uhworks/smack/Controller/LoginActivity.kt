package org.uhworks.smack.Controller

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import org.uhworks.smack.R
import org.uhworks.smack.Services.AuthService

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginSpinner.visibility = View.INVISIBLE
    }

    fun loginLoginBtnClicked(view: View) {

        val email = loginEmailTxt.text.toString()
        val password = loginPasswordTxt.text.toString()

        enableSpinner(true)

        hideKeyboard()

        if (email.isNotEmpty() && password.isNotEmpty()) {

            AuthService.loginUser(this, email, password) { loginSuccess ->

                if (loginSuccess) {

                    AuthService.findUserByEmail(this) { findSuccess ->
                        if (findSuccess) {

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

            Toast.makeText(this, "Please fill in both email and password.", Toast.LENGTH_SHORT).show()
        }
    }

    fun loginCreateUserBtn(view: View) {

        val createUserIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createUserIntent)

        // Dismiss to get back to Main Activity afterwards
        finish()
    }

    private fun enableSpinner(enable: Boolean) {

        loginSpinner.visibility = if (enable) View.VISIBLE else View.INVISIBLE

        loginCreateUserBtn.isEnabled = !enable
        loginLoginBtn.isEnabled = !enable
    }

    private fun errorToast() {

        Toast.makeText(this, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show()
        enableSpinner(false)
    }

    private fun hideKeyboard() {

        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText) {

            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }
}


