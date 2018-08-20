package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_company_editor.*

class CompanyEditorActivity : AppCompatActivity() {

    //TODO Ovako se dobiva string iz indeksa u bazi:  resources.getStringArray(R.array.contactTitle_array)[index]
    //TODO Ovako se dobiva indeks selektiranog u spinneru:  spTitleContact.selectedItemPosition.toString()

    //TODO Za Company i Contact Editor staviti alert dialog na back button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var company: Company? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_editor)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        company = intent.getSerializableExtra(getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT)) as? Company

        if (intent.getBooleanExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false)) {
            this.title = getString(R.string.newCompany)
        } else {
            this.title = getString(R.string.editCompany)
        }

        spCompanyStatus.adapter = ArrayAdapter.createFromResource(
                this,
                R.array.status_array,
                R.layout.simple_spinner_dropdown_item
        )

        spCompanyCvsSegment.adapter = ArrayAdapter.createFromResource(
                this,
                R.array.companyCVS_array,
                R.layout.simple_spinner_dropdown_item
        )

        spCompanyCommunicationType.adapter = ArrayAdapter.createFromResource(
                this,
                R.array.companyCommunicationType_array,
                R.layout.simple_spinner_dropdown_item
        )

        spCompanyEmployees.adapter = ArrayAdapter.createFromResource(
                this,
                R.array.companyEmployees_array,
                R.layout.simple_spinner_dropdown_item
        )

        //TODO OIB ima fiksan broj znamenaka (mozda druge drzave to nemaju?)

        //LUKA - Company i Contact paziti da nije out of range
        spCompanyStatus.setSelection(company?.status ?: 0)
        spCompanyCvsSegment.setSelection(company?.cvsSegment ?: 0)
        spCompanyCommunicationType.setSelection(company?.communicationType ?: 0)
        spCompanyEmployees.setSelection(company?.employees ?: 0)
        etCompanyName.setText(company?.name)
        etCompanyOIB.setText(company?.OIB)
        etCompanyWebPage.setText(company?.webPage)
        etCompanyPhone.setText(company?.phone)
        etCompanyDetails.setText(company?.details)
        etCompanyIncome.setText(company?.income)

    }

    override fun onResume() {
        super.onResume()
        val user: FirebaseUser? = mAuth.currentUser
        if (user == null) {
            sendToLogin()
        }
    }

    private fun sendToLogin() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(loginIntent)
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun onUserInteraction() {
        (application as MyApp).onUserInteracted()
    }

    fun onSaveClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        var thereAreNoErrors = true

        val status: Int = spCompanyStatus.selectedItemPosition
        val cvsSegment: Int = spCompanyCvsSegment.selectedItemPosition
        val communicationType: Int = spCompanyCommunicationType.selectedItemPosition
        val employees: Int = spCompanyEmployees.selectedItemPosition
        val oib: String = etCompanyOIB.text.toString().trim()
        val name: String = etCompanyName.text.toString().trim()
        val income: String? = etCompanyIncome.text.toString().trim()

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

        //LUKA - Dovrsiti popis...

        if (thereAreNoErrors) {
            if (intent.getBooleanExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false)) {
                val docRef: DocumentReference? = mAuth.uid?.let { db.collection("Users").document(it).collection("Companies").document() }
                company = Company(docRef?.id, status, oib, name, webPage, cvsSegment, details, phone, communicationType, employees, income)
                company?.let { docRef?.set(it) }?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "New Company created", Toast.LENGTH_SHORT).show()
                        setResult(AppCompatActivity.RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                company = Company(company?.documentID, status, oib, name, webPage, cvsSegment, details, phone, communicationType, employees, income)
                mAuth.uid?.let { company?.let { it1 -> db.collection("Users").document(it).collection("Companies").document(company?.documentID.toString()).set(it1) } }?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Company updated", Toast.LENGTH_LONG).show()
                        setResult(AppCompatActivity.RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

        }
    }
}
