package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.reset_password.view.*

//LUKA - styles za sve zivo
//LUKA - Loading dialogs
//LUKA - urediti login i register screen, staviti animacije + trimmati sve inpute za login i register - dodati ikonice i onfucus listenere
//LUKA - change color for onFocus PNGs
//FILIP - reset password stranica ima jako strgani background image na mobitelu - https://imgur.com/a/9yk4E08
//FILIP - dodati Remember Me feature

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    private lateinit var email: String
    private lateinit var alertDialog: AlertDialog
    private lateinit var resetPasswordView: View

    private var dialog: android.app.AlertDialog? = null

    private var lastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        (application as MyApp).cancelTimer()

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        setSupportActionBar(toolbarLogin)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        btnLogin.setOnClickListener(this)
        tvForgotPassword.setOnClickListener(this)
        tvRegister.setOnClickListener(this)

        etLoginEmail.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val email: String = etLoginEmail.text.toString().trim()
                etLoginEmail.setText(email)

                if (email.isBlank()) {
                    etLoginEmail.error = getString(R.string.emailEmptyMessage)
                }
            }
        }
        etLoginPassword.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (etLoginPassword.text.isBlank()) {
                    etLoginPassword.error = getString(R.string.passwordEmptyMessage)
                }
            }
        }
    }

    private fun onLogin() {
        email = etLoginEmail.text.toString().trim()
        val tempPassword = etLoginPassword.text.toString()       // Do NOT trim the password
        var thereAreNoErrors = true

        if (email.isBlank()) {
            etLoginEmail.error = getString(R.string.emailEmptyMessage)
            thereAreNoErrors = false
        }
        if (tempPassword.isBlank()) {
            etLoginPassword.error = getString(R.string.passwordEmptyMessage)
            thereAreNoErrors = false
        }
        if (thereAreNoErrors) {

            dialog = SpotsDialog.Builder().setContext(this).build()
            dialog?.show()

            mAuth.signInWithEmailAndPassword(email, HashSHA3.getHashedValue(tempPassword))
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            db.collection(getString(R.string.databaseCollectionUsers))
                                    .document(mAuth.uid.toString())
                                    .get()
                                    .addOnSuccessListener { documentSnapshot ->
                                        if (documentSnapshot.exists()) {
                                            ActiveUserSingleton.user = User(
                                                    documentSnapshot.getString(getString(R.string.databaseDocumentFirstName)).toString(),
                                                    documentSnapshot.getString(getString(R.string.databaseDocumentLastName)).toString(),
                                                    mAuth.currentUser!!.email.toString()
                                            )
                                            if (mAuth.currentUser!!.isEmailVerified) {
                                                sendToMain()
                                            } else {
                                                mAuth.currentUser!!
                                                        .sendEmailVerification()
                                                        .addOnCompleteListener { task ->
                                                            if (!task.isSuccessful) {
                                                                Toast.makeText(
                                                                        this,
                                                                        "${getString(R.string.verificationEmailErrorPrefix)} ${task.exception?.message}",
                                                                        Toast.LENGTH_LONG
                                                                ).show()
                                                            }
                                                            sendToVerify()
                                                        }
                                            }
                                        } else {
                                            Toast.makeText(this, getString(R.string.noUserFound), Toast.LENGTH_LONG).show()
                                            dialog?.dismiss()
                                        }
                                    }
                        } else {
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                            dialog?.dismiss()
                        }

                    }
        }
    }

    private fun onForgotPassword() {
        resetPasswordView = layoutInflater.inflate(R.layout.reset_password, constraintLayoutLoginMain, false)
        alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setView(resetPasswordView)
        resetPasswordView.btnSendPassReset.setOnClickListener {
            onSendEmail()
        }

        resetPasswordView.etEmailPassReset.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val email: String = resetPasswordView.etEmailPassReset.text.toString().trim()
                resetPasswordView.etEmailPassReset.setText(email)
                resetPasswordView.etEmailPassReset.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_email_outline, 0, 0, 0)
                alertDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

                if (email.isBlank()) {
                    resetPasswordView.etEmailPassReset.error = getString(R.string.emailEmptyMessage)
                }
            } else {
                resetPasswordView.etEmailPassReset.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_email_outline_accent, 0, 0, 0)
                alertDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }
        }

        alertDialog.show()
        resetPasswordView.etEmailPassReset.requestFocus()
    }

    fun onSendEmail() {
        val email: String = resetPasswordView.etEmailPassReset.text.toString().trim()

        if (!email.isBlank()) {
            dialog = SpotsDialog.Builder().setContext(this).build()
            dialog?.show()
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            alertDialog.dismiss()
                            dialog?.dismiss()
//                            Toast.makeText(this, getString(R.string.checkYourEmail), Toast.LENGTH_LONG).show()
                            Snackbar.make(etLoginEmail, getString(R.string.checkYourEmail), Snackbar.LENGTH_INDEFINITE)
                                    .setAction("Dismiss") {}    // There must be an empty OnClickListener for Dismiss to work
                                    .show()
                            // LUKA - zamijeniti sve toastove? -> napraviti static funkciju, extract resource
                        } else {
                            resetPasswordView.etEmailPassReset.error = task.exception?.message
                            dialog?.dismiss()
                        }
                    }
        } else {
            resetPasswordView.etEmailPassReset.error = getString(R.string.emailEmptyMessage)
            dialog?.dismiss()
        }

    }

    override fun onClick(p0: View) {
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
            return
        }
        lastClickTime = SystemClock.elapsedRealtime()
        pressedOnClick(p0)
    }

    private fun pressedOnClick(v: View) {
        when (v.id) {
            R.id.btnLogin -> onLogin()
            R.id.tvRegister -> onCreateNewAccount()
            R.id.tvForgotPassword -> onForgotPassword()
        }

    }

    private fun onCreateNewAccount() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivityForResult(intent, 0)   //FILIP - ne vidim da se igdje koristi ovaj ForResult i requestCode
    }

    override fun onResume() {
        super.onResume()
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        val user: FirebaseUser? = mAuth.currentUser
        user?.reload()
        if (user != null) {
            if (user.isEmailVerified) {
                sendToMain()
            } else {
                sendToVerify()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        dialog?.dismiss()
    }

    private fun sendToMain() {
        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(mainIntent)
        finish()
    }

    private fun sendToVerify() {
        val verifyIntent = Intent(this, VerifyActivity::class.java)
        verifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(verifyIntent)
        finish()
    }
}
