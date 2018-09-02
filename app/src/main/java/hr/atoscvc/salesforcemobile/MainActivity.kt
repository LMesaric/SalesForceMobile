package hr.atoscvc.salesforcemobile

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_companies.*
import kotlinx.android.synthetic.main.fragment_contacts.*
import kotlinx.android.synthetic.main.search_settings.*

//FILIP - aplikacija se rusi: pokreni, stisni back (izadje van), vrati se unutra (srusi se)
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

    lateinit var searchView: SearchView
    private lateinit var searchItem: MenuItem

    private lateinit var homeFragment: HomeFragment
    private lateinit var contactsFragment: ContactsFragment
    private lateinit var companiesFragment: CompaniesFragment

    private lateinit var currentFragment: Fragment

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
                    goToHome()
                    searchItem.collapseActionView()
                    true
                }

                R.id.navBarContacts -> {
                    goToContacts()
                    searchItem.collapseActionView()
                    true
                }

                R.id.navBarCompanies -> {
                    goToCompanies()
                    searchItem.collapseActionView()
                    true
                }

                else -> false
            }
        }

        setSearchSettingsSelection()

        rbSettingsActive.setOnClickListener { searchSettingsChanged() }
        rbSettingsInactive.setOnClickListener { searchSettingsChanged() }
        rbSettingsAll.setOnClickListener { searchSettingsChanged() }
        cbSettingsMatchAll.setOnClickListener { searchSettingsChanged() }
        cbSettingsMatchCase.setOnClickListener { searchSettingsChanged() }
        cbSettingsMatchWords.setOnClickListener { searchSettingsChanged() }
    }

    private fun goToHome() {
        coordinator.title = resources.getString(R.string.Home)
        appBarLayout.setExpanded(false, true)
        appBarLayout.isActivated = false
        searchItem.isVisible = false
        searchView.visibility = View.GONE
        replaceFragment(homeFragment)
    }

    private fun goToContacts() {
        coordinator.title = resources.getString(R.string.Contacts)
        val bundle = Bundle()
        bundle.putBoolean(getString(R.string.EXTRA_CONTACT_IS_LIST_FOR_SELECT), false)
        contactsFragment.arguments = bundle
        appBarLayout.setExpanded(true, true)
        appBarLayout.isActivated = true
        searchView.setOnQueryTextListener(contactsFragment)
        searchItem.isVisible = true
        searchView.visibility = View.VISIBLE
        replaceFragment(contactsFragment)
    }

    private fun goToCompanies() {
        coordinator.title = resources.getString(R.string.Companies)
        val bundle = Bundle()
        bundle.putBoolean(getString(R.string.EXTRA_COMPANY_IS_LIST_FOR_SELECT), false)
        companiesFragment.arguments = bundle
        appBarLayout.setExpanded(true, true)
        appBarLayout.isActivated = true
        searchView.setOnQueryTextListener(companiesFragment)
        searchItem.isVisible = true
        searchView.visibility = View.VISIBLE
        replaceFragment(companiesFragment)
    }

    override fun recyclerViewContactsOnClick(imageView: ImageView, contact: Contact, hideEditButtons: Boolean) {
        val contactDetailsIntent = Intent(this, ContactDetailsActivity::class.java).apply {
            putExtra(getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT), contact)
            putExtra(getString(R.string.EXTRA_CONTACT_HIDE_EDIT_BUTTONS), hideEditButtons)
        }
        startActivityForResult(contactDetailsIntent, ContactsFragment.requestItemRefresh)
    }

    override fun recyclerViewCompaniesOnClick(imageView: ImageView, company: Company, hideEditButtons: Boolean) {
        val companyDetailsIntent = Intent(this, CompanyDetailsActivity::class.java).apply {
            putExtra(getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT), company)
            putExtra(getString(R.string.EXTRA_COMPANY_HIDE_EDIT_BUTTONS), hideEditButtons)
        }
        startActivityForResult(companyDetailsIntent, CompaniesFragment.requestItemRefresh)
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
            replaceFragment(contactsFragment)
        } else if (refreshCompanies) {
            refreshCompanies = false
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

        val searchEditText = searchView.findViewById<View>(android.support.v7.appcompat.R.id.search_src_text) as EditText
        searchEditText.setTextColor(ContextCompat.getColor(this, R.color.colorBackgroundWhite))

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
            R.id.action_advanced_search -> {
                toggleSearchSettings()
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

    override fun onBackPressed() {
        if (!toggleSearchSettings(true)) {
            super.onBackPressed()
        }
    }

    private fun toggleSearchSettings(onlyClose: Boolean = false): Boolean {
        val state = searchSettingsContainer.visibility
        return if (onlyClose && state != View.VISIBLE) {
            false
        } else {
            TransitionManager.beginDelayedTransition(searchSettingsContainer, Slide(Gravity.TOP).setDuration(600))
            searchSettingsContainer.visibility = if (state != View.VISIBLE) View.VISIBLE else View.GONE
            true
        }
    }

    private fun setSearchSettingsSelection() {
        val sharedPref = getSharedPreferences(
                getString(R.string.SHARED_PREFERENCES_SEARCH_SETTINGS),
                Context.MODE_PRIVATE
        )
        //LUKA extract default values kod R.string.SHARED_PREFERENCES_RADIO_BUTTON_SELECTED_ID
        val selectedRadioId: Int = sharedPref.getInt(
                getString(R.string.SHARED_PREFERENCES_RADIO_BUTTON_SELECTED_ID),
                R.id.rbSettingsActive
        )
        when (selectedRadioId) {
            R.id.rbSettingsActive -> rbSettingsActive.isChecked = true
            R.id.rbSettingsInactive -> rbSettingsInactive.isChecked = true
            R.id.rbSettingsAll -> rbSettingsAll.isChecked = true
        }

        cbSettingsMatchAll.isChecked = sharedPref.getBoolean(getString(R.string.SHARED_PREFERENCES_CHECK_BOX_MATCH_ALL), false)
        cbSettingsMatchCase.isChecked = sharedPref.getBoolean(getString(R.string.SHARED_PREFERENCES_CHECK_BOX_MATCH_CASE), false)
        cbSettingsMatchWords.isChecked = sharedPref.getBoolean(getString(R.string.SHARED_PREFERENCES_CHECK_BOX_MATCH_WORDS), false)
    }

    private fun searchSettingsChanged() {
        //FIXME - na poziv ove funkcije treba se refreshati search aktivnog fragmenta
        //LUKA - zatvoriti search setting na fling up
        //LUKA - recyclerview item - na klik podizanje i expand layouta iznad svega

        SearchFilter.preferencesChanged = true

        val sharedPref = getSharedPreferences(
                getString(R.string.SHARED_PREFERENCES_SEARCH_SETTINGS),
                Context.MODE_PRIVATE
        )
        with(sharedPref.edit()) {
            putInt(
                    getString(R.string.SHARED_PREFERENCES_RADIO_BUTTON_SELECTED_ID),
                    when {
                        rbSettingsActive.isChecked -> R.id.rbSettingsActive
                        rbSettingsInactive.isChecked -> R.id.rbSettingsInactive
                        rbSettingsAll.isChecked -> R.id.rbSettingsAll
                        else -> R.id.rbSettingsActive
                    }
            )
            putBoolean(getString(R.string.SHARED_PREFERENCES_CHECK_BOX_MATCH_ALL), cbSettingsMatchAll.isChecked)
            putBoolean(getString(R.string.SHARED_PREFERENCES_CHECK_BOX_MATCH_CASE), cbSettingsMatchCase.isChecked)
            putBoolean(getString(R.string.SHARED_PREFERENCES_CHECK_BOX_MATCH_WORDS), cbSettingsMatchWords.isChecked)
            apply()
        }
    }
}
