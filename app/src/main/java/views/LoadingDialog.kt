package views

import android.animation.ObjectAnimator
import android.app.Activity
import android.app.AlertDialog
import android.support.annotation.NonNull
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import android.widget.TextView
import br.com.rafaelverginelli.photogenius.R
import org.w3c.dom.Text

/*
    This class has an instance in CustomAppCompatActivity, therefore all its subclasses will
    have access to a LoadingDialog. Title and Description are customizable.
*/
class LoadingDialog(activity: Activity) {

    private var activity: Activity? = activity

    private var dialog: AlertDialog? = null

    var imgLoading: ImageView? = null

    var title: String = ""
    var description: String = ""

    fun showDialog(@NonNull rootView: ViewGroup, title: String, description: String){
        dismiss()
        if(activity == null)
            return

        val builder = AlertDialog.Builder(activity)
        val dialogView = activity!!.layoutInflater.inflate(R.layout.loading_dialog, null)
        imgLoading = dialogView.findViewById(R.id.imgLoading)
        val txtTitle = dialogView.findViewById<TextView>(R.id.txtTitle)
        val txtDescription = dialogView.findViewById<TextView>(R.id.txtDescription)

        txtTitle.text = title
        txtDescription.text = description

        builder.setView(dialogView)
        builder.setCancelable(false)

        dialog = builder.create()
        dialog!!.show()

        animateLoading()
    }

    fun dismiss(){
        if(dialog != null){
            dialog!!.dismiss()
        }
    }

    private fun animateLoading(){
        val bounceX: ObjectAnimator =
                ObjectAnimator.ofFloat(imgLoading, "scaleX", 0.8f, 1f)
        bounceX.duration = 200
        bounceX.startDelay = 2000
        bounceX.interpolator = BounceInterpolator()
        bounceX.repeatCount = ObjectAnimator.INFINITE
        bounceX.repeatMode = ObjectAnimator.RESTART
        bounceX.start()

        val bounceY: ObjectAnimator =
                ObjectAnimator.ofFloat(imgLoading, "scaleY", 0.8f, 1f)
        bounceY.duration = 200
        bounceY.startDelay = 2000
        bounceY.interpolator = BounceInterpolator()
        bounceY.repeatCount = ObjectAnimator.INFINITE
        bounceY.repeatMode = ObjectAnimator.RESTART
        bounceY.start()
    }
}