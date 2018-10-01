package abstractions

import android.support.v7.app.AppCompatActivity

/*
    One class to rule them all.
    It is actually the Activity super class that I use when I need all the other Activities
    to do something similar, like displaying a "Network unavailable" dialog, a "Loading" dialog
    or naming a TAG.
*/
abstract class CustomAppCompatActivity : AppCompatActivity {

    val TAG: String = CustomAppCompatActivity::class.java.simpleName

    constructor() : super()
}