package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class VerifyActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)

        mAuth = FirebaseAuth.getInstance()
    }

    override fun onResume() {
        super.onResume()
        if (mAuth.currentUser!!.isEmailVerified) {
            sendToMain()
        }
    }

    fun onVerifySend(@Suppress("UNUSED_PARAMETER") view: View) {
        mAuth.currentUser!!.sendEmailVerification()
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Verification email sent", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Error sending verification email :" + task.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
    }

    private fun sendToMain() {
        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(mainIntent)
        finish()
    }

}
