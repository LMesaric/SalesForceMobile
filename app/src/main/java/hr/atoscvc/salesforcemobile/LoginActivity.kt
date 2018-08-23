package hr.atoscvc.salesforcemobile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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

        etEmail.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val email = etEmail.text.toString().trim()
                etEmail.setText(email)

                if (email.isBlank()) {
                    etEmail.error = getString(R.string.emailEmptyMessage)
                }
            }
        }
        etPassword.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (etPassword.text.isBlank()) {
                    etPassword.error = getString(R.string.passwordEmptyMessage)
                }
            }
        }
    }

    fun onLogin(@Suppress("UNUSED_PARAMETER") view: View) {
        disableAllButtons()
        email = etEmail.text.toString().trim()
        val tempPassword = etPassword.text.toString()       // Do NOT trim the password
        var thereAreNoErrors = true

        if (email.isBlank()) {
            etEmail.error = getString(R.string.emailEmptyMessage)
            thereAreNoErrors = false
        }
        if (tempPassword.isBlank()) {
            etPassword.error = getString(R.string.passwordEmptyMessage)
            thereAreNoErrors = false
        }
        if (thereAreNoErrors) {
            btnLogin.visibility = View.INVISIBLE

            mAuth.signInWithEmailAndPassword(email, HashSHA3.getHashedValue(tempPassword))
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            db.collection("Users")
                                    .document(mAuth.uid.toString())
                                    .get()
                                    .addOnSuccessListener { documentSnapshot ->
                                        if (documentSnapshot.exists()) {
                                            ActiveUserSingleton.user = User(
                                                    documentSnapshot.getString("firstName").toString(),
                                                    documentSnapshot.getString("lastName").toString(),
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
                                                                        "Error sending verification email: ${task.exception?.message}",
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
                            Toast.makeText(this, getString(R.string.wrongEmailOrPassword), Toast.LENGTH_LONG).show()
                        }
                        btnLogin.visibility = View.VISIBLE
                    }
        }
        enableAllButtons()
    }

    @SuppressLint("InflateParams")
    fun onForgotPassword(@Suppress("UNUSED_PARAMETER") view: View) {
        resetPasswordView = layoutInflater.inflate(R.layout.reset_password, null)
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

        alertDialog.show()      //FILIP App mi se jednom ovdje srusio, navodno leaka activity i ne moze naci parenta za attachati dialog
        resetPasswordView.etEmailPassReset.requestFocus()
    }

    fun onSendEmail(@Suppress("UNUSED_PARAMETER") view: View) {
        val email: String = resetPasswordView.etEmailPassReset.text.toString().trim()

        if (!email.isBlank()) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            alertDialog.dismiss()
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
        startActivityForResult(intent, 0)
    }

    override fun onResume() {
        super.onResume()
        enableAllButtons()
        val user: FirebaseUser? = mAuth.currentUser
        if (user != null) {
            sendToMain()
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
    //FILIP - Za apsolutno sve greske osim successful password changea nema poruke useru
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
