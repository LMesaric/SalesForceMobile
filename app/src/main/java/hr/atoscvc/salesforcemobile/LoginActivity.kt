package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.reset_password.view.*

//LUKA - dodati Remember Me feature
//LUKA - urediti login i register screen, trimmati sve inpute za login i register - dodati ikonice i onfucus listenere
//LUKA - change color for onFocus PNGs, torbe umjesto zgrade za company icon
//LUKA - preseliti search_icon i action_add u drawable (usage!!)
//FILIP - Loading dialogs
//FILIP - reset password stranica ima jako mali centralni dio na mobitelu

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
                                                                ToastExtension.makeText(
                                                                        this,
                                                                        "${getString(R.string.verificationEmailErrorPrefix)} ${task.exception?.message}"
                                                                )
                                                            }
                                                            sendToVerify()
                                                        }
                                            }
                                        } else {
                                            ToastExtension.makeText(this, R.string.noUserFound)
                                            dialog?.dismiss()
                                        }
                                    }
                        } else {
                            ToastExtension.makeText(this, task.exception?.message.toString())
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

    private fun onSendEmail() {
        val email: String = resetPasswordView.etEmailPassReset.text.toString().trim()

        if (!email.isBlank()) {
            dialog = SpotsDialog.Builder().setContext(this).build()
            dialog?.show()
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            alertDialog.dismiss()
                            dialog?.dismiss()
                            ToastExtension.makeText(this, R.string.checkYourEmail)
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

    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastClickTime < 400) {
            return
        }
        lastClickTime = SystemClock.elapsedRealtime()
        pressedOnClick(v)
    }

    private fun pressedOnClick(v: View) {
        when (v.id) {
            R.id.btnLogin -> onLogin()
            R.id.tvRegister -> onCreateNewAccount()
            R.id.tvForgotPassword -> onForgotPassword()
        }
    }

    private fun onCreateNewAccount() {
        val registerIntent = Intent(this, RegisterActivity::class.java)
        startActivity(registerIntent)
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
