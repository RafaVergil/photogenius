package br.com.rafaelverginelli.photogenius

import abstractions.CustomAppCompatActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import kotlinx.android.synthetic.main.activity_help.*
import utils.CurrentUserInstance
import utils.UTILS


class HelpActivity : CustomAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        btnContact.setOnClickListener{
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", getString(R.string.contact_email), null))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            emailIntent.putExtra(Intent.EXTRA_TEXT, "")
            startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email_title)))
        }

        btnPrivacyPolicy.setOnClickListener{
            val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_link)))
            startActivity(browserIntent)
        }

        btnGitHub.setOnClickListener{
            val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.git_hub_link)))
            startActivity(browserIntent)
        }

        btnSignOut.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this@HelpActivity).create()
            alertDialog.setTitle(R.string.sign_out_title)
            alertDialog.setMessage(this@HelpActivity
                    .getString(R.string.sign_out_message))
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, this@HelpActivity
                    .getString(android.R.string.yes)) { dialog, _ ->

                CurrentUserInstance.clearSession(this@HelpActivity)
                startActivity(Intent(this@HelpActivity, SplashActivity::class.java))

                dialog.dismiss()
            }
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, this@HelpActivity
                    .getString(android.R.string.no)) { dialog, _ -> dialog.dismiss()
            }
            alertDialog.show()
        }

        var apv = ""
        try {
            apv = packageManager.getPackageInfo(packageName, 0).versionName

        } catch (e: PackageManager.NameNotFoundException) {
            apv = ""
            UTILS.debugLog(TAG, e)
        } finally {
            txtVersion.text = String.format(getString(R.string.version_x), apv)
        }

    }
}
