package utils

import android.content.Context
import com.google.gson.Gson
import models.AuthModel

class CurrentUserInstance {

    companion object{
        val TAG = "CurrentUserInstance"
        private var currentUser: AuthModel? = null

        var currenUserInstance: AuthModel?
            get() {
                return currentUser
            }
            set (user: AuthModel?) {
                currentUser = user
            }

        // Persists user data
        fun save(context: Context) : Boolean {
            if(currentUser == null) {
                UTILS.debugLog(TAG, "Invalid user data")
                return false
            }

            val gson = Gson()
            val userJson: String = gson.toJson(currentUser).toString()

            val prefs =
                    context.getSharedPreferences(CONSTANTS.SHAREDPREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString(CONSTANTS.KEY_SHAREDPREFS_USER, userJson)
            editor.apply()
            return true

        }

        fun load(context: Context) : Boolean {

            val prefs =
                    context.getSharedPreferences(CONSTANTS.SHAREDPREFS_NAME, Context.MODE_PRIVATE)
            val userJson = prefs.getString(CONSTANTS.KEY_SHAREDPREFS_USER, "")

            if(userJson == null || userJson.isEmpty()) {
                UTILS.debugLog(TAG, "Invalid user data")
                return false
            }

            val gson = Gson()
            val user: AuthModel? = gson.fromJson(userJson, AuthModel::class.java)

            if(user == null) {
                UTILS.debugLog(TAG, "Invalid user data")
                return false
            }

            currentUser = user

            return true

        }

        fun clearSession(context: Context){
            val prefs =
                    context.getSharedPreferences(CONSTANTS.SHAREDPREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.remove(CONSTANTS.KEY_SHAREDPREFS_USER)
            editor.apply()

            UTILS.debugLog(TAG, "User session cleared.")
        }
    }
}