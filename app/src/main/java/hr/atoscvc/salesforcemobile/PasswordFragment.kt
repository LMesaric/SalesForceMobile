package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dmax.dialog.SpotsDialog
import hr.atoscvc.salesforcemobile.CheckPasswordConstraints.checkPasswordConstraints
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_password.*
import kotlinx.android.synthetic.main.fragment_password.view.*

class PasswordFragment : Fragment(), View.OnClickListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var dialog: android.app.AlertDialog? = null

    private var lastClickTime: Long = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_password, container, false)
        view.btnFinishPass.setOnClickListener(this)

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        view.etRegisterPassword.addTextChangedListener(PasswordTextWatcher(view.etRegisterPassword))

        return view
    }

    override fun onResume() {
        super.onResume()
        activity?.registerImageView?.setImageResource(R.drawable.password_page)
    }

    override fun onClick(view: View?) {
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
            return
        }
        lastClickTime = SystemClock.elapsedRealtime()

        val password: String = etRegisterPassword.text.toString()   // Do NOT trim the password
        var thereAreNoErrors = true

        val passwordStatus: PasswordErrors = checkPasswordConstraints(password)

        if (!passwordStatus.success) {
            etRegisterPassword.error = passwordStatus.message
            thereAreNoErrors = false
        }

        if (password != etRegisterConfirmPassword.text.toString()) {
            etRegisterConfirmPassword.error = getString(R.string.wrongConfirmPassword)
            thereAreNoErrors = false
        }

        if (thereAreNoErrors) {
            dialog = SpotsDialog.Builder().setContext(activity).build()
            dialog?.show()
            mAuth.createUserWithEmailAndPassword(ActiveUserSingleton.user?.email.toString(), HashSHA3.getHashedValue(password))
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            addUserToDatabase()
                        } else {
                            SnackbarCustom.showIndefinite(etRegisterPassword, task.exception?.message)
                        }
                    }
        }
    }

    private fun sendToVerify() {
        val verifyIntent = Intent(activity, VerifyActivity::class.java)
        verifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(verifyIntent)
        activity?.finish()
    }

    private fun addUserToDatabase() {
        mAuth.currentUser?.uid?.let {
            ActiveUserSingleton.user?.let { it1 ->
                db.collection(getString(R.string.databaseCollectionUsers))
                        .document(it)
                        .set(it1)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                mAuth.currentUser!!
                                        .sendEmailVerification()
                                        .addOnCompleteListener { task2 ->
                                            if (!task2.isSuccessful) {
                                                SnackbarCustom.showIndefinite(
                                                        etRegisterPassword,
                                                        "${getString(R.string.verificationEmailErrorPrefix)} ${task2.exception?.message}"
                                                )
                                            }
                                            sendToVerify()
                                        }
                            } else {
                                ActiveUserSingleton.user = null
                                mAuth.signOut()
                                SnackbarCustom.showIndefinite(etRegisterPassword, "Error: ${task.exception?.message}")
                            }
                        }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        dialog?.dismiss()
    }
}
