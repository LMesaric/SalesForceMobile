package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_companies.*
import kotlinx.android.synthetic.main.fragment_contacts.*

//TODO MainActivity napraviti kao tabbedActivity ?
//FIXME SVAKI PUT KAD CREATEAMO FRAGMENT ON OSTAJE SAVEAN, KAO I ONAJ STARI (MEMORIJA RASTE DO 370 MB) - REUSE https://stackoverflow.com/questions/19219458/fragment-on-screen-rotation

class MainActivity :
        AppCompatActivity(),
        LogoutListener,
        ReplaceFragmentListener,
        ContactAdapter.RecyclerViewContactsOnClickListener,
        CompanyAdapter.RecyclerViewCompaniesOnClickListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var refreshContacts = false
    private var refreshCompanies = false

    private lateinit var searchView: SearchView
    private lateinit var searchItem: MenuItem

    private lateinit var homeFragment: HomeFragment
    private lateinit var contactsFragment: ContactsFragment
    private lateinit var companiesFragment: CompaniesFragment

    private lateinit var currentFragment: Fragment

    //TODO tuning icon search - dropdown, checkbox, and/or search

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

        homeFragment = HomeFragment()
        contactsFragment = ContactsFragment()
        companiesFragment = CompaniesFragment()

        initializeFragments()
        navBar.selectedItemId = R.id.navBarHome
        replaceFragment(homeFragment)

        navBar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.navBarHome -> {
                    appBarLayout.setExpanded(false, true)
                    appBarLayout.isActivated = false
                    coordinator.title = resources.getString(R.string.Home)
                    searchItem.isVisible = false
                    searchView.visibility = View.GONE
                    replaceFragment(homeFragment)
                    searchItem.collapseActionView()
                    true
                }

                R.id.navBarContacts -> {
                    val bundle = Bundle()
                    bundle.putBoolean(getString(R.string.EXTRA_CONTACT_IS_LIST_FOR_SELECT), false)
                    contactsFragment.arguments = bundle
                    appBarLayout.setExpanded(true, true)
                    appBarLayout.isActivated = true
                    coordinator.title = resources.getString(R.string.Contacts)
                    searchItem.isVisible = true
                    searchView.visibility = View.VISIBLE
                    searchView.setOnQueryTextListener(contactsFragment)
                    replaceFragment(contactsFragment)
                    searchItem.collapseActionView()
                    true
                }

                R.id.navBarCompanies -> {
                    val bundle = Bundle()
                    bundle.putBoolean(getString(R.string.EXTRA_COMPANY_IS_LIST_FOR_SELECT), false)
                    companiesFragment.arguments = bundle
                    searchItem.isVisible = true
                    searchView.visibility = View.VISIBLE
                    appBarLayout.setExpanded(true, true)
                    appBarLayout.isActivated = true
                    coordinator.title = resources.getString(R.string.Companies)
                    searchView.setOnQueryTextListener(companiesFragment)
                    replaceFragment(companiesFragment)
                    searchItem.collapseActionView()
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

    private var mKeyboardVisible: Boolean = false
    private val mLayoutKeyboardVisibilityListener = {
        val rectangle = Rect()
        //error getContentView
        val contentView = mainRoot
        contentView.getWindowVisibleDisplayFrame(rectangle)
        val screenHeight = contentView.rootView.height

        // r.bottom is the position above soft keypad or device button.
        // If keypad is shown, the rectangle.bottom is smaller than that before.
        val keypadHeight = screenHeight - rectangle.bottom
        // 0.15 ratio is perhaps enough to determine keypad height.
        val isKeyboardNowVisible = keypadHeight > screenHeight * 0.15

        if (mKeyboardVisible != isKeyboardNowVisible) {
            if (isKeyboardNowVisible) {
                navBar.visibility = View.GONE
                navBarShadow.visibility = View.GONE
            } else {
                navBar.visibility = View.VISIBLE
                navBarShadow.visibility = View.VISIBLE
            }
        }

        mKeyboardVisible = isKeyboardNowVisible
    }

    override fun onPause() {
        super.onPause()
        mainRoot.viewTreeObserver.removeOnGlobalLayoutListener(mLayoutKeyboardVisibilityListener)
    }

    override fun onResume() {
        super.onResume()

        val user: FirebaseUser? = mAuth.currentUser
        if (user == null) {
            sendToLogin()
        } else if (!user.isEmailVerified) {
            sendToVerify()
        }

        mainRoot.viewTreeObserver.addOnGlobalLayoutListener(mLayoutKeyboardVisibilityListener)

        if (refreshContacts) {
            refreshContacts = false
            appBarLayout.setExpanded(true, true)
            appBarLayout.isActivated = true
            coordinator.title = resources.getString(R.string.Contacts)
            replaceFragment(contactsFragment)

        } else if (refreshCompanies) {
            refreshCompanies = false
            val bundle = Bundle()
            bundle.putBoolean(getString(R.string.EXTRA_COMPANY_IS_LIST_FOR_SELECT), false)
            companiesFragment.arguments = bundle
            appBarLayout.setExpanded(true, true)
            appBarLayout.isActivated = true
            coordinator.title = resources.getString(R.string.Companies)
            replaceFragment(companiesFragment)
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

    private fun initializeFragments() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.mainContainer, homeFragment)
        fragmentTransaction.add(R.id.mainContainer, companiesFragment)
        fragmentTransaction.add(R.id.mainContainer, contactsFragment)
        fragmentTransaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        searchItem = menu?.findItem(R.id.action_search)!!
        searchView = searchItem.actionView as SearchView

        searchItem.isVisible = false
        searchView.visibility = View.GONE
        searchItem.collapseActionView()

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(menuItem: MenuItem): Boolean {
                appBarLayout.setExpanded(false, true)
                recyclerViewContacts?.isNestedScrollingEnabled = false
                recyclerViewCompanies?.isNestedScrollingEnabled = false
                return true
            }

            override fun onMenuItemActionCollapse(menuItem: MenuItem): Boolean {
                if (currentFragment == homeFragment) {
                    appBarLayout.setExpanded(false, true)
                } else {
                    appBarLayout.setExpanded(true, true)
                }
                recyclerViewContacts?.isNestedScrollingEnabled = true
                recyclerViewContacts?.smoothScrollToPosition(0)
                recyclerViewCompanies?.isNestedScrollingEnabled = true
                recyclerViewCompanies?.smoothScrollToPosition(0)
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {

            R.id.action_logout -> {
                sendToLogin()
            }

            R.id.action_account_settings -> {
                val intent = Intent(this, ChangePasswordActivity::class.java)
                startActivity(intent)
            }

        }
        return true
    }

    override fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        currentFragment = fragment

        fragmentTransaction.hide(contactsFragment)
        fragmentTransaction.hide(companiesFragment)
        fragmentTransaction.hide(homeFragment)

        fragmentTransaction.show(fragment)
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

    private fun sendToVerify() {
        val verifyIntent = Intent(this, VerifyActivity::class.java)
        verifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(verifyIntent)
        finish()
    }
}
