package hr.atoscvc.salesforcemobile

import android.app.Activity
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import kotlin.math.roundToInt

object ToastExtension {
    /**
     * Shows a Snackbar-like Toast that is dismissed automatically after a few seconds or
     * by touching any part of the screen.
     *
     * @param activity  Activity in which Toast is displayed.
     * @param resId     ID of the resource string to be displayed as the message.   //The text to show.  Can be formatted text.
     * @param duration  Optional parameter specifying how long to display the message.
     */
    fun makeText(activity: Activity, resId: Int, duration: Int = Toast.LENGTH_LONG) {
        val text = activity.resources.getText(resId)
        makeText(activity, text, duration)
    }

    /**
     * Shows a Snackbar-like Toast that is dismissed automatically after a few seconds or
     * by touching any part of the screen.
     *
     * @param activity  Activity in which Toast is displayed.
     * @param text      The text to show. Can be formatted text.
     * @param duration  Optional parameter specifying how long to display the message.
     */
    fun makeText(activity: Activity, text: CharSequence, duration: Int = Toast.LENGTH_LONG) {
        val outMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(outMetrics)
        val pxWidth = outMetrics.widthPixels
        val margin = (pxWidth * 0.05).roundToInt()

        val t: Toast = Toast.makeText(activity, text, duration)
        val viewGroup = t.view as ViewGroup
        viewGroup.background = activity.getDrawable(R.drawable.toast_background)

        val lp = viewGroup.getChildAt(0).layoutParams
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
        lp.width = pxWidth - 2 * margin
        viewGroup.getChildAt(0).layoutParams = lp

        viewGroup.getChildAt(0).setPadding(
                activity.resources.getDimension(R.dimen.toast_padding_left).toInt(),
                activity.resources.getDimension(R.dimen.toast_padding_top).toInt(),
                activity.resources.getDimension(R.dimen.toast_padding_right).toInt(),
                activity.resources.getDimension(R.dimen.toast_padding_bottom).toInt()
        )

        val v = t.view.findViewById(android.R.id.message) as TextView
        v.setTextColor(ContextCompat.getColor(activity, android.R.color.white))
        v.gravity = Gravity.CENTER_VERTICAL or Gravity.START
        t.setGravity(Gravity.BOTTOM, 0, margin)

        t.show()
    }
}
