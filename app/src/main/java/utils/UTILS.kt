package utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/*
    I have a set of common classes that I use in all my projects.
    UTILS is one of those classes. UTILS is where I keep methods that will come in handy for all
    the project.
*/

object UTILS {

    /*
        It is not recommend to leave console logs printing stuff around.
        All my prints are made using the DebugLog method, and the bool 'isDebugEnabled' is
        responsible for allowing those prints or not. 'isDebugEnabled' is always TRUE during
        debugging, and it's necessarily FALSE when building a release apk.
     */
    private const val isDebugEnabled: Boolean = true

    fun DebugLog(tag: String, obj: kotlin.Any) {
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
}