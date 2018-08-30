package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import hr.atoscvc.salesforcemobile.ContactsFragment.Companion.contactList
import kotlinx.android.synthetic.main.activity_contact_details.*

//TODO - dodati i direct link na company details
//LUKA - export i import kontakata iz Imenika (paziti da se ne exporta vise puta)

class ContactDetailsActivity : AppCompatActivity() {

    companion object {
        const val requestCodeEditContact = 5
    }

    private var generator = ColorGenerator.MATERIAL

    private lateinit var contact: Contact
    private var isChanged: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_details)
        contact = intent.getSerializableExtra(getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT)) as Contact
    }

    override fun onResume() {
        super.onResume()

        val letters = contact.firstName[0].toString().toUpperCase() + contact.lastName[0].toString().toUpperCase()

        val drawable: TextDrawable = TextDrawable.builder().buildRound(letters, generator.getColor(contact.documentID))
        ivContactDetailsAvatar.setImageDrawable(drawable)

        tvContactDetailsName.text = ConcatenateObjectToString.concatenateContactName(contact, resources, true)
        tvContactDetailsCompanyName.text = contact.company?.name
        tvContactDetailsStatus.text = resources.getStringArray(R.array.status_array)[contact.status]

        if (contact.email.isNullOrBlank()) {
            layoutContactDetailsEmail.visibility = View.GONE
            fabContactDetailsSendEmail.isEnabled = false
        } else {
            tvContactDetailsEmail.text = contact.email
            layoutContactDetailsEmail.visibility = View.VISIBLE
            fabContactDetailsSendEmail.isEnabled = true
        }

        if (contact.phone.isNullOrBlank()) {
            layoutContactDetailsPhoneNumber.visibility = View.GONE
            fabContactDetailsCall.isEnabled = false
            fabContactDetailsSendText.isEnabled = false
        } else {
            tvContactDetailsPhoneNumber.text = contact.phone
            layoutContactDetailsPhoneNumber.visibility = View.VISIBLE
            fabContactDetailsCall.isEnabled = true
            fabContactDetailsSendText.isEnabled = true
        }

        if (contact.details.isNullOrBlank()) {
            layoutContactDetailsDetails.visibility = View.GONE
        } else {
            tvContactDetailsDetails.text = contact.details
            layoutContactDetailsDetails.visibility = View.VISIBLE
        }

        if (contact.preferredTime == 0) {
            layoutContactDetailsPreferredTime.visibility = View.GONE
        } else {
            tvContactDetailsPrefTime.text = resources.getStringArray(R.array.contactPreferredTime_array)[contact.preferredTime]
            layoutContactDetailsPreferredTime.visibility = View.VISIBLE
        }

        val shouldHideButtons: Boolean = intent.getBooleanExtra(getString(R.string.EXTRA_CONTACT_HIDE_EDIT_BUTTONS), false)
        tvContactDetailsEdit.isEnabled = !shouldHideButtons
    }

    fun onContactEmail(@Suppress("UNUSED_PARAMETER") view: View) {
        val email: String? = contact.email?.trim()
        if (email.isNullOrBlank()) {
            Toast.makeText(this, getString(R.string.wrongEmail), Toast.LENGTH_SHORT).show()
        } else {
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:")).apply {
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_TEXT, "${getString(R.string.emailGreeting)} ${ConcatenateObjectToString.concatenateContactName(contact, resources, true)}, \n\n")
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
                putExtra("sms_body", "${getString(R.string.smsGreeting)} ${ConcatenateObjectToString.concatenateContactName(contact, resources, false)}, \n")
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
                intent.putExtra(getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT), contact)
                isChanged = true
            }
        }
    }
}
