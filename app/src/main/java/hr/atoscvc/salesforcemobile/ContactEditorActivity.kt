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

    companion object {
        fun setCompany(company: Company): ContactEditFragment {
            val contactEditFragment = ContactEditFragment()
            val bundle = Bundle()
            bundle.putSerializable("Company", company)
            contactEditFragment.arguments = bundle
            return contactEditFragment
        }
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var contact: Contact? = null
    private var chosenCompany: Company? = null

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
