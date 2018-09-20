package hr.atoscvc.salesforcemobile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_contacts.*

class ContactsFragment : Fragment(), SearchView.OnQueryTextListener {

    companion object {
        const val requestCodeRefresh = 3
        const val requestItemRefresh = 4
        var contactList = ArrayList<Contact>()
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var mainActivity: MainActivity? = null
    private var companyDetailsActivity: CompanyDetailsActivity? = null

    private var adapter: ContactAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_contacts, container, false)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpContactList()
    }

    private fun setUpContactList() {
        val companyID = arguments?.getString("companyID")

        contactList = ArrayList()

        try {
            mainActivity = activity as MainActivity
            adapter = activity?.applicationContext?.let {
                ContactAdapter(
                        contactList,
                        activity as Activity,
                        arguments
                                ?.getBoolean(getString(R.string.EXTRA_CONTACT_IS_LIST_FOR_SELECT))
                                ?: false,
                        activity as MainActivity
                )
            }
        } catch (e: ClassCastException) {
            try {
                companyDetailsActivity = activity as CompanyDetailsActivity
                adapter = activity?.applicationContext?.let {
                    ContactAdapter(
                            contactList,
                            activity as Activity,
                            arguments
                                    ?.getBoolean(getString(R.string.EXTRA_CONTACT_IS_LIST_FOR_SELECT))
                                    ?: false,
                            activity as CompanyDetailsActivity
                    )
                }
            } catch (e: ClassCastException) {

            }
        }

        recyclerViewContacts.setHasFixedSize(true)
        recyclerViewContacts.layoutManager = LinearLayoutManager(activity)

        recyclerViewContacts.adapter = adapter

        if (companyID == null) {
            activity?.fabAdd?.show()
            activity?.fabAdd?.setOnClickListener {
                val intent = Intent(activity, ContactEditorActivity::class.java).apply {
                    putExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), true)
                }
                activity?.startActivityForResult(intent, requestCodeRefresh)
            }

            db.collection(getString(R.string.databaseCollectionUsers))
                    .document(mAuth.uid!!)
                    .collection(getString(R.string.databaseCollectionContacts))
                    .orderBy(getString(R.string.databaseDocumentLastName))
                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        if (firebaseFirestoreException != null) {
                            Log.d("ERRORS", firebaseFirestoreException.message)
                        }
                        if (querySnapshot != null) {
                            for (doc in querySnapshot.documentChanges) {
                                if (doc.type == DocumentChange.Type.ADDED) {
                                    val newContact = doc.document.toObject<Contact>(Contact::class.java)
                                    contactList.add(newContact)
                                    adapter?.notifyDataSetChanged()
                                }
                            }
                            mainActivity?.searchView?.setQuery(mainActivity?.searchView?.query, true)
                        }
                    }
        } else {
            db.collection(getString(R.string.databaseCollectionUsers))
                    .document(mAuth.uid!!)
                    .collection(getString(R.string.databaseCollectionContacts))
                    .whereEqualTo("company.documentID", companyID)
                    .orderBy(getString(R.string.databaseDocumentLastName))
                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        if (firebaseFirestoreException != null) {
                            Log.d("ERRORS", firebaseFirestoreException.message)
                        }
                        if (querySnapshot != null) {
                            for (doc in querySnapshot.documentChanges) {
                                if (doc.type == DocumentChange.Type.ADDED) {
                                    val newContact = doc.document.toObject<Contact>(Contact::class.java)
                                    contactList.add(newContact)
                                    adapter?.notifyDataSetChanged()
                                }
                            }
                        }
                    }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            setUpContactList()
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        filter(query)
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        filter(query)
        return true
    }

    private fun filter(query: String?) {
        val filteredList = ArrayList<Contact>()
        for (contact: Contact in contactList) {
            if (SearchFilter.satisfiesQuery(query, contact, activity?.applicationContext!!)) {
                filteredList.add(contact)
            }
        }
        adapter?.filter(filteredList)
    }
}
