package br.com.rafaelverginelli.photogenius

import android.animation.ObjectAnimator
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_sign_in.*
import models.AuthModel
import okhttp3.MultipartBody
import org.json.JSONObject
import requests.IAuthRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.AuthWebViewClient
import utils.CONSTANTS
import utils.RetrofitClientInstance

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        StartAnimations()

        btnSignIn.setOnClickListener{

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

    fun StartAnimations(){

        llLogoText.alpha = 0f
        txtSignInInfo.alpha = 0f
        btnSignIn.alpha = 0f

        val fadeDuration: Long = 500
        var delay: Long = 500

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

    val authCallback: AuthWebViewClient.IGetCodeCallback = object: AuthWebViewClient.IGetCodeCallback{
        override fun OnUrlRedirect(code: String) {
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
            call.enqueue(object : Callback<AuthModel> {

                override fun onResponse(call: Call<AuthModel>?, response: Response<AuthModel>?) {
                    val authModel: AuthModel? = response!!.body()
                    System.out.println("RETROFIT GET TOKEN Success! " + response.errorBody().toString())

                    if (response.isSuccessful) {
                        // Do your success stuff...
                    } else {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            Toast.makeText(applicationContext, jObjError.getString("message"), Toast.LENGTH_LONG).show()
                        } catch (e: Exception) {
                            Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<AuthModel>?, t: Throwable?) {
                    System.out.println("RETROFIT GET TOKEN Fail!")
                }
            })
        }
    }
}
