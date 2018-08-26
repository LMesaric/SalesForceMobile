package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.reset_password.view.*

//LUKA - styles za sve zivo
//LUKA - Loading dialogs
//LUKA - urediti login i register screen, staviti animacije + trimmati sve inpute za login i register - dodati ikonice i onfucus listenere
//LUKA - change color for onFocus PNGs
//FILIP - reset password stranica ima jako strgani background image na mobitelu - https://imgur.com/a/9yk4E08
//FILIP - dodati Remember Me feature

class LoginActivity : AppCompatActivity(), BackgroundWorker.AsyncResponse {

    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    private lateinit var email: String
    private lateinit var alertDialog: AlertDialog
    private lateinit var resetPasswordView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        (application as MyApp).cancelTimer()

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        setSupportActionBar(toolbarLogin)
        supportActionBar?.setDisplayShowTitleEnabled(false)

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

    fun onLogin(@Suppress("UNUSED_PARAMETER") view: View) {
        disableAllButtons()
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
            btnLogin.visibility = View.INVISIBLE

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
                                        }
                                    }
                        } else {
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                        }
                        btnLogin.visibility = View.VISIBLE
                    }
        }
        enableAllButtons()
    }

    fun onForgotPassword(@Suppress("UNUSED_PARAMETER") view: View) {
        resetPasswordView = layoutInflater.inflate(R.layout.reset_password, constraintLayoutLoginMain, false)
        alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setView(resetPasswordView)

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

    fun onSendEmail(@Suppress("UNUSED_PARAMETER") view: View) {
        val email: String = resetPasswordView.etEmailPassReset.text.toString().trim()

        if (!email.isBlank()) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            alertDialog.dismiss()
//                            Toast.makeText(this, getString(R.string.checkYourEmail), Toast.LENGTH_LONG).show()
                            Snackbar.make(etLoginEmail, getString(R.string.checkYourEmail), Snackbar.LENGTH_INDEFINITE)
                                    .setAction("Dismiss") {}    // There must be an empty OnClickListener for Dismiss to work
                                    .show()
                            // LUKA - zamijeniti sve toastove? -> napraviti static funkciju, extract resource
                        } else {
                            resetPasswordView.etEmailPassReset.error = task.exception?.message
                        }
                    }
        } else {
            resetPasswordView.etEmailPassReset.error = getString(R.string.emailEmptyMessage)
        }
    }

    fun onCreateNewAccount(@Suppress("UNUSED_PARAMETER") view: View) {
        disableAllButtons()
        val intent = Intent(this, RegisterActivity::class.java)
        startActivityForResult(intent, 0)   //FILIP - ne vidim da se igdje koristi ovaj ForResult i requestCode
    }

    override fun onResume() {
        super.onResume()
        enableAllButtons()
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

    private fun enableAllButtons() {
        tvRegister.isEnabled = true
        tvForgotPassword.isEnabled = true
        btnLogin.isEnabled = true
    }

    private fun disableAllButtons() {
        tvRegister.isEnabled = false
        tvForgotPassword.isEnabled = false
        btnLogin.isEnabled = false
    }

    //FILIP - forgot password loader indicator se trenutno vrti iza prozora pa se ne vidi
    //FILIP - sto napraviti s Background Workerom?
    override fun processFinish(output: String) {
        resetPasswordView.btnSendPassReset.visibility = View.VISIBLE
        resetPasswordView.mailProgress.visibility = View.GONE
        if (output.contains("Success")) {
            resetPasswordView.btnSendPassReset.visibility = View.GONE
            Toast.makeText(this, getString(R.string.checkYourEmail), Toast.LENGTH_LONG).show()
            alertDialog.dismiss()
        }
    }
}
