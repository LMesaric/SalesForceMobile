package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity(), ReplaceFragmentListener {

    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        replaceFragment(NewNameFragment())
    }

    override fun onResume() {
        super.onResume()
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

    override fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        if (fragment.javaClass == NewNameFragment().javaClass) {
            fragmentTransaction.replace(R.id.regContainer, fragment)
        } else {
            fragmentTransaction.replace(R.id.regContainer, fragment).addToBackStack("tag")
        }
        fragmentTransaction.commit()
    }
}
