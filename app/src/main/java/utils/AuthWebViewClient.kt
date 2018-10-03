package utils

import android.annotation.TargetApi
import android.os.AsyncTask
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URL

/*
    What's the purpose of this class?
    Here we are extending the WebViewClient to have control over the URL loaded in our WebView.
    Instagram API requires a Callback URL that will be called when Instagram responds to our
    requests.
    We want to know when those URL were called, so we can do something with the result.
*/

class AuthWebViewClient : WebViewClient() {

    private val TAG: String = AuthWebViewClient::javaClass.name
    private var requestToken = "";

    private var callback: IGetCodeCallback? = null

    fun setCallback(callback: IGetCodeCallback){
        this.callback = callback
    }

    //Got the got from the oauth/authorize/ request? Send it back to our SignIn Activity.
    interface IGetCodeCallback {
        fun onUrlRedirect(code: String)
    }

    //todo: treat errors properly
    @TargetApi(Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        if(callback == null){
            UTILS.DebugLog(TAG, "Callback null, please use setCallback()")
            return false
        }

        val url = request.url.toString()
        view.loadUrl(url)

        /*
            Here we are verifying if the loaded URL matches the result that we want, which is
            our Redirect URL + a code. If that's the case, grab that code and send it back
            via Callback.
        */
        if (url.startsWith(CONSTANTS.INSTAGRAM_API_REDIRECT_URL)) {
            val parts = url.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            requestToken = parts[1]
            callback!!.onUrlRedirect(requestToken)
            return true
        }
        callback!!.onUrlRedirect("")
        return false
    }

    /*
        Here I'm supressing the deprecation warning because the method below handles API 19<
        and the code above handles the API 20>
        No harm done!
     */
    @Suppress("OverridingDeprecatedMember")
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        if(callback == null){
            UTILS.DebugLog(TAG, "Callback nul, please use setCallback()l")
            return false
        }

        view.loadUrl(url)

        /*
            Here we are verifying if the loaded URL matches the result that we want, which is
            our Redirect URL + a code. If that's the case, grab that code and send it back
            via Callback.
        */
        if (url.startsWith(CONSTANTS.INSTAGRAM_API_REDIRECT_URL)) {
            val parts = url.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            requestToken = parts[1]
            callback!!.onUrlRedirect(requestToken)
            return true
        }
        callback!!.onUrlRedirect("")
        return false
    }
}