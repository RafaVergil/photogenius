package utils

import android.annotation.TargetApi
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

/*
    What's the purpose of this class?
    Here we are extending the WebViewClient to have control over the URL loaded in our WebView.
    Instagram API requires a Callback URL that will be called when Instagram responds to our
    requests.
    We want to know when those URL were called, so we can do something with the result.
*/

class AuthWebViewClient : WebViewClient() {

    private var requestToken = "";

    private var callback: IGetCodeCallback? = null

    fun setCallback(callback: IGetCodeCallback){
        this.callback = callback
    }

    //Got the got from the oauth/authorize/ request? Send it back to our SignIn Activity.
    interface IGetCodeCallback {
        fun OnUrlRedirect(code: String)
    }

    @TargetApi(Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        val url = request.url.toString()
        view.loadUrl(url)

        if (url.startsWith(CONSTANTS.INSTAGRAM_API_REDIRECT_URL)) {
            val parts = url.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            requestToken = parts[1]
            callback!!.OnUrlRedirect(requestToken)
            return true
        }
        return false
    }

    /*
        Here I'm supressing the deprecation warning because the method below handles API 19<
        and the code above handles the API 20>
        No harm done!
     */
    @Suppress("OverridingDeprecatedMember")
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        view.loadUrl(url)
        if (url.startsWith(CONSTANTS.INSTAGRAM_API_REDIRECT_URL)) {
            val parts = url.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            requestToken = parts[1]
            callback!!.OnUrlRedirect(requestToken)
            return true
        }
        return false
    }
}