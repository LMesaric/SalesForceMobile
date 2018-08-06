package hr.atoscvc.salesforcemobile

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import hr.atoscvc.salesforcemobile.CheckPasswordConstraints.checkPasswordConstraints

class PasswordTextWatcher(private val editTextUser: EditText, private val editTextPass: EditText) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun afterTextChanged(s: Editable) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val editTextUserString = editTextUser.text.toString()
        val editTextPassString = editTextPass.text.toString()

        if (editTextPassString.isNotEmpty()) {
            val isPasswordGood = checkPasswordConstraints(editTextUserString, editTextPassString)
            if (!isPasswordGood.success) {
                editTextPass.error = isPasswordGood.message
            } else {
                editTextPass.error = null
            }
        } else {
            editTextPass.error = null
        }
    }
}