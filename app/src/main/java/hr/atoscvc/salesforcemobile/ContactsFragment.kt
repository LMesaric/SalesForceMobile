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
import com.google.firebase.firestore.Query
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

    private var adapter: ContactAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_contacts, container, false)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        return view
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        val mainActivity = activity as MainActivity

        if (!hidden) {
            activity?.fabAdd?.show()
            activity?.fabAdd?.setOnClickListener {
                val intent = Intent(activity, ContactEditorActivity::class.java).apply {
                    putExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), true)
                }
                activity?.startActivityForResult(intent, requestCodeRefresh)
            }

            recyclerViewContacts.setHasFixedSize(true)
            recyclerViewContacts.layoutManager = LinearLayoutManager(activity)

            contactList = ArrayList()

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

            recyclerViewContacts.adapter = adapter

            //TODO Bilo bi dosta lako dodati Sort By feature - samo ubaciti string u .orderBy()
            val query: Query? = mAuth.uid?.let {
                db.collection(getString(R.string.databaseCollectionUsers))
                        .document(it)
                        .collection(getString(R.string.databaseCollectionContacts))
                        .orderBy(getString(R.string.databaseDocumentLastName))
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
                    mainActivity.searchView.setQuery(mainActivity.searchView.query, true)
                }
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        filter(query)
        return false
    }

    override fun onQueryTextChange(query: String?): Boolean {
        filter(query)
        return false
    }

    private fun filter(query: String?) {
        val filteredList = ArrayList<Contact>()
        for (contact: Contact in contactList) {
            if (SearchFilter.satisfiesQuery(query, contact, false, false, true, false, resources)) {
                filteredList.add(contact)
            }
        }
        adapter?.filter(filteredList)
    }
}
