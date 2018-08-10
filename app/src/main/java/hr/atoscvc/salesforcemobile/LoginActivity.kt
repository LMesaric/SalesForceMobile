package hr.atoscvc.salesforcemobile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.reset_password.view.*
import java.lang.ref.WeakReference

class LoginActivity : AppCompatActivity(), BackgroundWorker.AsyncResponse {

    //LUKA - Dodati Log na sve Create/Resume itd.
    //LUKA - Python line counter

    //FIXME Saltati password u bazi!

    //FILIP - novi password (hard reset) nema nikakve provjere (CheckPasswordConstraints) - implementirati u JS-u
    //FILIP - forgot password loader indicator se trenutno vrti iza prozora pa se ne vidi
    //FILIP - EventLog SQL tablica (EventID primary key + String) - "User $userID forgot his password", "User $userID set a new password", "User $userID changed his password", "User $userID logged in/out"

    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    private lateinit var email: String
    private lateinit var alertDialogBuilder: AlertDialog.Builder
    private lateinit var alertDialog: AlertDialog
    private lateinit var resetPasswordView: View
    private lateinit var operation: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        (application as MyApp).cancelTimer()

        db = FirebaseFirestore.getInstance()

        mAuth = FirebaseAuth.getInstance()

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
            etEmail.error = getString(R.string.usernameEmptyMessage)
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
                            db.collection("Users").document(mAuth.uid.toString()).get()
                                    .addOnSuccessListener { documentSnapshot ->
                                        if (documentSnapshot.exists()) {
                                            val activeUser = User(documentSnapshot.getString("firstName").toString(), documentSnapshot.getString("lastName").toString(), mAuth.currentUser!!.email.toString())
                                            ActiveUserSingleton.user = activeUser
                                            sendToMain()
                                        } else {
                                            Toast.makeText(this, "No user found", Toast.LENGTH_LONG).show()
                                        }
                                    }
                        } else {
                            Toast.makeText(this, "Wrong email or password", Toast.LENGTH_LONG).show()
                        }
                        btnLogin.visibility = View.VISIBLE
                    }
        }
        enableAllButtons()
    }

    @SuppressLint("InflateParams")
    fun onForgotPassword(@Suppress("UNUSED_PARAMETER") view: View) {
        resetPasswordView = layoutInflater.inflate(R.layout.reset_password, null)
        alertDialogBuilder = AlertDialog.Builder(this)
        alertDialog = alertDialogBuilder.create()
        alertDialog.setView(resetPasswordView)

        resetPasswordView.etUsernamePassReset.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val username = resetPasswordView.etUsernamePassReset.text.toString().trim()
                resetPasswordView.etUsernamePassReset.setText(username)
                resetPasswordView.etUsernamePassReset.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_username_outline, 0, 0, 0)

                if (username.isBlank()) {
                    resetPasswordView.etUsernamePassReset.error = getString(R.string.usernameEmptyMessage)
                }
            } else {
                resetPasswordView.etUsernamePassReset.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_username_outline_accent, 0, 0, 0)
            }
        }

        resetPasswordView.etEmailPassReset.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val email = resetPasswordView.etEmailPassReset.text.toString().trim()
                resetPasswordView.etEmailPassReset.setText(email)
                resetPasswordView.etEmailPassReset.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_email_outline, 0, 0, 0)

                if (email.isBlank()) {
                    resetPasswordView.etEmailPassReset.error = getString(R.string.emailEmptyMessage)
                }
            } else {
                resetPasswordView.etEmailPassReset.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_email_outline_accent, 0, 0, 0)
            }
        }

        alertDialog.show()

        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }

    fun onSendEmail(@Suppress("UNUSED_PARAMETER") view: View) {
        var thereAreNoErrors = true

        val username = resetPasswordView.etUsernamePassReset.text.toString().trim()
        val email = resetPasswordView.etEmailPassReset.text.toString().trim()

        if (username.isBlank()) {
            resetPasswordView.etUsernamePassReset.error = getString(R.string.usernameEmptyMessage)
            thereAreNoErrors = false
        }

        if (email.isBlank()) {
            resetPasswordView.etEmailPassReset.error = getString(R.string.emailEmptyMessage)
            thereAreNoErrors = false
        }

        if (thereAreNoErrors) {
            resetPasswordView.btnSendPassReset.visibility = View.GONE
            operation = "PasswordReset"
            val backgroundWorker = BackgroundWorker(
                    WeakReference(applicationContext),
                    getString(R.string.loginStatus),
                    this,
                    WeakReference(resetPasswordView.mailProgress)
            )
            backgroundWorker.execute(operation, username, email)
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

    override fun processFinish(output: String) {
        resetPasswordView.btnSendPassReset.visibility = View.VISIBLE
        resetPasswordView.mailProgress.visibility = View.GONE
        if (output.contains("Success")) {
            resetPasswordView.btnSendPassReset.visibility = View.GONE
            Toast.makeText(this, "Check your email", Toast.LENGTH_LONG).show()
            alertDialog.dismiss()
        }
        //FILIP - Za apsolutno sve greske osim successful password changea nema poruke useru
        //FILIP - Moguce da cak ne radi za ispravan username i email (probao sam i nisam dobio Toast ni email)
        //FILIP - Kada je username bio za login, email je bio ekstra confirmation. Sad mozda vise ne treba username za reset (ne koristi se pa se zaboravi - cemu uopce imati usernameove?)
    }

}
