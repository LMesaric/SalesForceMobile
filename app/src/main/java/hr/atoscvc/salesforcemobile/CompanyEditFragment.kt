package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_company_edit.*
import kotlinx.android.synthetic.main.fragment_company_edit.view.*

class CompanyEditFragment : Fragment() {

    private var generator = ColorGenerator.MATERIAL

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var company: Company? = null

    private var docRef: DocumentReference? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_company_edit, container, false)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        company = activity?.intent?.getSerializableExtra(getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT)) as? Company

        val keyId: String
        var letter = ""

        if (activity?.intent?.getBooleanExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false) == true) {
            activity?.title = getString(R.string.newCompany)

            docRef = mAuth.uid?.let {
                db.collection(getString(R.string.databaseCollectionUsers))
                        .document(it)
                        .collection(getString(R.string.databaseCollectionCompanies))
                        .document()
            }
            keyId = docRef!!.id
        } else {
            activity?.title = getString(R.string.editCompany)

            keyId = company!!.documentID!!
            letter = company?.name?.get(0).toString().toUpperCase()
        }

        view.ivCompanyEditAvatar.setImageDrawable(TextDrawable.builder().buildRound(letter, generator.getColor(keyId)))

        view.etCompanyName.addTextChangedListener(DrawableTextWatcher(view.ivCompanyEditAvatar, keyId, view.etCompanyName))

        view.spCompanyStatus.setItems(resources.getStringArray(R.array.status_array).toList())
        view.spCompanyCvsSegment.setItems(resources.getStringArray(R.array.companyCVS_array).toList())
        view.spCompanyCommunicationType.setItems(resources.getStringArray(R.array.companyCommunicationType_array).toList())
        view.spCompanyEmployees.setItems(resources.getStringArray(R.array.companyEmployees_array).toList())

        view.etCompanyName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                view.etCompanyName.setText(view.etCompanyName.text.toString().trim())
            }
        }
        view.etCompanyOIB.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                view.etCompanyOIB.setText(view.etCompanyOIB.text.toString().trim())
            }
        }
        view.etCompanyPhone.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                view.etCompanyPhone.setText(view.etCompanyPhone.text.toString().trim())
            }
        }
        view.etCompanyWebPage.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                view.etCompanyWebPage.setText(view.etCompanyWebPage.text.toString().trim())
            }
        }
        view.etCompanyDetails.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                view.etCompanyDetails.setText(view.etCompanyDetails.text.toString().trim())
            }
        }
        view.etCompanyIncome.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                view.etCompanyIncome.setText(view.etCompanyIncome.text.toString().trim())
            }
        }

        view.spCompanyStatus.selectedIndex = company?.status ?: 0
        view.spCompanyCvsSegment.selectedIndex = company?.cvsSegment ?: 0
        view.spCompanyCommunicationType.selectedIndex = company?.communicationType ?: 0
        view.spCompanyEmployees.selectedIndex = company?.employees ?: 0
        view.etCompanyName.setText(company?.name)
        view.etCompanyOIB.setText(company?.OIB)
        view.etCompanyPhone.setText(company?.phone)
        view.etCompanyWebPage.setText(company?.webPage)
        view.etCompanyDetails.setText(company?.details)
        view.etCompanyIncome.setText(company?.income)

        return view
    }

    fun save() {
        var thereAreNoErrors = true

        val status: Int = spCompanyStatus.selectedIndex
        val cvsSegment: Int = spCompanyCvsSegment.selectedIndex
        val communicationType: Int = spCompanyCommunicationType.selectedIndex
        val employees: Int = spCompanyEmployees.selectedIndex

        val oib: String = etCompanyOIB.text.toString().trim()
        val name: String = etCompanyName.text.toString().trim()

        var webPage: String? = etCompanyWebPage.text.toString().trim()
        if (webPage.isNullOrBlank()) {
            webPage = null
        }
        var details: String? = etCompanyDetails.text.toString().trim()
        if (details.isNullOrBlank()) {
            details = null
        }
        var phone: String? = etCompanyPhone.text.toString().trim()
        if (phone.isNullOrBlank()) {
            phone = null
        }
        var income: String? = etCompanyIncome.text.toString().trim()
        if (income.isNullOrBlank()) {
            income = null
        }

        if (name.isEmpty()) {
            etCompanyName.error = getString(R.string.companyNameEmptyMessage)
            thereAreNoErrors = false
        }

        if (oib.isEmpty()) {
            etCompanyOIB.error = getString(R.string.oibEmptyMessage)
            thereAreNoErrors = false
        } else if (!oib.matches("\\d+".toRegex())) {
            etCompanyOIB.error = getString(R.string.oibOnlyDigitsMessage)
            thereAreNoErrors = false
        }

        if (thereAreNoErrors) {
            val messageId: Int
            company = Company(company?.documentID, status, oib, name, webPage, cvsSegment, details, phone, communicationType, employees, income)

            if (activity?.intent?.getBooleanExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false) == true) {
                messageId = R.string.newCompanyCreated
                company?.documentID = docRef?.id
            } else {
                messageId = R.string.companyUpdated
                docRef = db.collection(getString(R.string.databaseCollectionUsers))
                        .document(mAuth.uid!!)
                        .collection(getString(R.string.databaseCollectionCompanies))
                        .document(company!!.documentID!!)
            }

            docRef?.addSnapshotListener(requireActivity()) { documentSnapshot, firebaseFirestoreException ->

                if (firebaseFirestoreException != null) {
                    ToastExtension.makeText(requireActivity(), firebaseFirestoreException.message.toString())
                    return@addSnapshotListener
                }

                documentSnapshot?.reference?.set(company!!)
                ToastExtension.makeText(requireActivity(), messageId)
                activity?.setResult(
                        AppCompatActivity.RESULT_OK,
                        Intent().apply {
                            putExtra(getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT), company)
                        }
                )
                activity?.finish()
            }
        }
    }
}
