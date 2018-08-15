package hr.atoscvc.salesforcemobile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_contact_editor.*

//LUKA - urediti login i register screen, staviti animacije + trimmati sve inpute za login i register

class ContactEditorActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private var contact: Contact? = null
    private var chosenCompany: Company? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_editor)

        mAuth = FirebaseAuth.getInstance()

        contact = intent.getSerializableExtra(getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT)) as? Contact

        if (intent.getBooleanExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false)) {
            this.title = getString(R.string.newContact)
            chosenCompany = intent.getSerializableExtra(getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT)) as? Company ?: chosenCompany
        } else {
            this.title = getString(R.string.editContact)
            chosenCompany = contact?.company
        }

        spContactTitle.adapter = ArrayAdapter.createFromResource(
                this,
                R.array.contactTitle_array,
                R.layout.simple_spinner_dropdown_item
        )

        spContactStatus.adapter = ArrayAdapter.createFromResource(
                this,
                R.array.status_array,
                R.layout.simple_spinner_dropdown_item
        )

        spContactPreferredTime.adapter = ArrayAdapter.createFromResource(
                this,
                R.array.contactPreferredTime_array,
                R.layout.simple_spinner_dropdown_item
        )

        etContactFirstName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                etContactFirstName.setText(etContactFirstName.text.toString().trim())
                etContactFirstName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_smile, 0, 0, 0)
            } else {
                etContactFirstName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_smile_accent, 0, 0, 0)
            }
        }
        etContactLastName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                etContactLastName.setText(etContactLastName.text.toString().trim())
                etContactLastName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_smile, 0, 0, 0)
            } else {
                etContactLastName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_smile_accent, 0, 0, 0)
            }
        }
        etContactPhone.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                etContactPhone.setText(etContactPhone.text.toString().trim())
                etContactPhone.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_phone, 0, 0, 0)
            } else {
                etContactPhone.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_phone_accent, 0, 0, 0)
            }
        }
        etContactEmail.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                etContactEmail.setText(etContactEmail.text.toString().trim())
                etContactEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_email_outline, 0, 0, 0)
            } else {
                etContactEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_email_outline_accent, 0, 0, 0)
            }
        }
        //TODO Listener for Details

        spContactTitle.setSelection(contact?.title ?: 0)
        spContactStatus.setSelection(contact?.status ?: 0)
        spContactPreferredTime.setSelection(contact?.preferredTime ?: 0)
        etContactFirstName.setText(contact?.firstName)
        etContactLastName.setText(contact?.lastName)
        etContactCompanyName.setText(chosenCompany?.name)
        etContactPhone.setText(contact?.phone)
        etContactEmail.setText(contact?.email)
        etContactDetails.setText(contact?.details)

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
        finish()
    }

    override fun onUserInteraction() {
        (application as MyApp).onUserInteracted()
    }

    fun onChooseCompanyClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        //TODO Osim biranja Companyja, trebalo bi omoguciti i kreiranje novog u istom prozoru -> problem vracanja podatka kroz intent
        /**val intent = Intent(this, CompanyListActivity::class.java).apply {
        putExtra(getString(R.string.EXTRA_COMPANY_IS_LIST_FOR_SELECT), true)
        }
        startActivityForResult(intent, CompaniesFragment.requestCodeChooseCompany)*/
    }

    fun onSaveClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        var thereAreNoErrors = true

        val status: Int = spContactStatus.selectedItemPosition
        val title: Int = spContactTitle.selectedItemPosition
        val prefTime: Int = spContactPreferredTime.selectedItemPosition
        val firstName: String = etContactFirstName.text.toString().trim()
        val lastName: String = etContactLastName.text.toString().trim()

        var phone: String? = etContactPhone.text.toString().trim()
        if (phone.isNullOrBlank()) {
            phone = null
        }
        var email: String? = etContactEmail.text.toString().trim()
        if (email.isNullOrBlank()) {
            email = null
        }
        var details: String? = etContactEmail.text.toString().trim()
        if (details.isNullOrBlank()) {
            details = null
        }

        if (firstName.isEmpty()) {
            etContactFirstName.error = getString(R.string.firstNameEmptyMessage)
            thereAreNoErrors = false
        }
        if (lastName.isEmpty()) {
            etContactLastName.error = getString(R.string.lastNameEmptyMessage)
            thereAreNoErrors = false
        }
        if (chosenCompany == null) {
            etContactCompanyName.error = getString(R.string.noCompanyChosenMessage)
            thereAreNoErrors = false
        }

        //LUKA - Dovrsiti popis...

        if (thereAreNoErrors) {
            val documentID: String? = contact?.documentID
            contact = Contact(documentID, status, title, firstName, lastName, chosenCompany!!, phone, email, prefTime, details)
            //FILIP Stvara se novi Contact -> save u bazu
            //FILIP Stvara se (ID == null) ili updatea (ID != null) 'contact' -> poruke useru "New Contact created" / "Contact updated"
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CompaniesFragment.requestCodeChooseCompany) {
            if (resultCode == Activity.RESULT_OK) {
                chosenCompany = data?.getSerializableExtra(getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT)) as? Company ?: chosenCompany
                etContactCompanyName.setText(chosenCompany?.name)
            }
        }
    }
}
