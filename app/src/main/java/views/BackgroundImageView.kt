package views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.ImageView
import br.com.rafaelverginelli.photogenius.R
import utils.UTILS

/*
    This view works as an ImageView, but it supports Round Borders and Gradient Background.
    It's pretty simple and it makes the app look nice.
 */
class BackgroundImageView : ImageView {

    private val TAG: String = javaClass.simpleName

    var rivColorStart: Int = Color.WHITE
    var rivColorEnd: Int = Color.WHITE
    var rivRadius: Int = 0
    var rivGradientOrientation: Int = GradientDrawable.Orientation.TOP_BOTTOM.ordinal
    var rivTopLeft: Boolean = false
    var rivTopRight: Boolean = false
    var rivBottomRight: Boolean = false
    var rivBottomLeft: Boolean = false

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context,
            attrs, defStyleAttr) {

        if(attrs == null){
            UTILS.debugLog(TAG, "Null attrs")
            return
        }

        val attributes = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.BackgroundImageView,
                0, 0)

        try {

            rivColorStart = attributes?.
                    getColor(R.styleable.BackgroundImageView_rivColorStart, Color.WHITE) ?:
                    Color.WHITE

            rivColorEnd = attributes?.
                    getColor(R.styleable.BackgroundImageView_rivColorEnd, Color.WHITE) ?:
                    Color.WHITE

            rivRadius = attributes?.
                    getInteger(R.styleable.BackgroundImageView_rivRadius, 0) ?: 0

            rivGradientOrientation = attributes?.
                    getInteger(R.styleable.BackgroundImageView_rivGradientOrientation,
                            GradientDrawable.Orientation.TOP_BOTTOM.ordinal) ?:
                            GradientDrawable.Orientation.TOP_BOTTOM.ordinal

            rivTopLeft = attributes == null ||
                    attributes.getBoolean(R.styleable.BackgroundImageView_rivTopLeft,
                            true)

            rivTopRight = attributes == null ||
                    attributes.getBoolean(R.styleable.BackgroundImageView_rivTopRight,
                            true)

            rivBottomRight = attributes == null ||
                    attributes.getBoolean(R.styleable.BackgroundImageView_rivBottomRight,
                            true)

            rivBottomLeft = attributes == null ||
                    attributes.getBoolean(R.styleable.BackgroundImageView_rivBottomLeft,
                            true)

        } finally {
            attributes!!.recycle()
        }

        initDrawable()
    }

    private fun initDrawable() {

        val drawable = GradientDrawable()
        //Set gradient color
        drawable.colors = intArrayOf(rivColorStart, rivColorEnd)

        //Set gradient round border radius
        val radii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        if (rivTopLeft) {
            radii[0] = rivRadius.toFloat()
            radii[1] = rivRadius.toFloat()
        }

        if (rivTopRight) {
            radii[2] = rivRadius.toFloat()
            radii[3] = rivRadius.toFloat()
        }

        if (rivBottomRight) {
            radii[4] = rivRadius.toFloat()
            radii[5] = rivRadius.toFloat()
        }

        if (rivBottomLeft) {
            radii[6] = rivRadius.toFloat()
            radii[7] = rivRadius.toFloat()
        }

        drawable.cornerRadii = radii

        //Set gradient orieantion
        drawable.orientation = if (rivGradientOrientation >= 0 || rivGradientOrientation <= 7)
            GradientDrawable.Orientation.values()[rivGradientOrientation]
        else
            GradientDrawable.Orientation.TOP_BOTTOM

        background = drawable.constantState!!.newDrawable()
    }

}

