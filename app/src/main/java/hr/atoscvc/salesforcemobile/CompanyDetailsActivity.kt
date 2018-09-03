package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_details)
        company = intent.getSerializableExtra(getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT)) as Company
    }

    override fun onResume() {
        super.onResume()

        val letters = company.name[0].toString().toUpperCase()
        val drawable: TextDrawable = TextDrawable.builder().buildRound(letters, generator.getColor(company.documentID))
        ivCompanyDetailsAvatar.setImageDrawable(drawable)

        tvCompanyDetailsName.text = company.name
        tvCompanyDetailsStatus.text = resources.getStringArray(R.array.status_array)[company.status]
        tvCompanyDetailsOib.text = company.OIB

        if (company.cvsSegment == 0) {
            tvCompanyDetailsCvsSegment.visibility = View.GONE
        } else {
            tvCompanyDetailsCvsSegment.text = resources.getStringArray(R.array.companyCVS_array)[company.cvsSegment]
            tvCompanyDetailsCvsSegment.visibility = View.VISIBLE
        }

        if (company.communicationType == 0) {
            tvCompanyDetailsCommunicationType.visibility = View.GONE
        } else {
            tvCompanyDetailsCommunicationType.text = resources.getStringArray(R.array.companyCommunicationType_array)[company.communicationType]
            tvCompanyDetailsCommunicationType.visibility = View.VISIBLE
        }

        if (company.employees == 0) {
            tvCompanyDetailsEmployees.visibility = View.GONE
        } else {
            tvCompanyDetailsEmployees.text = resources.getStringArray(R.array.companyEmployees_array)[company.employees]
            tvCompanyDetailsEmployees.visibility = View.VISIBLE
        }

        if (company.webPage.isNullOrBlank()) {
            tvCompanyDetailsWebPage.visibility = View.GONE
            fabCompanyDetailsOpenWebPage.hide()
        } else {
            tvCompanyDetailsWebPage.text = company.webPage
            tvCompanyDetailsWebPage.visibility = View.VISIBLE
            fabCompanyDetailsOpenWebPage.show()
        }

        if (company.phone.isNullOrBlank()) {
            tvCompanyDetailsPhoneNumber.visibility = View.GONE
            fabCompanyDetailsCall.hide()
            fabCompanyDetailsSendText.hide()
        } else {
            tvCompanyDetailsPhoneNumber.text = company.phone
            tvCompanyDetailsPhoneNumber.visibility = View.VISIBLE
            fabCompanyDetailsCall.show()
            fabCompanyDetailsSendText.show()
        }

        if (company.income.isNullOrBlank()) {
            tvCompanyDetailsIncome.visibility = View.GONE
        } else {
            tvCompanyDetailsIncome.text = company.income
            tvCompanyDetailsIncome.visibility = View.VISIBLE
        }

        if (company.details.isNullOrBlank()) {
            tvCompanyDetailsDetails.visibility = View.GONE
        } else {
            tvCompanyDetailsDetails.text = company.details
            tvCompanyDetailsDetails.visibility = View.VISIBLE
        }

        val shouldHideButtons: Boolean = intent.getBooleanExtra(getString(R.string.EXTRA_COMPANY_HIDE_EDIT_BUTTONS), false)
        if (shouldHideButtons) {
            layoutCompanyDetailsButtons.visibility = View.GONE
            allButtonsEnabledStatus(false)
        } else {
            layoutCompanyDetailsButtons.visibility = View.VISIBLE
            allButtonsEnabledStatus(true)
        }
    }

    fun onCompanyWebPage(@Suppress("UNUSED_PARAMETER") view: View) {
        var webPage: String? = company.webPage?.trim()
        if (webPage.isNullOrBlank()) {
            SnackbarCustom.showAutoClose(tvCompanyDetailsWebPage, R.string.wrongWebPage)
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
                SnackbarCustom.showAutoClose(tvCompanyDetailsWebPage, R.string.noSuitableAppFound)
            }
        }
    }

    fun onCompanyCall(@Suppress("UNUSED_PARAMETER") view: View) {
        val phone: String? = company.phone?.trim()
        if (phone.isNullOrBlank()) {
            SnackbarCustom.showAutoClose(tvCompanyDetailsPhoneNumber, R.string.wrongPhoneNumber)
        } else {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            try {
                startActivity(Intent.createChooser(dialIntent, getString(R.string.dialPhoneChooser)))
            } catch (e: Exception) {
                // Most of the times, if not always, app chooser will display a similar message.
                SnackbarCustom.showAutoClose(tvCompanyDetailsPhoneNumber, R.string.noSuitableAppFound)
            }
        }
    }

    fun onCompanyText(@Suppress("UNUSED_PARAMETER") view: View) {
        val phone: String? = company.phone?.trim()
        if (phone.isNullOrBlank()) {
            SnackbarCustom.showAutoClose(tvCompanyDetailsPhoneNumber, R.string.wrongPhoneNumber)
        } else {
            val smsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("smsto:$phone")).apply {
                putExtra("sms_body", "${company.name}, \n")
            }
            try {
                startActivity(Intent.createChooser(smsIntent, getString(R.string.sendTextChooser)))
            } catch (e: Exception) {
                // Most of the times, if not always, app chooser will display a similar message.
                SnackbarCustom.showAutoClose(tvCompanyDetailsPhoneNumber, R.string.noSuitableAppFound)
            }
        }
    }

    fun onEditCompanyDetails(@Suppress("UNUSED_PARAMETER") view: View) {
        val intentEdit = Intent(this, CompanyEditorActivity::class.java).apply {
            putExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false)
            putExtra(getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT), company)
        }
        startActivityForResult(intentEdit, CompanyDetailsActivity.requestCodeEditCompany)
    }

    fun onAddContactCompanyDetails(@Suppress("UNUSED_PARAMETER") view: View) {
        val intent = Intent(this, ContactEditorActivity::class.java).apply {
            putExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), true)
            putExtra(getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT), company)
        }
        ContactEditFragment.chosenCompany = company
        startActivity(intent)
    }

    fun onViewContactsCompanyDetails(@Suppress("UNUSED_PARAMETER") view: View) {
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
                val message: String? = data.getStringExtra(getString(R.string.EXTRA_SNACKBAR_TEXT))
                if (message != null) {
                    SnackbarCustom.showAutoClose(tvCompanyDetailsName, message)
                }
                isChanged = true
            }
        }
    }

    private fun allButtonsEnabledStatus(status: Boolean) {
        btnCompanyDetailsEdit.isEnabled = status
        btnCompanyDetailsAddContact.isEnabled = status
        btnCompanyDetailsViewContacts.isEnabled = status
    }
}
