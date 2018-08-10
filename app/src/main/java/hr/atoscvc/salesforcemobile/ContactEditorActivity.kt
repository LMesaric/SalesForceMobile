package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_contact_editor.*

class ContactEditorActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_editor)

        mAuth = FirebaseAuth.getInstance()

        if (intent.getBooleanExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false)) {
            this.title = getString(R.string.newContact)
        } else {
            this.title = getString(R.string.editContact)
        }

        val adapterTitle = ArrayAdapter.createFromResource(
                this,
                R.array.contactTitle_array,
                R.layout.simple_spinner_dropdown_item
        )
        spContactTitle.adapter = adapterTitle

        val adapterStatus = ArrayAdapter.createFromResource(
                this,
                R.array.status_array,
                R.layout.simple_spinner_dropdown_item
        )
        spContactStatus.adapter = adapterStatus

        val adapterPreferredTime = ArrayAdapter.createFromResource(
                this,
                R.array.contactPreferredTime_array,
                R.layout.simple_spinner_dropdown_item
        )
        spContactPreferredTime.adapter = adapterPreferredTime

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
        //FIXME etCompanyNameContact bi trebao imati svoj button i onda search ili create new (nije editable)
        etContactCompanyName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                etContactCompanyName.setText(etContactCompanyName.text.toString().trim())
                etContactCompanyName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_company, 0, 0, 0)
            } else {
                etContactCompanyName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_company_accent, 0, 0, 0)
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

        val contact: Contact? = intent.getSerializableExtra(getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT)) as? Contact
        //LUKA - EXTRA_COMPANY_ENTIRE_OBJECT u intentu?
        spContactTitle.setSelection(contact?.title ?: 0)
        spContactStatus.setSelection(contact?.status ?: 0)
        spContactPreferredTime.setSelection(contact?.preferredTime ?: 0)
        etContactFirstName.setText(contact?.firstName)
        etContactLastName.setText(contact?.lastName)
        etContactCompanyName.setText(contact?.company?.name)
        etContactPhone.setText(contact?.phone)
        etContactEmail.setText(contact?.email)
        etContactDetails.setText(contact?.details)

        /*spContactTitle.setSelection(intent.getIntExtra(getString(R.string.EXTRA_CONTACT_TITLE_SPINNER_INDEX), 0))
        spContactStatus.setSelection(intent.getIntExtra(getString(R.string.EXTRA_CONTACT_STATUS_SPINNER_INDEX), 0))
        spContactPreferredTime.setSelection(intent.getIntExtra(getString(R.string.EXTRA_CONTACT_PREF_TIME_SPINNER_INDEX), 0))
        etContactFirstName.setText(intent.getStringExtra(getString(R.string.EXTRA_CONTACT_FIRST_NAME)))
        etContactLastName.setText(intent.getStringExtra(getString(R.string.EXTRA_CONTACT_LAST_NAME)))
        //FIXME Ovo ne bi trebao biti string (ovisi o tome sto ce se dogoditi s tim poljem)
        etContactCompanyName.setText(intent.getStringExtra(getString(R.string.EXTRA_CONTACT_COMPANY)))
        etContactPhone.setText(intent.getStringExtra(getString(R.string.EXTRA_CONTACT_PHONE)))
        etContactEmail.setText(intent.getStringExtra(getString(R.string.EXTRA_CONTACT_EMAIL)))
        etContactDetails.setText(intent.getStringExtra(getString(R.string.EXTRA_CONTACT_DETAILS)))*/
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

    fun onSaveClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        //TODO Prikazati sve errore, a podatke iscitati iz polja na ekranu
        if (intent.getBooleanExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false)) {
            //TODO Stvara se novi Contact - save u bazu
            Toast.makeText(this, "New Contact created", Toast.LENGTH_SHORT).show()
        } else {
            //TODO Sprema se edit postojeceg Contacta - update u bazu
//            val documentID = intent.getStringExtra(getString(R.string.EXTRA_CONTACT_DOCUMENT_ID))
            val documentID = (intent.getSerializableExtra(getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT)) as? Contact)?.documentID
            if (documentID.isNullOrBlank()) {
                //Ako je null ne spremaj nista u bazu!
            } else {
                Toast.makeText(this, "Contact updated", Toast.LENGTH_SHORT).show()
            }
        }

        finish()    //Samo ako nema errora
    }

}
