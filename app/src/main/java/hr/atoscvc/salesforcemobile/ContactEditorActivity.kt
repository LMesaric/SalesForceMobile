package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_contact_editor.*

class ContactEditorActivity : AppCompatActivity(), ReplaceFragmentListener, CompanyAdapter.RecyclerViewCompaniesOnClickListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var contactEditFragment = ContactEditFragment()
    var companiesFragment = CompaniesFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_editor)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setSupportActionBar(toolbarContactEdit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        replaceFragment(contactEditFragment)
    }

    override fun onResume() {
        super.onResume()
        val user: FirebaseUser? = mAuth.currentUser
        if (user == null) {
            sendToLogin()
        }
    }

    override fun onBackPressed() {
        if (contactEditFragment.isVisible) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(getString(R.string.warningForDiscardingChanges))
                    .setPositiveButton(getString(R.string.Save)) { dialog, _ ->
                        contactEditFragment.save()
                        dialog.cancel()
                    }
                    .setNegativeButton(getString(R.string.Discard)) { _, _ ->
                        super.onBackPressed()
                    }
                    .setNeutralButton(getString(android.R.string.cancel)) {dialog, _ ->
                        dialog.cancel()
                    }
            builder.create().show()
        } else {
            super.onBackPressed()
        }
    }

    override fun recyclerViewCompaniesOnClick(imageView: ImageView, company: Company, hideEditButtons: Boolean) {
        val companyDetailsIntent = Intent(this, CompanyDetailsActivity::class.java).apply {
            putExtra(getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT), company)
            putExtra(getString(R.string.EXTRA_COMPANY_HIDE_EDIT_BUTTONS), hideEditButtons)
        }
        startActivityForResult(companyDetailsIntent, CompaniesFragment.requestItemRefresh)
    }

    private fun sendToLogin() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(loginIntent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.contact_edit_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.action_done -> if (contactEditFragment.isVisible) {
                contactEditFragment.save()
            } else {
                ToastExtension.makeText(this, R.string.cannotSaveNow)
            }
        }
        return true
    }

    override fun onUserInteraction() {
        (application as MyApp).onUserInteracted()
    }

    override fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        if (fragment.javaClass == contactEditFragment.javaClass) {
            fragmentTransaction.replace(R.id.contactEditorContainer, fragment)
        } else {
            fragmentTransaction.replace(R.id.contactEditorContainer, fragment).addToBackStack("tag")
        }
        fragmentTransaction.commit()
    }
}
