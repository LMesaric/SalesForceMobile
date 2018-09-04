package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_contact_edit.*
import kotlinx.android.synthetic.main.fragment_contact_edit.view.*

class ContactEditFragment : Fragment() {

    companion object {
        var chosenCompany: Company? = null
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var contact: Contact? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_contact_edit, container, false)

        val replaceFragmentListener = activity as ReplaceFragmentListener

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        contact = activity?.intent?.getSerializableExtra(getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT)) as? Contact

        if (activity?.intent?.getBooleanExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false) == true) {
            activity?.title = getString(R.string.newContact)
        } else {
            activity?.title = getString(R.string.editContact)
        }

        view.btnContactChooseCompany.setOnClickListener {
            //FILIP Osim biranja Companyja, trebalo bi omoguciti i kreiranje novog u istom prozoru -> problem vracanja podatka onome koji poziva - uopce nema searcha!!
            val companiesFragment = CompaniesFragment()
            val bundle = Bundle()
            bundle.putBoolean(getString(R.string.EXTRA_COMPANY_IS_LIST_FOR_SELECT), true)
            companiesFragment.arguments = bundle
            replaceFragmentListener.replaceFragment(companiesFragment)
        }

        view.btnContactSave.setOnClickListener { _ ->
            var thereAreNoErrors = true

            val status: Int = view.spContactStatus.selectedItemPosition
            val title: Int = view.spContactTitle.selectedItemPosition
            val prefTime: Int = view.spContactPreferredTime.selectedItemPosition

            val firstName: String = view.etContactFirstName.text.toString().trim()
            val lastName: String = view.etContactLastName.text.toString().trim()

            var phone: String? = view.etContactPhone.text.toString().trim()
            if (phone.isNullOrBlank()) {
                phone = null
            }

            var email: String? = view.etContactEmail.text.toString().trim()
            if (email.isNullOrBlank()) {
                email = null
            }

            var details: String? = view.etContactDetails.text.toString().trim()
            if (details.isNullOrBlank()) {
                details = null
            }

            if (firstName.isEmpty()) {
                view.etContactFirstName.error = getString(R.string.firstNameEmptyMessage)
                thereAreNoErrors = false
            }
            if (lastName.isEmpty()) {
                view.etContactLastName.error = getString(R.string.lastNameEmptyMessage)
                thereAreNoErrors = false
            }
            if (email != null && !CheckEmailValidity.isEmailValid(email)) {
                view.etContactEmail.error = getString(R.string.malformedEmailMessage)
                thereAreNoErrors = false
            }
            if (chosenCompany == null) {
                view.etContactCompanyName.error = getString(R.string.noCompanyChosenMessage)
                thereAreNoErrors = false
            }

            if (thereAreNoErrors) {
                if (activity?.intent?.getBooleanExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false) == true) {
                    val docRef: DocumentReference? = mAuth.uid?.let {
                        db.collection(getString(R.string.databaseCollectionUsers))
                                .document(it)
                                .collection(getString(R.string.databaseCollectionContacts))
                                .document()
                    }
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

        view.spContactTitle.adapter = ArrayAdapter.createFromResource(
                activity?.baseContext!!,
                R.array.contactTitle_array,
                R.layout.simple_spinner_dropdown_item
        )

        view.spContactStatus.adapter = ArrayAdapter.createFromResource(
                activity?.baseContext!!,
                R.array.status_array,
                R.layout.simple_spinner_dropdown_item
        )

        view.spContactPreferredTime.adapter = ArrayAdapter.createFromResource(
                activity?.baseContext!!,
                R.array.contactPreferredTime_array,
                R.layout.simple_spinner_dropdown_item
        )

        view.etContactFirstName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                view.etContactFirstName.setText(view.etContactFirstName.text.toString().trim())
                view.etContactFirstName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_smile, 0, 0, 0)
            } else {
                view.etContactFirstName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_smile_accent, 0, 0, 0)
            }
        }
        view.etContactLastName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                view.etContactLastName.setText(view.etContactLastName.text.toString().trim())
                view.etContactLastName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_smile, 0, 0, 0)
            } else {
                view.etContactLastName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_smile_accent, 0, 0, 0)
            }
        }
        view.etContactPhone.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                view.etContactPhone.setText(view.etContactPhone.text.toString().trim())
                view.etContactPhone.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_phone, 0, 0, 0)
            } else {
                view.etContactPhone.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_phone_accent, 0, 0, 0)
            }
        }
        view.etContactEmail.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                view.etContactEmail.setText(view.etContactEmail.text.toString().trim())
                view.etContactEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_email_outline, 0, 0, 0)
            } else {
                view.etContactEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_email_outline_accent, 0, 0, 0)
            }
        }

        //LUKA Listener for Details

        view.spContactTitle.setSelection(contact?.title ?: 0)
        view.spContactStatus.setSelection(contact?.status ?: 0)
        view.spContactPreferredTime.setSelection(contact?.preferredTime ?: 0)
        view.etContactFirstName.setText(contact?.firstName)
        view.etContactLastName.setText(contact?.lastName)
        view.etContactCompanyName.setText(chosenCompany?.name)
        view.etContactPhone.setText(contact?.phone)
        view.etContactEmail.setText(contact?.email)
        view.etContactDetails.setText(contact?.details)

        return view
    }

    override fun onResume() {
        super.onResume()
        etContactCompanyName.setText(chosenCompany?.name)
    }
}
