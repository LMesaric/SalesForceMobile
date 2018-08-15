package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

//LUKA - urediti login i register screen, staviti animacije + trimmati sve inpute za login i register

class ContactEditorActivity : AppCompatActivity(), ReplaceFragmentListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_editor)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        replaceFragment(ContactEditFragment())

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

    override fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        if (fragment.javaClass == ContactEditFragment().javaClass) {
            fragmentTransaction.replace(R.id.contactEditorContainer, fragment)
        } else {
            fragmentTransaction.replace(R.id.contactEditorContainer, fragment).addToBackStack("tag")
        }
        fragmentTransaction.commit()
    }
    fun onSaveClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        //LUKA Prikazati sve errore, a podatke iscitati iz polja na ekranu (osim chosenCompany) i stvoriti objekt u varijabli 'contact'
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
}
