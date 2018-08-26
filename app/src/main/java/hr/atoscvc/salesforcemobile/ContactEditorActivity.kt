package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class ContactEditorActivity : AppCompatActivity(), ReplaceFragmentListener, CompanyAdapter.RecyclerViewCompaniesOnClickListener {

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

    override fun recyclerViewCompaniesOnClick(circleImageView: CircleImageView, company: Company, hideEditButtons: Boolean) {
        val companyDetailsIntent = Intent(this, CompanyDetailsActivity::class.java).apply {
            putExtra(getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT), company)
            putExtra(getString(R.string.EXTRA_COMPANY_HIDE_EDIT_BUTTONS), hideEditButtons)
        }
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, circleImageView, getString(R.string.transitionNameCompanyAvatar))
        startActivityForResult(companyDetailsIntent, CompaniesFragment.requestItemRefresh, options.toBundle())
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
