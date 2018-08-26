package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_company_edit.view.*

//TODO - Valuta za income

class CompanyEditFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var company: Company? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_company_edit, container, false)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        company = activity?.intent?.getSerializableExtra(getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT)) as? Company

        //FILIP - ovi naslovi se trebaju negdje prikazati (i za Contact)
        if (activity?.intent?.getBooleanExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false) == true) {
            activity?.title = getString(R.string.newCompany)
        } else {
            activity?.title = getString(R.string.editCompany)
        }

        view.btnCompanySave.setOnClickListener { _ ->
            var thereAreNoErrors = true

            val status: Int = view.spCompanyStatus.selectedItemPosition
            val cvsSegment: Int = view.spCompanyCvsSegment.selectedItemPosition
            val communicationType: Int = view.spCompanyCommunicationType.selectedItemPosition
            val employees: Int = view.spCompanyEmployees.selectedItemPosition

            val oib: String = view.etCompanyOIB.text.toString().trim()
            val name: String = view.etCompanyName.text.toString().trim()

            var webPage: String? = view.etCompanyWebPage.text.toString().trim()
            if (webPage.isNullOrBlank()) {
                webPage = null
            }
            var details: String? = view.etCompanyDetails.text.toString().trim()
            if (details.isNullOrBlank()) {
                details = null
            }
            var phone: String? = view.etCompanyPhone.text.toString().trim()
            if (phone.isNullOrBlank()) {
                phone = null
            }
            var income: String? = view.etCompanyIncome.text.toString().trim()
            if (income.isNullOrBlank()) {
                income = null
            }

            if (name.isEmpty()) {
                view.etCompanyName.error = getString(R.string.companyNameEmptyMessage)
                thereAreNoErrors = false
            }

            if (oib.isEmpty()) {
                view.etCompanyOIB.error = getString(R.string.oibEmptyMessage)
                thereAreNoErrors = false
            } else if (!oib.matches("\\d+".toRegex())) {
                view.etCompanyOIB.error = getString(R.string.oibOnlyDigitsMessage)
                thereAreNoErrors = false
            }

            if (thereAreNoErrors) {
                company = Company(company?.documentID, status, oib, name, webPage, cvsSegment, details, phone, communicationType, employees, income)

                if (activity?.intent?.getBooleanExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false) == true) {
                    val docRef: DocumentReference? = mAuth.uid?.let {
                        db.collection(getString(R.string.databaseCollectionUsers))
                                .document(it)
                                .collection(getString(R.string.databaseCollectionCompanies))
                                .document()
                    }
                    company?.documentID = docRef?.id
                    company?.let { docRef?.set(it) }?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(activity, getString(R.string.newCompanyCreated), Toast.LENGTH_SHORT).show()
                            activity?.setResult(
                                    AppCompatActivity.RESULT_OK,
                                    Intent().apply {
                                        putExtra(getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT), company)
                                    }
                            )
                            activity?.finish()
                        } else {
                            Toast.makeText(activity, task.exception?.message, Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    mAuth.uid?.let {
                        company?.let { it1 ->
                            db.collection(getString(R.string.databaseCollectionUsers))
                                    .document(it)
                                    .collection(getString(R.string.databaseCollectionCompanies))
                                    .document(company?.documentID.toString())
                                    .set(it1)
                        }
                    }?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(activity, getString(R.string.companyUpdated), Toast.LENGTH_LONG).show()
                            activity?.setResult(
                                    AppCompatActivity.RESULT_OK,
                                    Intent().apply {
                                        putExtra(getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT), company)
                                    }
                            )
                            activity?.finish()
                        } else {
                            Toast.makeText(activity, task.exception?.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        view.spCompanyStatus.adapter = ArrayAdapter.createFromResource(
                activity?.baseContext!!,
                R.array.status_array,
                R.layout.simple_spinner_dropdown_item
        )

        view.spCompanyCvsSegment.adapter = ArrayAdapter.createFromResource(
                activity?.baseContext!!,
                R.array.companyCVS_array,
                R.layout.simple_spinner_dropdown_item
        )

        view.spCompanyCommunicationType.adapter = ArrayAdapter.createFromResource(
                activity?.baseContext!!,
                R.array.companyCommunicationType_array,
                R.layout.simple_spinner_dropdown_item
        )

        view.spCompanyEmployees.adapter = ArrayAdapter.createFromResource(
                activity?.baseContext!!,
                R.array.companyEmployees_array,
                R.layout.simple_spinner_dropdown_item
        )

        view.spCompanyStatus.setSelection(company?.status ?: 0)
        view.spCompanyCvsSegment.setSelection(company?.cvsSegment ?: 0)
        view.spCompanyCommunicationType.setSelection(company?.communicationType ?: 0)
        view.spCompanyEmployees.setSelection(company?.employees ?: 0)
        view.etCompanyName.setText(company?.name)
        view.etCompanyOIB.setText(company?.OIB)
        view.etCompanyWebPage.setText(company?.webPage)
        view.etCompanyPhone.setText(company?.phone)
        view.etCompanyDetails.setText(company?.details)
        view.etCompanyIncome.setText(company?.income)

        return view
    }
}
