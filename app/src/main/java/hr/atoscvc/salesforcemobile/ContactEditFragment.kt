package hr.atoscvc.salesforcemobile

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class ContactEditFragment : Fragment() {

    companion object {
        var chosenCompany: Company? = null
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var contact: Contact? = null
    private lateinit var etContactCompanyName: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_contact_edit, container, false)

        val btnChooseCompany: ImageButton = view.findViewById(R.id.btnContactChooseCompany)
        val btnSave: Button = view.findViewById(R.id.btnContactSave)

        val spContactTitle: Spinner = view.findViewById(R.id.spContactTitle)
        val spContactStatus: Spinner = view.findViewById(R.id.spContactStatus)
        val spContactPreferredTime: Spinner = view.findViewById(R.id.spContactPreferredTime)

        val etContactFirstName: EditText = view.findViewById(R.id.etContactFirstName)
        val etContactLastName: EditText = view.findViewById(R.id.etContactLastName)
        val etContactPhone: EditText = view.findViewById(R.id.etContactPhone)
        val etContactEmail: EditText = view.findViewById(R.id.etContactEmail)
        etContactCompanyName = view.findViewById(R.id.etContactCompanyName)
        val etContactDetails: EditText = view.findViewById(R.id.etContactDetails)

        val replaceFragmentListener = activity as ReplaceFragmentListener

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        btnChooseCompany.setOnClickListener {
            //TODO Osim biranja Companyja, trebalo bi omoguciti i kreiranje novog u istom prozoru -> problem vracanja podatka kroz intent
            val companiesFragment = CompaniesFragment()
            val bundle = Bundle()
            bundle.putBoolean("isForSelect", true)
            companiesFragment.arguments = bundle
            replaceFragmentListener.replaceFragment(companiesFragment)
        }

        btnSave.setOnClickListener { _ ->
            //LUKA Prikazati sve errore, a podatke iscitati iz polja na ekranu (osim chosenCompany) i stvoriti objekt u varijabli 'contact'
            if (activity?.intent?.getBooleanExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false) == true) {
                //FILIP Stvara se novi Contact -> save u bazu
                val docRef: DocumentReference? = mAuth.uid?.let { db.collection("Users").document(it).collection("Contacts").document() }
                contact = chosenCompany?.let { Contact(docRef?.id, spContactStatus.selectedItemPosition, spContactTitle.selectedItemPosition, etContactFirstName.text.toString(), etContactLastName.text.toString(), it, etContactPhone.text.toString(), etContactEmail.text.toString(), spContactPreferredTime.selectedItemPosition, etContactDetails.text.toString()) }
                contact?.let { docRef?.set(it) }?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(activity, "New Contact created", Toast.LENGTH_SHORT).show()
                        activity?.setResult(Activity.RESULT_OK)
                        activity?.finish()
                    } else {
                        Toast.makeText(activity, task.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                val documentID = contact?.documentID
                if (documentID.isNullOrBlank()) {
                    Toast.makeText(activity, "Unknown error occurred", Toast.LENGTH_SHORT).show()
                    activity?.finish()
                } else {
                    //FILIP Sprema se edit postojeceg Contacta -> update u bazu
                    Toast.makeText(activity, "Contact updated", Toast.LENGTH_SHORT).show()
                }
            }
        }

        contact = activity?.intent?.getSerializableExtra(getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT)) as? Contact

        if (activity?.intent?.getBooleanExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false) == true) {
            activity?.title = getString(R.string.newContact)
        } else {
            activity?.title = getString(R.string.editContact)
            chosenCompany = contact?.company
        }

        spContactTitle.adapter = ArrayAdapter.createFromResource(
                activity?.baseContext,
                R.array.contactTitle_array,
                R.layout.simple_spinner_dropdown_item
        )

        spContactStatus.adapter = ArrayAdapter.createFromResource(
                activity?.baseContext,
                R.array.status_array,
                R.layout.simple_spinner_dropdown_item
        )

        spContactPreferredTime.adapter = ArrayAdapter.createFromResource(
                activity?.baseContext,
                R.array.contactPreferredTime_array,
                R.layout.simple_spinner_dropdown_item
        )

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

        //TODO Listener for Details

        spContactTitle.setSelection(contact?.title ?: 0)
        spContactStatus.setSelection(contact?.status ?: 0)
        spContactPreferredTime.setSelection(contact?.preferredTime ?: 0)
        etContactFirstName.setText(contact?.firstName)
        etContactLastName.setText(contact?.lastName)
        etContactCompanyName.setText(chosenCompany?.name)
        etContactPhone.setText(contact?.phone)
        etContactEmail.setText(contact?.email)
        etContactDetails.setText(contact?.details)

        return view
    }

    override fun onResume() {
        super.onResume()
        etContactCompanyName.setText(chosenCompany?.name)
    }

}
