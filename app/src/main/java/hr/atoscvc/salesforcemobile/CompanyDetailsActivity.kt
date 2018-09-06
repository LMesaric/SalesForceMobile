package hr.atoscvc.salesforcemobile

import android.animation.Animator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import kotlinx.android.synthetic.main.activity_company_details.*

class CompanyDetailsActivity : AppCompatActivity() {

    companion object {
        const val requestCodeEditCompany = 6
    }

    private var generator = ColorGenerator.MATERIAL

    private lateinit var company: Company
    private var isChanged: Boolean = false

    private lateinit var editItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_details)
        company = intent.getSerializableExtra(getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT)) as Company
        setSupportActionBar(toolBarCompanyDetails)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onResume() {
        super.onResume()

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

        tvCompanyDetailsStatus.text = resources.getStringArray(R.array.status_array)[company.status]
        tvCompanyDetailsOib.text = company.OIB

        if (company.cvsSegment == 0) {
            layoutCompanyDetailsCvsSegment.visibility = View.GONE
        } else {
            tvCompanyDetailsCvsSegment.text = resources.getStringArray(R.array.companyCVS_array)[company.cvsSegment]
            layoutCompanyDetailsCvsSegment.visibility = View.VISIBLE
        }

        if (company.communicationType == 0) {
            layoutCompanyDetailsCommunicationType.visibility = View.GONE
        } else {
            tvCompanyDetailsCommunicationType.text = resources.getStringArray(R.array.companyCommunicationType_array)[company.communicationType]
            layoutCompanyDetailsCommunicationType.visibility = View.VISIBLE
        }

        if (company.employees == 0) {
            layoutCompanyDetailsEmployees.visibility = View.GONE
        } else {
            tvCompanyDetailsEmployees.text = resources.getStringArray(R.array.companyEmployees_array)[company.employees]
            layoutCompanyDetailsEmployees.visibility = View.VISIBLE
        }

        if (company.webPage.isNullOrBlank()) {
            layoutCompanyDetailsWebPage.visibility = View.GONE
        } else {
            tvCompanyDetailsWebPage.text = company.webPage
            layoutCompanyDetailsWebPage.visibility = View.VISIBLE
        }

        if (company.phone.isNullOrBlank()) {
            layoutCompanyDetailsPhoneNumber.visibility = View.GONE
            fabCompanyDetailsCall.isClickable = false
        } else {
            tvCompanyDetailsPhoneNumber.text = company.phone
            layoutCompanyDetailsPhoneNumber.visibility = View.VISIBLE
            fabCompanyDetailsCall.isClickable = true
        }

        if (company.income.isNullOrBlank()) {
            layoutCompanyDetailsIncome.visibility = View.GONE
        } else {
            tvCompanyDetailsIncome.text = company.income
            layoutCompanyDetailsIncome.visibility = View.VISIBLE
        }

        if (company.details.isNullOrBlank()) {
            layoutCompanyDetailsDetails.visibility = View.GONE
        } else {
            tvCompanyDetailsDetails.text = company.details
            layoutCompanyDetailsDetails.visibility = View.VISIBLE
        }

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

    fun onCompanyWebPage(@Suppress("UNUSED_PARAMETER") view: View) {
        var webPage: String? = company.webPage?.trim()
        if (webPage.isNullOrBlank()) {
            ToastExtension.makeText(this, R.string.wrongWebPage)
        } else {
            webPage?.let {
                if (it.startsWith("www.", true)) {
                    webPage = "http://$webPage"
                } else if (!it.startsWith("http://", true) and !it.startsWith("https://", true)) {
                    webPage = "http://www.$webPage"
                }
            }
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webPage))
            try {
                startActivity(Intent.createChooser(webIntent, getString(R.string.openWebChooser)))
            } catch (e: Exception) {
                // Most of the times, if not always, app chooser will display a similar message.
                ToastExtension.makeText(this, R.string.noSuitableAppFound)
            }
        }
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

    fun onCompanyText(@Suppress("UNUSED_PARAMETER") view: View) {
        val phone: String? = company.phone?.trim()
        if (phone.isNullOrBlank()) {
            ToastExtension.makeText(this, R.string.wrongPhoneNumber)
        } else {
            val smsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("smsto:$phone")).apply {
                putExtra("sms_body", "${company.name}, \n")
            }
            try {
                startActivity(Intent.createChooser(smsIntent, getString(R.string.sendTextChooser)))
            } catch (e: Exception) {
                // Most of the times, if not always, app chooser will display a similar message.
                ToastExtension.makeText(this, R.string.noSuitableAppFound)
            }
        }
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

    private fun onViewContactsCompanyDetails() {
        //FILIP - pregled svih kontakata za ovaj company
    }

    override fun onBackPressed() {
        if (isChanged) {
            val intentBack = Intent().apply {
                putExtra(getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT), company)
            }
            setResult(AppCompatActivity.RESULT_OK, intentBack)
        }
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CompanyDetailsActivity.requestCodeEditCompany) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                company = data?.getSerializableExtra(getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT)) as Company
                intent.putExtra(getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT), company)
                isChanged = true
            }
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
}
