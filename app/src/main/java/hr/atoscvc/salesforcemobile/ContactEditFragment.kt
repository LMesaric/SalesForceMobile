package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        val keyId: String
        var letters = ""

        if (activity?.intent?.getBooleanExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false) == true) {
            activity?.title = getString(R.string.newContact)

            docRef = mAuth.uid?.let {
                db.collection(getString(R.string.databaseCollectionUsers))
                        .document(it)
                        .collection(getString(R.string.databaseCollectionContacts))
                        .document()
            }
            keyId = docRef!!.id
        } else {
            activity?.title = getString(R.string.editContact)

            keyId = contact!!.documentID!!
            letters = contact?.firstName?.get(0).toString().toUpperCase() + contact?.lastName?.get(0).toString().toUpperCase()
        }

        view.ivContactEditAvatar.setImageDrawable(TextDrawable.builder().buildRound(letters, generator.getColor(keyId)))

        view.etContactFirstName.addTextChangedListener(DrawableTextWatcher(view.ivContactEditAvatar, keyId, view.etContactFirstName, view.etContactLastName))
        view.etContactLastName.addTextChangedListener(DrawableTextWatcher(view.ivContactEditAvatar, keyId, view.etContactFirstName, view.etContactLastName))

        view.btnContactEditChooseCompany.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean(getString(R.string.EXTRA_COMPANY_IS_LIST_FOR_SELECT), true)
            (activity as ContactEditorActivity).companiesFragment.arguments = bundle
            replaceFragmentListener.replaceFragment((activity as ContactEditorActivity).companiesFragment)
        }

        view.spContactTitle.setItems(resources.getStringArray(R.array.contactTitle_array).toList())
        view.spContactStatus.setItems(resources.getStringArray(R.array.status_array).toList())
        view.spContactPreferredTime.setItems(resources.getStringArray(R.array.contactPreferredTime_array).toList())

        view.etContactFirstName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                view.etContactFirstName.setText(view.etContactFirstName.text.toString().trim())
            }
        }
        view.etContactLastName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                view.etContactLastName.setText(view.etContactLastName.text.toString().trim())
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
        view.etContactDetails.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                view.etContactDetails.setText(view.etContactDetails.text.toString().trim())
            }
        }

        view.spContactTitle.selectedIndex = contact?.title ?: 0
        view.spContactStatus.selectedIndex = contact?.status ?: 0
        view.spContactPreferredTime.selectedIndex = contact?.preferredTime ?: 0
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
            ToastExtension.makeText(requireActivity(), R.string.noCompanyChosenMessage)
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
