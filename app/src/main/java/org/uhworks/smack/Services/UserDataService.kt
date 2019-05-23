package org.uhworks.smack.Services

import android.graphics.Color
import java.util.*

object UserDataService {

    var id = ""
    var email = ""
    var name = ""
    var avatarColor = ""
    var avatarName = ""


    fun logout() {

        id = ""
        email = ""
        name = ""
        avatarName = ""
        avatarColor = ""

        AuthService.authToken = ""
        AuthService.userEmail = ""
        AuthService.isLoggedIn = false
    }
    fun returnAvatarColor(rgbComponents: String): Int {

        val strippedColor = rgbComponents
            .replace("[", "")
            .replace("]", "")
            .replace(",", "")

        var red = 0
        var green = 0
        var blue = 0

        val scanner = Scanner(strippedColor)

        if(scanner.hasNext()) {
            red = (scanner.nextDouble() * 255).toInt()
            green = (scanner.nextDouble() * 255).toInt()
            blue = (scanner.nextDouble() * 255).toInt()
        }

        return Color.rgb(red, green, blue)
    }

}