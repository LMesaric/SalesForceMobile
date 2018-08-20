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

class MainActivity : AppCompatActivity(), LogoutListener, ContactAdapter.RecyclerViewOnClickListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var position: Int = 0
    private var refreshContacts = false
    private var refreshCompanies = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                    appBarLayout.setExpanded(true, true)
                    appBarLayout.isActivated = true
                    coordinator.title = resources.getString(R.string.Contacts)
                    replaceFragment(ContactsFragment())
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

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

    }

    override fun recyclerViewOnClick(circleImageView: CircleImageView, contact: Contact, position: Int) {
        this.position = position
        val intent = Intent(this, ContactDetailsActivity::class.java)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, circleImageView, "contactAvatar")
        intent.putExtra(getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT), contact)
        startActivityForResult(intent, ContactsFragment.requestItemRefresh, options.toBundle())
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

        if (requestCode == CompaniesFragment.requestCodeRefresh) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                refreshCompanies = true
            }
        } else if (requestCode == ContactsFragment.requestCodeRefresh) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                refreshContacts = true
            }
        } else if (requestCode == ContactsFragment.requestItemRefresh) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                refreshContacts = true
            }
        }
    }

}
