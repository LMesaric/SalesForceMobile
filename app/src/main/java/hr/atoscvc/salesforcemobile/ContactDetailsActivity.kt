package hr.atoscvc.salesforcemobile

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_contact_details.*

class ContactDetailsActivity : AppCompatActivity() {

    private lateinit var contact: Contact

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_details)
        contact = intent.getSerializableExtra("Contact") as Contact
    }

    override fun onResume() {
        super.onResume()
        tvContactDetailsFirstName.text = contact.firstName
        tvContactDetailsLastName.text = contact.lastName
        tvContactDetailsEmail.text = contact.email
        tvContactDetailsPhoneNumber.text = contact.phone
        tvContactDetailsDetails.text = contact.details
    }

    fun onEditContactDetails(view: View) {
        val intent = Intent(this, ContactEditorActivity::class.java).apply {
            putExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false)
            putExtra(getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT), contact)
        }
        ContactEditFragment.chosenCompany = contact.company
        startActivityForResult(intent, 4)
        //TODO Testirati radi li implementirani refresh RecycleViewa nakon Savea
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 4) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                contact = data?.getSerializableExtra("Contact") as Contact
            }
        }
    }

    override fun onBackPressed() {
        setResult(AppCompatActivity.RESULT_OK)
        finish()
    }

}
