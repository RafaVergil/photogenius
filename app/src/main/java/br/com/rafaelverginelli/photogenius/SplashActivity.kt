package br.com.rafaelverginelli.photogenius

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_splash.*
import utils.CONSTANTS


class SplashActivity : AppCompatActivity() {

    val KEY_ACCESS_TOKEN = "access_token="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        btnTest.setOnClickListener{
            val uri = Uri.parse(String.format(
                    CONSTANTS.INSTAGRAM_API_GET_ACCESS_TOKEN_URL,
                    CONSTANTS.INSTAGRAM_API_CLIENT_ID,
                    String.format(CONSTANTS.INSTAGRAM_API_REDIRECT_URL,
                            getString(R.string.app_package_name))))

            val intent = Intent(Intent.ACTION_VIEW, uri)
            val b = Bundle()
            intent.putExtras(b)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        val intent = intent
        if (Intent.ACTION_VIEW == intent.action) {
            val uri = intent.data?.toString()
            /*
            Getting the Fragment Identifier from url
            I couldn't find a nicer way of extracing the access_token
            Another solution would be replacing '#' for '?' and extracting
            via getQueryParameterNames(), but it wouldn't be safe and it would be
            string manipulation, anyway.
             */
            val accessToken = uri!!.substring(uri.indexOf('#') + KEY_ACCESS_TOKEN.length + 1)

            if(!accessToken.isEmpty()) {
                txtTest.text = accessToken
                return
            }
        }

        txtTest.setText("Nothing found");
    }
}
