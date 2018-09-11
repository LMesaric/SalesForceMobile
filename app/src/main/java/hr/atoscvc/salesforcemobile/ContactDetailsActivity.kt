package hr.atoscvc.salesforcemobile

import android.animation.Animator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import kotlinx.android.synthetic.main.activity_contact_details.*

class ContactDetailsActivity : AppCompatActivity() {

    companion object {
        const val requestCodeEditContact = 5
    }

    private var generator = ColorGenerator.MATERIAL

    private lateinit var contact: Contact
    private var isChanged: Boolean = false

    private lateinit var editItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_details)
        contact = intent.getSerializableExtra(getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT)) as Contact
        setSupportActionBar(toolBarContactDetails)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onResume() {
        super.onResume()

        val letters = contact.firstName[0].toString().toUpperCase() + contact.lastName[0].toString().toUpperCase()
        //Integer.toHexString(generator.getColor(contact.documentID))
        val drawable: TextDrawable = TextDrawable.builder().buildRect(letters, generator.getColor(contact.documentID))
        expandedAvatarContactDetails.setImageDrawable(drawable)

        collapsingToolbarContactDetails.setBackgroundColor(generator.getColor(contact.documentID))

        appBarContactDetails.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, _ ->
            val offsetAlpha = (appBarLayout.y / appBarContactDetails.totalScrollRange)
            expandedAvatarContactDetails.alpha = 1 - (offsetAlpha * -1)
        })

        appBarContactDetails.setExpanded(true, true)
        appBarContactDetails.isActivated = true
        collapsingToolbarContactDetails.title = ConcatenateObjectToString.concatenateContactName(contact, resources, true)

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
            fabContactDetailsCall.isClickable = false
            fabContactDetailsCallSecondary.isClickable = false
            fabContactDetailsSendText.isEnabled = false
        } else {
            tvContactDetailsPhoneNumber.text = contact.phone
            layoutContactDetailsPhoneNumber.visibility = View.VISIBLE
            fabContactDetailsCall.isClickable = true
            fabContactDetailsCallSecondary.isClickable = true
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

        fabContactDetailsCallSecondary.hide()

        fabContactDetailsCall.addOnHideAnimationListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}
            override fun onAnimationEnd(p0: Animator?) {}
            override fun onAnimationCancel(p0: Animator?) {}
            override fun onAnimationStart(p0: Animator?) {
                fabContactDetailsCallSecondary.show()
            }
        })

        fabContactDetailsCall.addOnShowAnimationListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}
            override fun onAnimationEnd(p0: Animator?) {}
            override fun onAnimationCancel(p0: Animator?) {}
            override fun onAnimationStart(p0: Animator?) {
                fabContactDetailsCallSecondary.hide()
            }
        })
    }

    fun onContactEmail(@Suppress("UNUSED_PARAMETER") view: View) {
        val email: String? = contact.email?.trim()
        if (email.isNullOrBlank()) {
            ToastExtension.makeText(this, R.string.wrongEmail)
        } else {
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:")).apply {
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_TEXT, "${getString(R.string.emailGreeting)} ${ConcatenateObjectToString.concatenateContactName(contact, resources, true)}, \n\n")
            }
            try {
                startActivity(Intent.createChooser(emailIntent, getString(R.string.sendEmailChooser)))
            } catch (e: Exception) {
                // Most of the times, if not always, app chooser will display a similar message.
                ToastExtension.makeText(this, R.string.noSuitableEmailAppFound)
            }
        }
    }

    fun onContactCall(@Suppress("UNUSED_PARAMETER") view: View) {
        val phone: String? = contact.phone?.trim()
        if (phone.isNullOrBlank()) {
            ToastExtension.makeText(this, R.string.wrongPhoneNumber)
        } else {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            try {
                startActivity(Intent.createChooser(dialIntent, getString(R.string.dialPhoneChooser)))
            } catch (e: Exception) {
                // Most of the times, if not always, app chooser will display a similar message.
                ToastExtension.makeText(this, R.string.noSuitableAppFound)
            }
        }
    }

    fun onContactText(@Suppress("UNUSED_PARAMETER") view: View) {
        val phone: String? = contact.phone?.trim()
        if (phone.isNullOrBlank()) {
            ToastExtension.makeText(this, R.string.wrongPhoneNumber)
        } else {
            val smsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("smsto:$phone")).apply {
                putExtra("sms_body", "${getString(R.string.smsGreeting)} ${ConcatenateObjectToString.concatenateContactName(contact, resources, false)}, \n")
            }
            try {
                startActivity(Intent.createChooser(smsIntent, getString(R.string.sendTextChooser)))
            } catch (e: Exception) {
                // Most of the times, if not always, app chooser will display a similar message.
                ToastExtension.makeText(this, R.string.noSuitableAppFound)
            }
        }
    }

    private fun onEditContactDetails() {
        val intentEdit = Intent(this, ContactEditorActivity::class.java).apply {
            putExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false)
            putExtra(getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT), contact)
        }
        ContactEditFragment.chosenCompany = contact.company
        startActivityForResult(intentEdit, ContactDetailsActivity.requestCodeEditContact)
    }

    private fun onShareContact() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, contact.toFormattedString(resources))
            type = "text/plain"
        }
        try {
            startActivity(Intent.createChooser(shareIntent, getString(R.string.shareChooser)))
        } catch (e: Exception) {
            // Most of the times, if not always, app chooser will display a similar message.
            ToastExtension.makeText(this, R.string.noSuitableAppFound)
        }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.contact_details_menu, menu)
        editItem = menu?.findItem(R.id.action_contact_edit)!!
        val shouldHideButtons: Boolean = intent.getBooleanExtra(getString(R.string.EXTRA_CONTACT_HIDE_EDIT_BUTTONS), false)
        editItem.isVisible = !shouldHideButtons
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.action_contact_edit -> onEditContactDetails()
            R.id.action_contact_share -> onShareContact()
        }
        return true
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

    fun onCompanyDetails(@Suppress("UNUSED_PARAMETER") vew: View) {
        val intent = Intent(this, CompanyDetailsActivity::class.java)
        intent.putExtra(getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT), contact.company)
        startActivity(intent)
    }
}
