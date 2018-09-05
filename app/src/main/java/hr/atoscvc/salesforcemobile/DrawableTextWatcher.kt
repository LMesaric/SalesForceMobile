package hr.atoscvc.salesforcemobile

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator

class DrawableTextWatcher(
        private val imageView: ImageView,
        private val key: Any,
        private val editTextFirst: EditText,
        private val editTextSecond: EditText? = null
) : TextWatcher {
    override fun afterTextChanged(s: Editable?) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val letterOne: Char? = if (editTextFirst.text.isBlank()) null else editTextFirst.text.trim()[0].toUpperCase()
        val letterTwo: Char? = if (editTextSecond == null || editTextSecond.text.isBlank()) null else editTextSecond.text.trim()[0].toUpperCase()
        val iconText = "${letterOne ?: ""}${letterTwo ?: ""}"
        val drawable: TextDrawable = TextDrawable.builder().buildRound(iconText, ColorGenerator.MATERIAL.getColor(key))
        imageView.setImageDrawable(drawable)
    }
}
