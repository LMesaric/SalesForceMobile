package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_contact_details.*

class ContactDetailsActivity : AppCompatActivity() {

    private lateinit var contact: Contact
    private var isChanged: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_details)
        contact = intent.getSerializableExtra(getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT)) as Contact
    }

    override fun onResume() {
        super.onResume()
        tvContactDetailsFirstName.text = contact.firstName
        tvContactDetailsLastName.text = contact.lastName
        tvContactDetailsEmail.text = contact.email
        tvContactDetailsPhoneNumber.text = contact.phone
        tvContactDetailsDetails.text = contact.details
    }

    fun onEditContactDetails(@Suppress("UNUSED_PARAMETER") view: View) {
        val intent = Intent(this, ContactEditorActivity::class.java).apply {
            putExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false)
            putExtra(getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT), contact)
        }
        ContactEditFragment.chosenCompany = contact.company
        startActivityForResult(intent, 4)
        //TODO Testirati radi li implementirani refresh RecycleViewa nakon Savea
    }

    override fun onBackPressed() {
        if (isChanged) {
            val intent = Intent()
            intent.putExtra(getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT), contact)
            setResult(AppCompatActivity.RESULT_OK, intent)
        }
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 4) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                contact = data?.getSerializableExtra(getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT)) as Contact
                isChanged = true
            }
        }
    }

}
