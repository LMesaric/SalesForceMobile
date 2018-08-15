package hr.atoscvc.salesforcemobile


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class ContactsFragment : Fragment() {

    companion object {
        const val requestCodeRefresh = 3
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private var fabAddContacts: FloatingActionButton? = null

    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_contacts, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewContacts)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)


        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val contactList = ArrayList<Contact>()

        val adapter = activity?.applicationContext?.let { ContactAdapter(contactList, activity as Activity, false) }
        recyclerView.adapter = adapter

        val query: Query? = mAuth.uid?.let { db.collection("Users").document(it).collection("Contacts").orderBy("firstName").limit(10) }
        query?.addSnapshotListener { p0, p1 ->
            if (p1 != null) {
                Log.d("ERRORS", p1.message)
            }
            if (p0 != null) {
                for (doc in p0.documentChanges) {
                    if (doc.type == DocumentChange.Type.ADDED) {
                        //val contact: Contact = doc.document.toObject(Contact)
                        val newContact = doc.document.toObject<Contact>(Contact::class.java)
                        contactList.add(newContact)
                        adapter?.notifyDataSetChanged()
                    }
                }

            }
        }

        fabAddContacts = activity?.findViewById(R.id.fabAdd)
        fabAddContacts?.visibility = View.VISIBLE
        fabAddContacts?.setOnClickListener {
            val intent = Intent(activity, ContactEditorActivity::class.java).apply {
                putExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), true)
            }
            startActivity(intent)
        }

        
        /*val adapter = ContactAdapter(
                contactList,
                this,
                intent.getBooleanExtra(
                        getString(R.string.EXTRA_CONTACT_IS_LIST_FOR_SELECT),
                        false
                )
        )*/

        return view
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ContactsFragment.requestCodeRefresh) {
            if (resultCode == RESULT_OK) {
                recyclerViewContacts.adapter?.notifyDataSetChanged()
            }
        }
    }*/

}
