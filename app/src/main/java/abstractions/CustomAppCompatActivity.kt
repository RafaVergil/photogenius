package abstractions

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import views.LoadingDialog

/*
    One class to rule them all.
    It is actually the Activity super class that I use when I need all the other Activities
    to do something similar, like displaying a "Network unavailable" dialog, a "Loading" dialog
    or naming a TAG.
*/
abstract class CustomAppCompatActivity : AppCompatActivity() {

    val TAG: String = CustomAppCompatActivity::class.java.simpleName
    lateinit var loadingDialog: LoadingDialog

    fun getConn(): DatabaseConnection {
        return DatabaseConnection.getInstance(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
            I'm setting the instance of LoadingDialog in the onCreate method because doing that
            in the constructor or when initializing the 'loadingDialog' leads to Leaking Constructor
            Warning.
         */
        loadingDialog = LoadingDialog(this@CustomAppCompatActivity)
    }
}