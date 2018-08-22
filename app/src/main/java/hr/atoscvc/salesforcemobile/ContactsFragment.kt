package hr.atoscvc.salesforcemobile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_contacts.view.*

class ContactsFragment : Fragment() {

    companion object {
        const val requestCodeRefresh = 3
        const val requestItemRefresh = 4
        var contactList = ArrayList<Contact>()
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_contacts, container, false)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        activity?.fabAdd?.visibility = View.VISIBLE
        activity?.fabAdd?.setOnClickListener {
            val intent = Intent(activity, ContactEditorActivity::class.java).apply {
                putExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), true)
            }
            activity?.startActivityForResult(intent, requestCodeRefresh)
        }

        view.recyclerViewContacts.setHasFixedSize(true)
        view.recyclerViewContacts.layoutManager = LinearLayoutManager(activity)

        contactList = ArrayList()

        val adapter = activity?.applicationContext?.let {
            ContactAdapter(
                    contactList,
                    activity as Activity,
                    arguments
                            ?.getBoolean(getString(R.string.EXTRA_CONTACT_IS_LIST_FOR_SELECT))
                            ?: false,
                    activity as MainActivity
            )
        }

        view.recyclerViewContacts.adapter = adapter

        //TODO Bilo bi dosta lako dodati Sort By feature - samo ubaciti string u .orderBy()
        val query: Query? = mAuth.uid?.let {
            db.collection("Users")
                    .document(it)
                    .collection("Contacts")
                    .orderBy("lastName")
        }
        query?.addSnapshotListener { p0, p1 ->
            if (p1 != null) {
                Log.d("ERRORS", p1.message)
            }
            if (p0 != null) {
                for (doc in p0.documentChanges) {
                    if (doc.type == DocumentChange.Type.ADDED) {
                        val newContact = doc.document.toObject<Contact>(Contact::class.java)
                        contactList.add(newContact)
                        adapter?.notifyDataSetChanged()
                    }
                }
            }
        }

        return view
    }
}
