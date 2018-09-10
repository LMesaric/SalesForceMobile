package hr.atoscvc.salesforcemobile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_company_details.*
import kotlinx.android.synthetic.main.fragment_view_company_details.*

class ViewCompanyDetailsFragment : Fragment() {

    private lateinit var company: Company

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        company = activity?.intent?.getSerializableExtra(getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT)) as Company
        return inflater.inflate(R.layout.fragment_view_company_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvCompanyDetailsStatus.text = resources.getStringArray(R.array.status_array)[company.status]
        tvCompanyDetailsOib.text = company.OIB

        fabCompanyDetailsOpenWebPage.setOnClickListener {
            onCompanyWebPage()
        }

        fabCompanyDetailsSendText.setOnClickListener {
            onCompanyText()
        }

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
            (activity as CompanyDetailsActivity).fabCompanyDetailsCall.isClickable = false
        } else {
            tvCompanyDetailsPhoneNumber.text = company.phone
            layoutCompanyDetailsPhoneNumber.visibility = View.VISIBLE
            (activity as CompanyDetailsActivity).fabCompanyDetailsCall.isClickable = true
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
    }

    private fun onCompanyWebPage() {
        var webPage: String? = company.webPage?.trim()
        if (webPage.isNullOrBlank()) {
            ToastExtension.makeText(activity as Activity, R.string.wrongWebPage)
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
                ToastExtension.makeText(activity as Activity, R.string.noSuitableAppFound)
            }
        }
    }

    private fun onCompanyText() {
        val phone: String? = company.phone?.trim()
        if (phone.isNullOrBlank()) {
            ToastExtension.makeText(activity as Activity, R.string.wrongPhoneNumber)
        } else {
            val smsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("smsto:$phone")).apply {
                putExtra("sms_body", "${company.name}, \n")
            }
            try {
                startActivity(Intent.createChooser(smsIntent, getString(R.string.sendTextChooser)))
            } catch (e: Exception) {
                // Most of the times, if not always, app chooser will display a similar message.
                ToastExtension.makeText(activity as Activity, R.string.noSuitableAppFound)
            }
        }
    }


}
