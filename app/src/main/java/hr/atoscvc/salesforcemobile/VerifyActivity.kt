package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_verify.*

class VerifyActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mAuth: FirebaseAuth

    private var lastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)

        mAuth = FirebaseAuth.getInstance()

        val title: String = getString(R.string.verificationEmailSentPrefix) + mAuth.currentUser?.email.toString()

        tvVerifyTitle.text = title
    }

    override fun onResume() {
        super.onResume()
        mAuth.currentUser!!.reload()
        if (mAuth.currentUser!!.isEmailVerified) {
            sendToMain()
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
            R.id.btnVerifyLogout -> onVerifyLogout()
            R.id.btnVerifyResend -> onVerifySend()
            R.id.btnVerifyDone -> onVerificationDone()
        }

    }

    private fun onVerifySend() {
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
            return
        }
        lastClickTime = SystemClock.elapsedRealtime()
        mAuth.currentUser!!.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Verification email sent", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "${getString(R.string.verificationEmailErrorPrefix)} ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
    }

    private fun onVerificationDone() {
        mAuth.currentUser!!.reload()
        if (mAuth.currentUser!!.isEmailVerified) {
            sendToMain()
        } else {
            Toast.makeText(this, "Email is still not verified!" + mAuth.currentUser!!.isEmailVerified, Toast.LENGTH_LONG).show()
        }
    }

    //FILIP - ovo bi trebalo ici u toolbar menu
    private fun onVerifyLogout() {
        ActiveUserSingleton.user = null
        mAuth.signOut()
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(loginIntent)
        finish()
    }

    private fun sendToMain() {
        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(mainIntent)
        finish()
    }
}
