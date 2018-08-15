package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

//LUKA - urediti login i register screen, staviti animacije + trimmati sve inpute za login i register

class ContactEditorActivity : AppCompatActivity(), ReplaceFragmentListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_editor)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        replaceFragment(ContactEditFragment())

    }

    override fun onResume() {
        super.onResume()
        val user: FirebaseUser? = mAuth.currentUser
        if (user == null) {
            sendToLogin()
        }
    }

    private fun sendToLogin() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(loginIntent)
        finish()
    }

    override fun onUserInteraction() {
        (application as MyApp).onUserInteracted()
    }

    override fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        if (fragment.javaClass == ContactEditFragment().javaClass) {
            fragmentTransaction.replace(R.id.contactEditorContainer, fragment)
        } else {
            fragmentTransaction.replace(R.id.contactEditorContainer, fragment).addToBackStack("tag")
        }
        fragmentTransaction.commit()
    }

}
