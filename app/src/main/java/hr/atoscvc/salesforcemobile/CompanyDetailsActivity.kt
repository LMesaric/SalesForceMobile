package hr.atoscvc.salesforcemobile

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import kotlinx.android.synthetic.main.activity_company_details.*
import kotlinx.android.synthetic.main.fragment_contacts.*

class CompanyDetailsActivity : AppCompatActivity(), ContactAdapter.RecyclerViewContactsOnClickListener {

    companion object {
        const val requestCodeEditCompany = 6
    }

    private var generator = ColorGenerator.MATERIAL

    private var refreshContacts = false

    private lateinit var company: Company
    private var isChanged: Boolean = false

    private val viewContactsFromCompanyFragment = ContactsFragment()
    private val viewCompanyDetailsFragment = ViewCompanyDetailsFragment()

    private lateinit var editItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_details)
        replaceFragment(viewCompanyDetailsFragment)
        company = intent.getSerializableExtra(getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT)) as Company
        setSupportActionBar(toolBarCompanyDetails)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun recyclerViewContactsOnClick(imageView: ImageView, contact: Contact, hideEditButtons: Boolean) {
        val contactDetailsIntent = Intent(this, ContactDetailsActivity::class.java).apply {
            putExtra(getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT), contact)
            putExtra(getString(R.string.EXTRA_CONTACT_HIDE_EDIT_BUTTONS), hideEditButtons)
        }
        startActivityForResult(contactDetailsIntent, ContactsFragment.requestItemRefresh)
    }

    override fun onResume() {
        super.onResume()

        if (refreshContacts) {
            viewContactsFromCompanyFragment.recyclerViewContacts?.adapter?.notifyDataSetChanged()
        }

        val letters = company.name[0].toString().toUpperCase()
        val drawable: TextDrawable = TextDrawable.builder().buildRound(letters, generator.getColor(company.documentID))
        expandedAvatarCompanyDetails.setImageDrawable(drawable)

        collapsingToolbarCompanyDetails.setBackgroundColor(generator.getColor(company.documentID))

        appBarCompanyDetails.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, _ ->
            val offsetAlpha = (appBarLayout.y / appBarCompanyDetails.totalScrollRange)
            expandedAvatarCompanyDetails.alpha = 1 - (offsetAlpha * -1)
        })

        appBarCompanyDetails.setExpanded(true, true)
        appBarCompanyDetails.isActivated = true
        collapsingToolbarCompanyDetails.title = company.name

        fabCompanyDetailsCallSecondary.hide()

        fabCompanyDetailsCall.addOnHideAnimationListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}
            override fun onAnimationEnd(p0: Animator?) {}
            override fun onAnimationCancel(p0: Animator?) {}
            override fun onAnimationStart(p0: Animator?) {
                fabCompanyDetailsCallSecondary.show()
            }
        })

        fabCompanyDetailsCall.addOnShowAnimationListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}
            override fun onAnimationEnd(p0: Animator?) {}
            override fun onAnimationCancel(p0: Animator?) {}
            override fun onAnimationStart(p0: Animator?) {
                fabCompanyDetailsCallSecondary.hide()
            }
        })
    }

    private fun onEditCompanyDetails() {
        val intentEdit = Intent(this, CompanyEditorActivity::class.java).apply {
            putExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false)
            putExtra(getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT), company)
        }
        startActivityForResult(intentEdit, CompanyDetailsActivity.requestCodeEditCompany)
    }

    private fun onShareCompany() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, company.toFormattedString(resources))
            type = "text/plain"
        }
        try {
            startActivity(Intent.createChooser(shareIntent, getString(R.string.shareChooser)))
        } catch (e: Exception) {
            // Most of the times, if not always, app chooser will display a similar message.
            ToastExtension.makeText(this, R.string.noSuitableAppFound)
        }
    }

    private fun onAddContactCompanyDetails() {
        val intent = Intent(this, ContactEditorActivity::class.java).apply {
            putExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), true)
            putExtra(getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT), company)
        }
        ContactEditFragment.chosenCompany = company
        startActivity(intent)
    }

    @SuppressLint("RestrictedApi")
    private fun onViewContactsCompanyDetails() {
        val bundle = Bundle()
        fabCompanyDetailsCall.visibility = View.GONE
        fabCompanyDetailsCallSecondary.visibility = View.GONE
        bundle.putString("companyID", company.documentID)
        viewContactsFromCompanyFragment.arguments = bundle
        replaceFragment(viewContactsFromCompanyFragment)
    }

    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {
        if (isChanged) {
            val intentBack = Intent().apply {
                putExtra(getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT), company)
            }
            setResult(AppCompatActivity.RESULT_OK, intentBack)
        }
        fabCompanyDetailsCall.visibility = View.VISIBLE
        fabCompanyDetailsCallSecondary.visibility = View.VISIBLE

        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CompanyDetailsActivity.requestCodeEditCompany) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                company = data?.getSerializableExtra(getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT)) as Company
                intent.putExtra(getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT), company)
                isChanged = true
                viewCompanyDetailsFragment.refreshDetails()
            }

        } else if (requestCode == ContactsFragment.requestCodeRefresh) {
            refreshContacts = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.company_details_menu, menu)
        editItem = menu?.findItem(R.id.action_company_edit)!!
        val shouldHideButtons: Boolean = intent.getBooleanExtra(getString(R.string.EXTRA_CONTACT_HIDE_EDIT_BUTTONS), false)
        editItem.isVisible = !shouldHideButtons
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.action_company_edit -> onEditCompanyDetails()
            R.id.action_company_share -> onShareCompany()
            R.id.action_view_contacts -> onViewContactsCompanyDetails()
            R.id.action_add_contact -> onAddContactCompanyDetails()
        }
        return true
    }

    fun onCompanyCall(@Suppress("UNUSED_PARAMETER") view: View) {
        val phone: String? = company.phone?.trim()
        if (phone.isNullOrBlank()) {
            ToastExtension.makeText(this, R.string.wrongPhoneNumber)
        } else {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            try {
                startActivity(Intent.createChooser(dialIntent, getString(R.string.dialPhoneChooser)))
            } catch (e: Exception) {
                // Most of the times, if not always, app chooser will display a similar message.
                ToastExtension.makeText(this, R.string.noSuitableAppFound)
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        if (fragment.javaClass == viewCompanyDetailsFragment.javaClass) {
            fragmentTransaction.replace(R.id.companyDetailsContainer, fragment)
        } else {
            fragmentTransaction.replace(R.id.companyDetailsContainer, fragment).addToBackStack("tag")
        }
        fragmentTransaction.commit()
    }
}
