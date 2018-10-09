package br.com.rafaelverginelli.photogenius

import abstractions.CustomAppCompatActivity
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_sign_in.*
import models.AuthModel
import okhttp3.MultipartBody
import org.json.JSONException
import org.json.JSONObject
import requests.IAuthRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.*


class SignInActivity : CustomAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        if(!CurrentUserInstance.load(this)){

            webView.visibility = View.INVISIBLE
            startAnimations()

            btnSignIn.setOnClickListener{

                if(UTILS.checkConnectivity(this)){
                    webView.visibility = View.VISIBLE

                    setUpAuthWebView()

                    val url: String = String.format(
                            CONSTANTS.INSTAGRAM_API_GET_CODE_URL,
                            CONSTANTS.INSTAGRAM_API_CLIENT_ID,
                            CONSTANTS.INSTAGRAM_API_REDIRECT_URL)

                    webView.loadUrl(url)

                }
                else {
                    callErrorDialog(getString(R.string.error_dialog_title),
                            getString(R.string.error_dialog_message_connection))
                }
            }
        }
        else {
            Toast.makeText(this,
                    String.format(getString(R.string.welcome_back_x),
                            CurrentUserInstance.currenUserInstance!!.user.full_name),
                    Toast.LENGTH_LONG).show()

            startActivity(Intent(this,
                    MainFragmentActivity::class.java))
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpAuthWebView(){

        webView.settings.setAppCacheEnabled(false)
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false
        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = object : WebChromeClient() {

            override fun onConsoleMessage(cmsg: ConsoleMessage): Boolean {
                var errorJSON: JSONObject? = null
                try {
                    errorJSON = JSONObject(cmsg.message())
                }
                catch (e: JSONException){
                    UTILS.debugLog(TAG, e)
                }

                if(errorJSON != JSONObject.NULL && errorJSON != null) {

                    if (errorJSON.has(CONSTANTS.KEY_ERROR_ENVELOPE_TYPE) &&
                            errorJSON.get(CONSTANTS.KEY_ERROR_ENVELOPE_TYPE) != JSONObject.NULL &&
                            errorJSON.has(CONSTANTS.KEY_ERROR_ENVELOPE_CODE) &&
                            errorJSON.get(CONSTANTS.KEY_ERROR_ENVELOPE_CODE) != JSONObject.NULL) {

                        val errorCode = errorJSON.getInt(CONSTANTS.KEY_ERROR_ENVELOPE_CODE)
                        val errorType = errorJSON.getString(CONSTANTS.KEY_ERROR_ENVELOPE_TYPE)

                        if(ErrorHelper.checkError(this@SignInActivity, errorCode,
                                        errorType)){
                            if(errorType == CONSTANTS.ERROR_TYPE_OAUTH_FORBIDDEN_EXCEPTION){
                                webView.clearCache(true)
                                webView.loadUrl("javascript:document.open();document.close();")
                                webView.visibility = View.INVISIBLE
                            }
                        }
                        return true
                    }
                }

                ErrorHelper.checkError(this@SignInActivity, cmsg.message())

                return true
            }
        }
        webView.webViewClient = AuthWebViewClient(authCallback)
    }

    override fun onBackPressed() {
        //Prevent user from coming back to Splash Screen.
    }

    private fun startAnimations(){

        llLogoText.alpha = 0f
        txtSignInInfo.alpha = 0f
        btnSignIn.alpha = 0f

        val fadeDuration: Long = 500
        val delay: Long = 500

        val alphaLogo: ObjectAnimator = ObjectAnimator.
                ofFloat(llLogoText, "alpha", 0f, 1f)
        alphaLogo.duration = fadeDuration
        alphaLogo.startDelay = delay
        alphaLogo.start()

        val alphaTxtInfo: ObjectAnimator = ObjectAnimator.
                ofFloat(txtSignInInfo, "alpha", 0f, 1f)
        alphaTxtInfo.duration = fadeDuration
        alphaTxtInfo.startDelay = delay * 2
        alphaTxtInfo.start()

        val alphaBtnSignIn: ObjectAnimator = ObjectAnimator.
                ofFloat(btnSignIn, "alpha", 0f, 1f)
        alphaBtnSignIn.duration = fadeDuration
        alphaBtnSignIn.startDelay = delay * 3
        alphaBtnSignIn.start()

    }

    private fun clearWebView(){
        webView.clearCache(true)
        webView.loadUrl("javascript:document.open();document.close();")
    }

    private fun callErrorDialog(title: String, message: String){
        clearWebView()
        webView.visibility = View.INVISIBLE
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, this
                .getString(android.R.string.yes))
        { dialog, _ -> dialog.dismiss() }
        alertDialog.show()
    }

    private val authCallback: AuthWebViewClient.IGetCodeCallback =
            object: AuthWebViewClient.IGetCodeCallback{

                override fun onUrlRedirect(code: String) {

                    if(code.isEmpty()){
                        return
                    }

                    webView.visibility = View.INVISIBLE
                    loadingDialog.showDialog(
                            getString(R.string.loading),
                            getString(R.string.signing_in))

                    val iAuthRequest: IAuthRequest =
                            RetrofitClientInstance.retrofitInstance.create(IAuthRequest::class.java)

                    val arguments = MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart(CONSTANTS.KEY_INSTAGRAM_API_GET_TOKEN_CLIENT_ID,
                                    CONSTANTS.INSTAGRAM_API_CLIENT_ID)
                            .addFormDataPart(CONSTANTS.KEY_INSTAGRAM_API_GET_TOKEN_CLIENT_SECRET,
                                    CONSTANTS.INSTAGRAM_API_CLIENT_SECRET)
                            .addFormDataPart(CONSTANTS.KEY_INSTAGRAM_API_GET_TOKEN_GRANT_TYPE,
                                    CONSTANTS.VALUE_INSTAGRAM_API_GRANT_TYPE)
                            .addFormDataPart(CONSTANTS.KEY_INSTAGRAM_API_GET_TOKEN_REDIRECT_URI,
                                    CONSTANTS.INSTAGRAM_API_REDIRECT_URL)
                            .addFormDataPart(CONSTANTS.KEY_INSTAGRAM_API_GET_TOKEN_CODE,
                                    code)
                            .build()

                    val call: Call<AuthModel> = iAuthRequest.getAuthToken(arguments)
                    call.enqueue(onUserAuthResponse)
                }
            }


    val onUserAuthResponse = object : Callback<AuthModel> {

        override fun onResponse(call: Call<AuthModel>?,
                                response: Response<AuthModel>?) {

            loadingDialog.dismiss()

            if (response != null &&
                    response.isSuccessful && response.body() != null) {

                //Exchanged code for token successfully. Store user info.
                CurrentUserInstance.currenUserInstance = response.body()
                CurrentUserInstance.save(this@SignInActivity)

                Toast.makeText(this@SignInActivity,
                        String.format(getString(R.string.welcome_x),
                                CurrentUserInstance.currenUserInstance!!.user.full_name),
                        Toast.LENGTH_LONG).show()

                startActivity(Intent(this@SignInActivity,
                        MainFragmentActivity::class.java))

            } else {
                callErrorDialog(getString(R.string.error_dialog_title),
                        getString(R.string.error_dialog_message_connection))
            }
        }

        override fun onFailure(call: Call<AuthModel>?, t: Throwable?) {
            UTILS.debugLog(TAG, "onFailure")
        }
    }

    override fun onResume() {
        super.onResume()
        clearWebView()
    }

    override fun onPause() {
        super.onPause()
        clearWebView()
    }

}
