package utils

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AlertDialog
import br.com.rafaelverginelli.photogenius.R
import br.com.rafaelverginelli.photogenius.SplashActivity
import okhttp3.ResponseBody
import org.json.JSONObject

/*
   This class was created to handle any kind of errors. For the moment, it is only expecting
   OAuth errors, but other methods should be included as the app grows.
 */
object ErrorHelper {

    private const val ERROR_TYPE_OAUTH_ACCESS_TOKEN_EXCEPTION = "OAuthAccessTokenException"

    fun checkError (context: Activity, errorBody: ResponseBody) {

        val envelopeJson = JSONObject(errorBody.string())

        if(envelopeJson != JSONObject.NULL){

            if(envelopeJson.has(CONSTANTS.KEY_ENVELOPE_METADE) &&
                    envelopeJson.get(CONSTANTS.KEY_ENVELOPE_METADE)
                    != JSONObject.NULL) {
                
                val meta = envelopeJson.getJSONObject(CONSTANTS.KEY_ENVELOPE_METADE)

                if(meta.has(CONSTANTS.KEY_ERROR_ENVELOPE_TYPE) &&
                        meta.get(CONSTANTS.KEY_ERROR_ENVELOPE_TYPE)
                        != JSONObject.NULL) {
                    val errorType = meta.getString(CONSTANTS.KEY_ERROR_ENVELOPE_TYPE)

                    when (errorType) {
                        ERROR_TYPE_OAUTH_ACCESS_TOKEN_EXCEPTION -> {

                            CurrentUserInstance.clearSession(context)

                            val alertDialog = AlertDialog.Builder(context).create()
                            alertDialog.setTitle(R.string.error_invalid_token_title)
                            alertDialog.setMessage(context
                                    .getString(R.string.error_invalid_token_message))
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, context
                                    .getString(android.R.string.ok)
                            ) { dialog, _ ->
                                dialog.dismiss()
                                context.startActivity(Intent(context, SplashActivity::class.java))
                            }
                            alertDialog.show()

                            return
                        }
                    }
                }
            }

        }

        // Unknown error
        genericError(context)

    }

    fun genericError(context: Activity){
        val alertDialog = AlertDialog.Builder(context).create()
        alertDialog.setTitle(R.string.generic_error_title)
        alertDialog.setMessage(context
                .getString(R.string.generic_error_message))
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, context
                .getString(android.R.string.ok)
        ) { dialog, _ -> dialog.dismiss() }
        alertDialog.show()
    }
}