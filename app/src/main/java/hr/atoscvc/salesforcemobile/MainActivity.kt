package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*

//TODO Prilikom rotacije ekrana uvijek skoci na Home i ostane staro selectano na dnu (na Registration se vraca na dio s imenom i prezimenom)
//TODO MainActivity napraviti kao tabbedActivity ?

class MainActivity : AppCompatActivity(), LogoutListener, ContactAdapter.RecyclerViewContactsOnClickListener, CompanyAdapter.RecyclerViewCompaniesOnClickListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var refreshContacts = false
    private var refreshCompanies = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        (application as MyApp).registerSessionListener(this)
        (application as MyApp).startUserSession()

        appBarLayout.setExpanded(false, true)
        appBarLayout.isActivated = false

        setSupportActionBar(toolBar)
        coordinator.title = resources.getString(R.string.Home)

        navBar.selectedItemId = R.id.navBarHome
        replaceFragment(HomeFragment())

        navBar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.navBarHome -> {
                    appBarLayout.setExpanded(false, true)
                    appBarLayout.isActivated = false
                    coordinator.title = resources.getString(R.string.Home)
                    replaceFragment(HomeFragment())
                    true
                }

                R.id.navBarContacts -> {
                    val contactsFragment = ContactsFragment()
                    val bundle = Bundle()
                    bundle.putBoolean(getString(R.string.EXTRA_CONTACT_IS_LIST_FOR_SELECT), false)
                    contactsFragment.arguments = bundle
                    appBarLayout.setExpanded(true, true)
                    appBarLayout.isActivated = true
                    coordinator.title = resources.getString(R.string.Contacts)
                    replaceFragment(contactsFragment)
                    true
                }

                R.id.navBarCompanies -> {
                    val companiesFragment = CompaniesFragment()
                    val bundle = Bundle()
                    bundle.putBoolean(getString(R.string.EXTRA_COMPANY_IS_LIST_FOR_SELECT), false)
                    companiesFragment.arguments = bundle
                    appBarLayout.setExpanded(true, true)
                    appBarLayout.isActivated = true
                    coordinator.title = resources.getString(R.string.Companies)
                    replaceFragment(companiesFragment)
                    true
                }

                else -> false
            }
        }
    }

    override fun recyclerViewContactsOnClick(circleImageView: CircleImageView, contact: Contact, hideEditButtons: Boolean) {
        val contactDetailsIntent = Intent(this, ContactDetailsActivity::class.java).apply {
            putExtra(getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT), contact)
            putExtra(getString(R.string.EXTRA_CONTACT_HIDE_EDIT_BUTTONS), hideEditButtons)
        }
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, circleImageView, getString(R.string.transitionNameContactAvatar))
        startActivityForResult(contactDetailsIntent, ContactsFragment.requestItemRefresh, options.toBundle())
    }

    override fun recyclerViewCompaniesOnClick(circleImageView: CircleImageView, company: Company, hideEditButtons: Boolean) {
        val companyDetailsIntent = Intent(this, CompanyDetailsActivity::class.java).apply {
            putExtra(getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT), company)
            putExtra(getString(R.string.EXTRA_COMPANY_HIDE_EDIT_BUTTONS), hideEditButtons)
        }
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, circleImageView, getString(R.string.transitionNameCompanyAvatar))
        startActivityForResult(companyDetailsIntent, CompaniesFragment.requestItemRefresh, options.toBundle())
    }

    override fun onResume() {
        super.onResume()
        if (refreshContacts) {
            refreshContacts = false
            appBarLayout.setExpanded(true, true)
            appBarLayout.isActivated = true
            coordinator.title = resources.getString(R.string.Contacts)
            replaceFragment(ContactsFragment())

        } else if (refreshCompanies) {
            refreshCompanies = false
            val companiesFragment = CompaniesFragment()
            val bundle = Bundle()
            bundle.putBoolean(getString(R.string.EXTRA_COMPANY_IS_LIST_FOR_SELECT), false)
            companiesFragment.arguments = bundle
            appBarLayout.setExpanded(true, true)
            appBarLayout.isActivated = true
            coordinator.title = resources.getString(R.string.Companies)
            replaceFragment(companiesFragment)
        }

        val user: FirebaseUser? = mAuth.currentUser
        if (user == null) {
            sendToLogin()
        }
    }

    private fun sendToLogin() {
        ActiveUserSingleton.user = null
        mAuth.signOut()
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(loginIntent)
        finish()
    }

    override fun onSessionTimeout() {
        sendToLogin()
    }

    override fun onUserInteraction() {
        (application as MyApp).onUserInteracted()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            if (item.itemId == R.id.action_logout) {
                sendToLogin()
            } else if (item.itemId == R.id.action_account_settings) {
                val intent = Intent(this, ChangePasswordActivity::class.java)
                startActivity(intent)
            }
        }

        return true
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainContainer, fragment)
        fragmentTransaction.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {

            CompaniesFragment.requestCodeRefresh ->
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    refreshCompanies = true
                }

            CompaniesFragment.requestItemRefresh ->
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    refreshCompanies = true
                }

            ContactsFragment.requestCodeRefresh ->
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    refreshContacts = true
                }

            ContactsFragment.requestItemRefresh ->
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    refreshContacts = true
                }
        }
    }
}
