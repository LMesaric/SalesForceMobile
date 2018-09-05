package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_contact_edit.*
import kotlinx.android.synthetic.main.fragment_contact_edit.view.*

class ContactEditFragment : Fragment() {

    companion object {
        var chosenCompany: Company? = null
    }

    private var generator = ColorGenerator.MATERIAL

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var contact: Contact? = null

    private var docRef: DocumentReference? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_contact_edit, container, false)

        val replaceFragmentListener = activity as ReplaceFragmentListener

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        contact = activity?.intent?.getSerializableExtra(getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT)) as? Contact

        var letters: String
        //Integer.toHexString(generator.getColor(contact.documentID))
        var drawable: TextDrawable

        if (activity?.intent?.getBooleanExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false) == true) {
            docRef = mAuth.uid?.let {
                db.collection(getString(R.string.databaseCollectionUsers))
                        .document(it)
                        .collection(getString(R.string.databaseCollectionContacts))
                        .document()
            }
            drawable = TextDrawable.builder().buildRound("", generator.getColor(docRef?.id))
        } else {
            letters = contact?.firstName?.get(0).toString().toUpperCase() + contact?.lastName?.get(0).toString().toUpperCase()
            drawable = TextDrawable.builder().buildRound(letters, generator.getColor(contact?.documentID))
        }

        view.ivContactEditAvatar.setImageDrawable(drawable)

        if (activity?.intent?.getBooleanExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false) == true) {
            activity?.title = getString(R.string.newContact)
        } else {
            activity?.title = getString(R.string.editContact)
        }

        view.btnContactEditChooseCompany.setOnClickListener {
            //FILIP Osim biranja Companyja, trebalo bi omoguciti i kreiranje novog u istom prozoru -> problem vracanja podatka onome koji poziva - uopce nema searcha!!
            val bundle = Bundle()
            bundle.putBoolean(getString(R.string.EXTRA_COMPANY_IS_LIST_FOR_SELECT), true)
            (activity as ContactEditorActivity).companiesFragment.arguments = bundle
            replaceFragmentListener.replaceFragment((activity as ContactEditorActivity).companiesFragment)
        }

        view.spContactTitle.setAdapter(ArrayAdapter.createFromResource(
                activity?.baseContext!!,
                R.array.contactTitle_array,
                R.layout.simple_spinner_dropdown_item
        ))

        view.spContactStatus.setAdapter(ArrayAdapter.createFromResource(
                activity?.baseContext!!,
                R.array.status_array,
                R.layout.simple_spinner_dropdown_item
        ))

        view.spContactPreferredTime.setAdapter(ArrayAdapter.createFromResource(
                activity?.baseContext!!,
                R.array.contactPreferredTime_array,
                R.layout.simple_spinner_dropdown_item
        ))

        view.etContactFirstName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            var firstNameLetter = ""
            var lastNameLetter = ""
            if (!hasFocus) {
                if (!view.etContactFirstName.text.toString().trim().isBlank()) {
                    firstNameLetter = view.etContactFirstName.text.toString()[0].toString().toUpperCase()
                }
                if (!view.etContactLastName.text.toString().trim().isBlank()) {
                    lastNameLetter = view.etContactLastName.text.toString()[0].toString().toUpperCase()
                }
                letters = firstNameLetter + lastNameLetter
                //Integer.toHexString(generator.getColor(contact.documentID))
                drawable = if (activity?.intent?.getBooleanExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false) == true) {
                    TextDrawable.builder().buildRound(letters, generator.getColor(docRef?.id))
                } else {
                    TextDrawable.builder().buildRound(letters, generator.getColor(contact?.documentID))
                }

                view.ivContactEditAvatar.setImageDrawable(drawable)
                view.etContactFirstName.setText(view.etContactFirstName.text.toString().trim())
            }
        }
        view.etContactLastName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            var firstNameLetter = ""
            var lastNameLetter = ""
            if (!hasFocus) {
                if (!view.etContactFirstName.text.toString().trim().isBlank()) {
                    firstNameLetter = view.etContactFirstName.text.toString()[0].toString().toUpperCase()
                }
                if (!view.etContactLastName.text.toString().trim().isBlank()) {
                    lastNameLetter = view.etContactLastName.text.toString()[0].toString().toUpperCase()
                }
                letters = firstNameLetter + lastNameLetter
                //Integer.toHexString(generator.getColor(contact.documentID))
                drawable = if (activity?.intent?.getBooleanExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false) == true) {
                    TextDrawable.builder().buildRound(letters, generator.getColor(docRef?.id))
                } else {
                    TextDrawable.builder().buildRound(letters, generator.getColor(contact?.documentID))
                }

                view.ivContactEditAvatar.setImageDrawable(drawable)
                view.etContactFirstName.setText(view.etContactFirstName.text.toString().trim())
            }
        }
        view.etContactPhone.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                view.etContactPhone.setText(view.etContactPhone.text.toString().trim())
            }
        }
        view.etContactEmail.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                view.etContactEmail.setText(view.etContactEmail.text.toString().trim())
            }
        }

        //LUKA Listener for Details

        view.spContactTitle.selectedIndex = (contact?.title ?: 0)
        view.spContactStatus.selectedIndex = (contact?.status ?: 0)
        view.spContactPreferredTime.selectedIndex = (contact?.preferredTime ?: 0)
        view.etContactFirstName.setText(contact?.firstName)
        view.etContactLastName.setText(contact?.lastName)
        view.etContactCompanyName.setText(chosenCompany?.name)
        view.etContactPhone.setText(contact?.phone)
        view.etContactEmail.setText(contact?.email)
        view.etContactDetails.setText(contact?.details)

        return view
    }

    fun save() {
        var thereAreNoErrors = true

        val status: Int = spContactStatus.selectedIndex
        val title: Int = spContactTitle.selectedIndex
        val prefTime: Int = spContactPreferredTime.selectedIndex

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

        var details: String? = etContactDetails.text.toString().trim()
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
        if (email != null && !CheckEmailValidity.isEmailValid(email)) {
            etContactEmail.error = getString(R.string.malformedEmailMessage)
            thereAreNoErrors = false
        }
        if (chosenCompany == null) {
            etContactCompanyName.error = getString(R.string.noCompanyChosenMessage)
            thereAreNoErrors = false
        }

        if (thereAreNoErrors) {
            if (activity?.intent?.getBooleanExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false) == true) {
                contact = chosenCompany?.let { Contact(docRef?.id, status, title, firstName, lastName, it, phone, email, prefTime, details) }
                contact?.let { docRef?.set(it) }?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        ToastExtension.makeText(requireActivity(), R.string.newContactCreated)
                        activity?.setResult(
                                AppCompatActivity.RESULT_OK,
                                Intent().apply {
                                    putExtra(getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT), contact)
                                }
                        )
                        activity?.finish()
                    } else {
                        ToastExtension.makeText(requireActivity(), task.exception?.message.toString())
                    }
                }
            } else {
                contact = chosenCompany?.let { Contact(contact?.documentID, status, title, firstName, lastName, it, phone, email, prefTime, details) }
                mAuth.uid?.let {
                    contact?.let { it1 ->
                        db.collection(getString(R.string.databaseCollectionUsers))
                                .document(it)
                                .collection(getString(R.string.databaseCollectionContacts))
                                .document(contact?.documentID.toString())
                                .set(it1)
                    }
                }?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        ToastExtension.makeText(requireActivity(), R.string.contactUpdated)
                        activity?.setResult(
                                AppCompatActivity.RESULT_OK,
                                Intent().apply {
                                    putExtra(getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT), contact)
                                }
                        )
                        activity?.finish()
                    } else {
                        ToastExtension.makeText(requireActivity(), task.exception?.message.toString())
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        etContactCompanyName.setText(chosenCompany?.name)
    }
}
