package utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.support.v7.app.AlertDialog
import br.com.rafaelverginelli.photogenius.R
import br.com.rafaelverginelli.photogenius.SplashActivity
import okhttp3.ResponseBody
import org.json.JSONObject
import java.net.HttpURLConnection


/*
   This class was created to handle any kind of errors. It should implement methods to handle
   all (or at least the maximum possible) the errors.
 */
object ErrorHelper {

    // When a retrofit request fails, this method treats the error response body.
    fun checkError (context: Activity, errorBody: ResponseBody) : Boolean {

        val envelopeJson = JSONObject(errorBody.string())

        if(envelopeJson != JSONObject.NULL){

            if(envelopeJson.has(CONSTANTS.KEY_ENVELOPE_METADE) &&
                    envelopeJson.get(CONSTANTS.KEY_ENVELOPE_METADE)
                    != JSONObject.NULL) {

                val meta = envelopeJson.getJSONObject(CONSTANTS.KEY_ENVELOPE_METADE)

                if (meta.has(CONSTANTS.KEY_ERROR_ENVELOPE_TYPE) &&
                        meta.get(CONSTANTS.KEY_ERROR_ENVELOPE_TYPE) != JSONObject.NULL &&
                        meta.has(CONSTANTS.KEY_ERROR_ENVELOPE_CODE) &&
                        meta.get(CONSTANTS.KEY_ERROR_ENVELOPE_CODE) != JSONObject.NULL) {

                    val errorCode = meta.getInt(CONSTANTS.KEY_ERROR_ENVELOPE_CODE)
                    val errorType = meta.getString(CONSTANTS.KEY_ERROR_ENVELOPE_TYPE)

                    return checkError(context, errorCode, errorType)
                }
            }
        }

        // Unknown error
        genericError(context)
        return true
    }

    /*
        When a JSON error is received (from a WebView, for example), this method will treat it.
        We don't receive or display the "message" of the errors, because they are not always
        user-friendly. Instead, we display our own message, based on the errorCode and errorType.
      */
    fun checkError(context: Activity, errorCode: kotlin.Int, errorType: String) : Boolean {
        when (errorCode) {
            //4xx
            in HttpURLConnection.HTTP_BAD_REQUEST..HttpURLConnection.HTTP_UNSUPPORTED_TYPE ->{

                when (errorType) {
                    CONSTANTS.ERROR_TYPE_OAUTH_ACCESS_TOKEN_EXCEPTION -> {
                        sessionError(context)
                        return true
                    }

                    CONSTANTS.ERROR_TYPE_OAUTH_FORBIDDEN_EXCEPTION -> {
                        unauthorisedError(context)
                        return true
                    }
                }

            }
        }

        return false
    }

    fun checkError(context: Activity, source: String) : Boolean {
        return checkError(context, source, false)
    }

    // This method treats general errors that comes as strings, like an html body.
    fun checkError(context: Activity, source: String, hasErrorForSure: Boolean) : Boolean {

        var errorType: kotlin.Int =
                if(hasErrorForSure) CONSTANTS.ERROR_TYPE_UNKOWN else CONSTANTS.ERROR_TYPE_NONE

        if (source.contains(CONSTANTS.ERROR_SOURCE_CHROME)){
            errorType = CONSTANTS.ERROR_TYPE_CONNECTION
        } else if (source.contains(CONSTANTS.ERROR_SOURCE_INSTAGRAM_SERVER)) {
            errorType = CONSTANTS.ERROR_TYPE_INSTAGRAM
        }

        when (errorType) {
            CONSTANTS.ERROR_TYPE_CONNECTION -> {
                genericError(context,
                        context.getString(R.string.error_dialog_title),
                        context.getString(R.string.error_dialog_message_connection))
                return true
            }
            CONSTANTS.ERROR_TYPE_INSTAGRAM -> {
                genericError(context,
                        context.getString(R.string.error_dialog_title),
                        context.getString(R.string.error_dialog_message_instagram))
                return true
            }
            CONSTANTS.ERROR_TYPE_UNKOWN -> {
                genericError(context)
            }
        }

        /*
            If 'hasErrorForSure' is false and none of the conditions are true, then no error is
            recognized, no dialog is shown.
         */
        return hasErrorForSure
    }

    private fun sessionError(context: Activity){
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
    }

    fun genericError(context: Activity){
        genericError(context,
                context.getString(R.string.generic_error_title),
                context.getString(R.string.generic_error_message))
    }

    private fun genericError(context: Activity, title: String, message: String) {
        val alertDialog = AlertDialog.Builder(context).create()
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, context
                .getString(android.R.string.ok)
        ) { dialog, _ -> dialog.dismiss() }
        alertDialog.show()
    }

    private fun unauthorisedError(context: Activity) {
        val alertDialog = AlertDialog.Builder(context).create()
        alertDialog.setTitle(context.getString(R.string.error_dialog_title))
        alertDialog.setMessage(context.getString(R.string.error_dialog_message_oath))
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context
                .getString(android.R.string.ok)
        ) { dialog, _ -> dialog.dismiss() }
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, context
                .getString(R.string.app_data_button)
        ) { dialog, _ ->

            /*
                As we can't be sure if we can safely delete app data programmatically, we'll
                recommend that the user do it manually.
                We delete cached app data to prevent auto sign in of Instagram.
                If the sign in is successful but the access to the app is denied,
                the user would be unable to sign in a different account, unless
                he manually erase all cached data.
            */
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null))
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)

            dialog.dismiss()
        }
        alertDialog.show()
    }
}