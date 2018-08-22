package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_contact_details.*

//TODO - dodati i direct link na company details
//LUKA - export i import kontakata iz Imenika (paziti da se ne exporta vise puta)

class ContactDetailsActivity : AppCompatActivity() {

    companion object {
        const val requestCodeEditContact = 5
    }

    private lateinit var contact: Contact
    private var isChanged: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_details)
        contact = intent.getSerializableExtra(getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT)) as Contact
    }

    override fun onResume() {
        super.onResume()

        tvContactDetailsName.text = ContactNameConcatenate.fullName(contact, resources, true)
        tvContactDetailsCompanyName.text = contact.company?.name
        tvContactDetailsStatus.text = resources.getStringArray(R.array.status_array)[contact.status]
        tvContactDetailsEmail.text = contact.email
        tvContactDetailsPhoneNumber.text = contact.phone
        tvContactDetailsPrefTime.text = resources.getStringArray(R.array.contactPreferredTime_array)[contact.preferredTime]
        tvContactDetailsDetails.text = contact.details

        val shouldHideButtons: Boolean = intent.getBooleanExtra(getString(R.string.EXTRA_CONTACT_HIDE_EDIT_BUTTONS), false)
        if (shouldHideButtons) {
            btnContactDetailsEdit.visibility = View.GONE
            btnContactDetailsEdit.isEnabled = false
        } else {
            btnContactDetailsEdit.visibility = View.VISIBLE
            btnContactDetailsEdit.isEnabled = true
        }
    }

    fun onContactEmail(@Suppress("UNUSED_PARAMETER") view: View) {
        val email: String? = contact.email?.trim()
        if (email.isNullOrBlank()) {
            Toast.makeText(this, getString(R.string.wrongEmail), Toast.LENGTH_SHORT).show()
        } else {
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:")).apply {
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_TEXT, "${getString(R.string.emailGreeting)} ${ContactNameConcatenate.fullName(contact, resources, true)}, \n\n")
            }
            try {
                startActivity(Intent.createChooser(emailIntent, getString(R.string.sendEmailChooser)))
            } catch (e: Exception) {
                // Most of the times, if not always, app chooser will display a similar message.
                Toast.makeText(this, getString(R.string.noSuitableEmailAppFound), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun onContactCall(@Suppress("UNUSED_PARAMETER") view: View) {
        val phone: String? = contact.phone?.trim()
        if (phone.isNullOrBlank()) {
            Toast.makeText(this, getString(R.string.wrongPhoneNumber), Toast.LENGTH_SHORT).show()
        } else {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            try {
                startActivity(Intent.createChooser(dialIntent, getString(R.string.dialPhoneChooser)))
            } catch (e: Exception) {
                // Most of the times, if not always, app chooser will display a similar message.
                Toast.makeText(this, getString(R.string.noSuitableAppFound), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun onContactText(@Suppress("UNUSED_PARAMETER") view: View) {
        val phone: String? = contact.phone?.trim()
        if (phone.isNullOrBlank()) {
            Toast.makeText(this, getString(R.string.wrongPhoneNumber), Toast.LENGTH_SHORT).show()
        } else {
            val smsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("smsto:$phone")).apply {
                putExtra("sms_body", "${getString(R.string.smsGreeting)} ${ContactNameConcatenate.fullName(contact, resources, false)}, \n")
            }
            try {
                startActivity(Intent.createChooser(smsIntent, getString(R.string.sendTextChooser)))
            } catch (e: Exception) {
                // Most of the times, if not always, app chooser will display a similar message.
                Toast.makeText(this, getString(R.string.noSuitableAppFound), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun onEditContactDetails(@Suppress("UNUSED_PARAMETER") view: View) {
        val intentEdit = Intent(this, ContactEditorActivity::class.java).apply {
            putExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false)
            putExtra(getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT), contact)
        }
        ContactEditFragment.chosenCompany = contact.company
        startActivityForResult(intentEdit, ContactDetailsActivity.requestCodeEditContact)
        //TODO Testirati radi li implementirani refresh RecycleViewa nakon Savea
    }

    override fun onBackPressed() {
        if (isChanged) {
            val intentBack = Intent().apply {
                putExtra(getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT), contact)
            }
            setResult(AppCompatActivity.RESULT_OK, intentBack)
        }
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ContactDetailsActivity.requestCodeEditContact) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                contact = data?.getSerializableExtra(getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT)) as Contact
                isChanged = true
            }
        }
    }
}
