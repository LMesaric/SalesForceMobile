package hr.atoscvc.salesforcemobile

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_company_editor.*

class CompanyEditorActivity : AppCompatActivity() {

    //TODO Ovako se dobiva string iz indeksa u bazi:  resources.getStringArray(R.array.contactTitle_array)[index]
    //TODO Ovako se dobiva indeks selektiranog u spinneru:  spTitleContact.selectedItemPosition.toString()

    //TODO Za Company i Contact Editor staviti alert dialog na back button

    private lateinit var mAuth: FirebaseAuth
    private var company: Company? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_editor)

        mAuth = FirebaseAuth.getInstance()

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
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun onUserInteraction() {
        (application as MyApp).onUserInteracted()
    }

    fun onSaveClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        //LUKA Prikazati sve errore, a podatke iscitati iz polja na ekranu i stvoriti objekt u varijabli 'company'
        if (intent.getBooleanExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false)) {
            //FILIP Stvara se novi Company -> save u bazu
            Toast.makeText(this, "New Company created", Toast.LENGTH_SHORT).show()
        } else {
            val documentID = company?.documentID
            if (documentID.isNullOrBlank()) {
                Toast.makeText(this, "Unknown error occurred", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_CANCELED)
                finish()
            } else {
                //FILIP Sprema se edit postojeceg Companyja -> update u bazu
                Toast.makeText(this, "Company updated", Toast.LENGTH_SHORT).show()
            }
        }

        setResult(RESULT_OK)    //Samo ako nema errora
        finish()
    }
}
