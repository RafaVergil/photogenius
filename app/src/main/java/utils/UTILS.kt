package utils

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import br.com.rafaelverginelli.photogenius.SplashActivity
import java.io.File


/*
    I have a set of common classes that I use in all my projects.
    UTILS is one of those classes. UTILS is where I keep methods that will come in handy for all
    the project.
*/

object UTILS {

    /*
        It is not recommend to leave console logs printing stuff around.
        All my prints are made using the debugLog method, and the bool 'isDebugEnabled' is
        responsible for allowing those prints or not. 'isDebugEnabled' is always TRUE during
        debugging, and it's necessarily FALSE when building a release apk.
     */
    private const val isDebugEnabled: Boolean = true

    fun debugLog(tag: String, obj: kotlin.Any) {
        if(isDebugEnabled) {
            System.out.println(tag)
            System.out.println(obj.toString())
        }
    }

    fun checkConnectivity(context: Context) : Boolean {
        val cm: ConnectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return (cm.activeNetworkInfo != null)
    }

    fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    /*
        Here we deleted cached app data to prevent auto sign in of Instagram.
        If the sign in is successful but the access to the app is denied,
        the user would be unable to sign in a different account, unless
        he manually erase all cached data.
    */

}