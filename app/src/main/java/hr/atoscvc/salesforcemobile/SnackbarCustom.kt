package hr.atoscvc.salesforcemobile

import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.view.View
import android.view.ViewGroup

object SnackbarCustom {
    /**
     * Adds margins and makes corners round, according to Material Design 2.
     * Action text color is also changed.
     * @return Snackbar object it was called on to allow chaining methods.
     */
    private fun Snackbar.beautify(): Snackbar {
        val margin = this.view.resources.getDimension(R.dimen.snackbar_margin).toInt()
        val params = this.view.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(margin, margin, margin, margin)
        this.view.layoutParams = params
        this.view.background = this.view.context.getDrawable(R.drawable.snackbar_background)
        this.setActionTextColor(ContextCompat.getColor(this.view.context, R.color.colorPrimaryRippleLight))
        ViewCompat.setElevation(this.view, 6f)
        return this
    }

    /**
     * Shows a Snackbar that must be dismissed by pressing a button.
     * @param view Any view from the layout. Parent will be found automatically.
     * @param resId ID of the resource string to be displayed as the message.
     */
    fun showIndefinite(view: View, resId: Int) {
        Snackbar.make(view, resId, Snackbar.LENGTH_INDEFINITE)
                // There must be an empty OnClickListener for Dismiss to work
                .setAction(view.resources.getString(R.string.dismiss)) {}
                .beautify()
                .show()
    }

    /**
     * Shows a Snackbar that must be dismissed by pressing a button.
     * @param view Any view from the layout. Parent will be found automatically.
     * @param message String to be displayed as the message.
     */
    fun showIndefinite(view: View, message: String?) {
        Snackbar.make(view, message.toString(), Snackbar.LENGTH_INDEFINITE)
                // There must be an empty OnClickListener for Dismiss to work
                .setAction(view.resources.getString(R.string.dismiss)) {}
                .beautify()
                .show()
    }

    /**
     * Shows a Snackbar that is dismissed automatically after three seconds.
     * @param view Any view from the layout. Parent will be found automatically.
     * @param resId ID of the resource string to be displayed as the message.
     */
    fun showAutoClose(view: View, resId: Int) {
        Snackbar.make(view, resId, 3000).beautify().show()
    }

    /**
     * Shows a Snackbar that is dismissed automatically after three seconds.
     * @param view Any view from the layout. Parent will be found automatically.
     * @param message String to be displayed as the message.
     */
    fun showAutoClose(view: View, message: String?) {
        Snackbar.make(view, message.toString(), 3000).beautify().show()
    }
}
