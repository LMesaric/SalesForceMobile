package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_change_password.*

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        (application as MyApp).cancelTimer()

        mAuth = FirebaseAuth.getInstance()

        /* Listener reporting password constraints errors on every change of password field */
        etUsernameInvisible.setText(mAuth.currentUser?.email)
        etPasswordNew.addTextChangedListener(PasswordTextWatcher(etUsernameInvisible, etPasswordNew))

        /* Listeners that change icon color */
        etPasswordOld.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                etPasswordOld.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_password_old, 0, 0, 0)
            } else {
                etPasswordOld.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_password_old_accent, 0, 0, 0)
            }
        }
        etPasswordNew.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                etPasswordNew.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_password_outline, 0, 0, 0)
            } else {
                etPasswordNew.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_password_outline_accent, 0, 0, 0)
            }
        }
        etPasswordNewConfirm.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                etPasswordNewConfirm.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_password_outline, 0, 0, 0)
            } else {
                etPasswordNewConfirm.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_password_outline_accent, 0, 0, 0)
            }
        }
    }

    fun onRequestReset(@Suppress("UNUSED_PARAMETER") view: View) {
        var thereAreNoErrors = true

        val passwordOld = etPasswordOld.text.toString()
        val passwordNew = etPasswordNew.text.toString()

        val passwordNewStatus = CheckPasswordConstraints.checkPasswordConstraints(
                mAuth.currentUser?.email.toString(),
                passwordNew
        )

        if (passwordOld.isBlank()) {
            etPasswordOld.error = getString(R.string.passwordEmptyMessage)
            thereAreNoErrors = false
        }
        if (!passwordNewStatus.success) {
            etPasswordNew.error = passwordNewStatus.message
            thereAreNoErrors = false
        }
        if (passwordNew != etPasswordNewConfirm.text.toString()) {
            etPasswordNewConfirm.error = getString(R.string.wrongConfirmPassword)
            thereAreNoErrors = false
        }

        if (thereAreNoErrors) {
            //val passwordOldHashed = HashSHA3.getHashedValue(passwordOld)
            btnChangePassOk.visibility = View.INVISIBLE
            btnChangePassCancel.visibility = View.INVISIBLE
            changePassProgress.visibility = View.VISIBLE

            //FILIP - testirati je li passwordOldHashed isti kao u bazi (isto kao za login)
            //Ovisno o tome postaviti passwordOldIsCorrect na true ili false
            val passwordOldIsCorrect = true

            if (!passwordOldIsCorrect) {
                etPasswordOld.error = getString(R.string.wrongPassword)
                btnChangePassOk.visibility = View.VISIBLE
                btnChangePassCancel.visibility = View.VISIBLE
                changePassProgress.visibility = View.INVISIBLE
            } else {
                //FILIP - zapisati novi password u bazu
                Toast.makeText(this, getString(R.string.passChangeSuccess), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    fun onCancel(@Suppress("UNUSED_PARAMETER") view: View) {
        finish()
    }

    override fun onResume() {
        super.onResume()
        window.setBackgroundDrawable(BitmapDrawable(
                applicationContext.resources,
                (application as MyApp).getInstance(applicationContext.resources)
        ))
        val user: FirebaseUser? = mAuth.currentUser
        if (user == null) {
            sendToLogin()
        }
    }

    private fun sendToLogin() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(loginIntent)
        finish()
    }

    override fun onUserInteraction() {
        (application as MyApp).onUserInteracted()
    }
}
