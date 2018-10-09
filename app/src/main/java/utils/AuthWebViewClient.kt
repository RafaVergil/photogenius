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

class AuthWebViewClient(private val callback: IGetCodeCallback) : WebViewClient() {

    private var requestToken = ""

    /*
        When the page finishes loading, console.log it. Like that we can know what's the content
        of the page and therefore treat possible errors.
        Without a server to handle URIs, URLS and redirects, this is the best approach.
     */
    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        view.loadUrl("javascript:console" +
                ".log(document.body.getElementsByTagName('pre')[0].innerHTML);")
    }

    //Got the got from the oauth/authorize/ request? Send it back to our SignIn Activity.
    interface IGetCodeCallback {
        fun onUrlRedirect(code: String)
    }

    @TargetApi(Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
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
            callback.onUrlRedirect(requestToken)
            return true
        }
        callback.onUrlRedirect("")
        return false
    }

    /*
        Here I'm suppressing the deprecation warning because the method below handles API 19<
        and the code above handles the API 20>
     */
    @Suppress("OverridingDeprecatedMember")
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        view.loadUrl(url)

        /*
            Here we are verifying if the loaded URL matches the result that we want, which is
            our Redirect URL + a code. If that's the case, grab that code and send it back
            via Callback.
        */
        if (url.startsWith(CONSTANTS.INSTAGRAM_API_REDIRECT_URL)) {
            val parts = url.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            requestToken = parts[1]
            callback.onUrlRedirect(requestToken)
            return true
        }
        callback.onUrlRedirect("")
        return false
    }
}