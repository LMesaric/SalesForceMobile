package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_company_editor.*

class CompanyEditorActivity : AppCompatActivity() {

    //TODO Ovako se dobiva string iz indeksa u bazi:  resources.getStringArray(R.array.contactTitle_array)[i]
    //TODO Ovako se dobiva indeks selektiranog u spinneru:  spTitleContact.selectedItemPosition.toString()

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_editor)

        mAuth = FirebaseAuth.getInstance()

        if (intent.getBooleanExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false)) {
            this.title = getString(R.string.newCompany)
        } else {
            this.title = getString(R.string.editCompany)
        }

        val adapterStatus = ArrayAdapter.createFromResource(
                this,
                R.array.status_array,
                R.layout.simple_spinner_dropdown_item
        )
        spCompanyStatus.adapter = adapterStatus

        val adapterCvsSegment = ArrayAdapter.createFromResource(
                this,
                R.array.companyCVS_array,
                R.layout.simple_spinner_dropdown_item
        )
        spCompanyCvsSegment.adapter = adapterCvsSegment

        val adapterCommunicationType = ArrayAdapter.createFromResource(
                this,
                R.array.companyCommunicationType_array,
                R.layout.simple_spinner_dropdown_item
        )
        spCompanyCommunicationType.adapter = adapterCommunicationType

        val adapterEmployees = ArrayAdapter.createFromResource(
                this,
                R.array.companyEmployees_array,
                R.layout.simple_spinner_dropdown_item
        )
        spCompanyEmployees.adapter = adapterEmployees

        //TODO OIB ima fiksan broj znamenaka (mozda druge drzave to nemaju?)

        spCompanyStatus.setSelection(intent.getIntExtra(getString(R.string.EXTRA_COMPANY_STATUS_SPINNER_INDEX), 0))
        spCompanyCvsSegment.setSelection(intent.getIntExtra(getString(R.string.EXTRA_COMPANY_CVS_SPINNER_INDEX), 0))
        spCompanyCommunicationType.setSelection(intent.getIntExtra(getString(R.string.EXTRA_COMPANY_COMMUNICATION_TYPE_SPINNER_INDEX), 0))
        spCompanyEmployees.setSelection(intent.getIntExtra(getString(R.string.EXTRA_COMPANY_EMPLOYEES_SPINNER_INDEX), 0))
        etCompanyName.setText(intent.getStringExtra(getString(R.string.EXTRA_COMPANY_NAME)))
        etCompanyOIB.setText(intent.getStringExtra(getString(R.string.EXTRA_COMPANY_OIB)))
        etCompanyWebPage.setText(intent.getStringExtra(getString(R.string.EXTRA_COMPANY_WEB_PAGE)))
        etCompanyPhone.setText(intent.getStringExtra(getString(R.string.EXTRA_COMPANY_PHONE)))
        etCompanyDetails.setText(intent.getStringExtra(getString(R.string.EXTRA_COMPANY_DETAILS)))
        etCompanyIncome.setText(intent.getStringExtra(getString(R.string.EXTRA_COMPANY_INCOME)))
    }

    override fun onResume() {
        super.onResume()
        window.setBackgroundDrawable(BitmapDrawable(
                applicationContext.resources,
                (application as MyApp).getInstance(applicationContext.resources)
        ))
        val user: FirebaseUser? = mAuth.currentUser
        if (user == null) {
            sendToLogin()
        }
    }

    private fun sendToLogin() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(loginIntent)
        finish()
    }

    override fun onUserInteraction() {
        (application as MyApp).onUserInteracted()
    }

}
