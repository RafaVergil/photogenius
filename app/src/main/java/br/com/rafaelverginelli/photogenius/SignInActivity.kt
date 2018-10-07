package br.com.rafaelverginelli.photogenius

import abstractions.CustomAppCompatActivity
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.ValueCallback
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_sign_in.*
import models.AuthModel
import okhttp3.MultipartBody
import org.json.JSONObject
import requests.IAuthRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.*

class SignInActivity : CustomAppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        if(!CurrentUserInstance.load(this@SignInActivity)){

            webView.visibility = View.INVISIBLE
            startAnimations()

            btnSignIn.setOnClickListener{

                webView.visibility = View.VISIBLE
                val authWebViewClient = AuthWebViewClient()
                authWebViewClient.setCallback(authCallback)
                webView.isVerticalScrollBarEnabled = false
                webView.isHorizontalScrollBarEnabled = false
                webView.webViewClient = authWebViewClient
                webView.settings.javaScriptEnabled = true

                val url: String = String.format(
                        CONSTANTS.INSTAGRAM_API_GET_CODE_URL,
                        CONSTANTS.INSTAGRAM_API_CLIENT_ID,
                        CONSTANTS.INSTAGRAM_API_REDIRECT_URL)

                webView.loadUrl(url)

            }
        }
        else {
            Toast.makeText(this@SignInActivity,
                    String.format(getString(R.string.welcome_back_x),
                            CurrentUserInstance.currenUserInstance!!.user.full_name),
                    Toast.LENGTH_LONG).show()

            startActivity(Intent(this@SignInActivity, MainActivity::class.java))
        }
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

                startActivity(Intent(this@SignInActivity, MainActivity::class.java))

            } else {
                //todo: treat errors properly
                try {
                    val jObjError = JSONObject(response!!.errorBody()!!.string())
                    Toast.makeText(applicationContext,
                            jObjError.getString("message"),
                            Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(applicationContext,
                            e.message,
                            Toast.LENGTH_LONG).show()
                }
            }
        }

        override fun onFailure(call: Call<AuthModel>?, t: Throwable?) {
            UTILS.DebugLog(TAG, "onFailure")
        }
    }

    override fun onResume() {
        super.onResume()

        //todo: clear webview

    }

}
